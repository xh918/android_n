package com.nexlabs.comnexlabsnanowidget.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.nexlabs.comnexlabsnanowidget.AssetsRecViewAdapter;
import com.nexlabs.comnexlabsnanowidget.CurrencyAdapter;
import com.nexlabs.comnexlabsnanowidget.MainActivity;
import com.nexlabs.comnexlabsnanowidget.R;
import com.nexlabs.comnexlabsnanowidget.currencymanager.ExchangeRates;
import com.nexlabs.comnexlabsnanowidget.currencymanager.FxCurrency;
import com.nexlabs.comnexlabsnanowidget.currencymanager.symbols.CurrencyIdentifier;
import com.nexlabs.comnexlabsnanowidget.currencymanager.symbols.SymbolFactory;
import com.nexlabs.comnexlabsnanowidget.dao.APIaccessDao;
import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;
import com.nexlabs.comnexlabsnanowidget.datapackage.ImgType;
import com.nexlabs.comnexlabsnanowidget.utils.ImageReader;
import com.nexlabs.comnexlabsnanowidget.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


import static com.nexlabs.comnexlabsnanowidget.MainActivity.IDENTIFIER_STANDARD_SELECTION;
import static com.nexlabs.comnexlabsnanowidget.MainActivity.NAME_STANDARD_SELECTION;
import static com.nexlabs.comnexlabsnanowidget.MainActivity.STANDARD_FX_CURRENCY_TICKER;
import static com.nexlabs.comnexlabsnanowidget.MainActivity.SYMBOL_STANDARD_SELECTION;

public class PreferencesFragment extends Fragment {
    RecyclerView prefRecView;
    AssetsRecViewAdapter assetsPrefAdapter;
    SwipeRefreshLayout mSwipeRefresh;

    RelativeLayout mLoadingLayout;
    LinearLayout mMainLayout;

    Spinner mFxCurrencySelector;

    CurrencyAdapter mFxSelectorAdapter;

    SymbolFactory mSymbolFactory;
    ExchangeRates mRates;
    ArrayList<Crypto> mList;
    Collection<FxCurrency> mCurrencies;
    //AssetsRecViewAdapter mAssetsAdapter;

    ActionBar actionBar;

    private FxCurrency mSelectedFxCurrency = null;
    private CurrencyIdentifier mSelectedFxCurrencyIdentifier = null;

    private Crypto mSelectedCrypto = null;
    private Bitmap mCurrentCoinLogo = null;
    private HashSet<String> visibleCryptos = null;

    Handler handler;
    Handler retryHandler;
    private  PreferencesFragment preferencesFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_prefs, container, false);
        handler = new Handler();
        initView(rootView);


       /* prefRecView.setHasFixedSize(true);
        prefRecView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        assetsPrefAdapter = new AssetsRecViewAdapter(getActivity(),
                getActivity().getApplicationContext(),
                R.layout.assets_single_crypto,
                getPreferredFileNodes());
        prefRecView.setAdapter(dataAdapter);*/

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //System.out.println("ONSTART CALLEDDDDDDDD");
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
           queryPrefCoinsData(mSymbolFactory, mSelectedFxCurrencyIdentifier.getTicker(),getActivity());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //System.out.println("PREF ONACTIVITYCREATED");
        Context context = ((AppCompatActivity)getActivity()).getApplicationContext();
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        if(getActivity() != null) {
            if (NetworkUtils.isConnected(getActivity())) {
                loadPrefViewData();
            } else {
                Toast.makeText(getActivity(), R.string.retry_in_15s, Toast.LENGTH_LONG).show();

                retryHandler = new Handler();
                final Runnable updateRunnable = new Runnable() {
                    @Override
                    public void run() {
                        //System.out.println("HANDLER : Checking connection" + (NetworkUtils.isConnected(getActivity())));
                        if (NetworkUtils.isConnected(getActivity())) {
                            loadPrefViewData();
                            retryHandler.removeCallbacks(this);
                        } else {
                            retryHandler.postDelayed(this, 10000);
                        }
                    }
                };
                retryHandler.postDelayed(updateRunnable, 10000);
            }
        }
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getResources().getString(R.string.app_name) + " - " + fileName);
    }

    private void loadPrefViewData(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO
                // FIRST LOAD SHOULD SELECT PREFERRED CURRENCY FROM SHAREDPREFS RATHER THAN DEFAULT USD
                mSymbolFactory = SymbolFactory.getInstance(getActivity().getApplicationContext());
                mRates = ExchangeRates.getInstance(getActivity().getApplicationContext());
                updateCurrencies(mRates);

                mList = queryPrefCoinsData(mSymbolFactory,STANDARD_FX_CURRENCY_TICKER, getActivity());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingLayout.setVisibility(View.GONE);
                        mMainLayout.setVisibility(View.VISIBLE);

                        setupFxCurrencySelector(mRates, mFxCurrencySelector, mSymbolFactory, getActivity());
                        // setStartingselection sets up field mSelectedFxCurrencyIdentifier
                        setStartingSelection(mRates, mFxSelectorAdapter, mFxCurrencySelector, mSymbolFactory, getActivity());
                        setupAssetsAdapter(mList, mSelectedFxCurrencyIdentifier , getActivity());
                        setupRecyclerView(getActivity());
                        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                issueRecyclerUpdate(getActivity(), mSelectedFxCurrencyIdentifier);
                                mSwipeRefresh.setRefreshing(false);
                            }
                        });

                        //System.out.println("SETUP COMPLETED");
                        handler.post(new Runnable() {
                            public void run() {

                                if(getActivity() != null){
                                    ArrayList<Crypto> updatedList =  queryPrefCoinsData(mSymbolFactory, mSelectedFxCurrencyIdentifier.getTicker(), getActivity());
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //System.out.println("HANDLER CODE FROM START");
                                            AssetsRecViewAdapter adapter = (AssetsRecViewAdapter) prefRecView.getAdapter();
                                           /* for(Crypto crypto : updatedList){
                                                System.out.println(crypto.getSymbol() + " BEFOR"  + " "  + crypto.getMarket().getPrice(mSelectedFxCurrency.getTicker()));
                                            }*/
                                            if( adapter == null){
                                                System.out.println("WAS NULLLLLLLLLLLLLLLLLLL prefs");
                                                setupAssetsAdapter(mList, mSelectedFxCurrencyIdentifier , getActivity());
                                            }
                                            mList.clear();
                                            mList.addAll(updatedList);
                                            /*for(Crypto crypto : mList){
                                                System.out.println(crypto.getSymbol() + " mlsit"  + " "  + crypto.getMarket().getPrice(mSelectedFxCurrency.getTicker()));
                                            }*/
                                            //adapter.notifyDataSetChanged();
                                            assetsPrefAdapter.notifyDataSetChanged();
                                            prefRecView.setAdapter(assetsPrefAdapter);
                                        }
                                    });
                                }

                                handler.postDelayed(this, 60000);
                            }
                        });



                    }
                });

            }


        });
        t.start();
    }


    private ArrayList<Crypto> queryPrefCoinsData(SymbolFactory symbolFactory, String fxTicker, Activity activity){

        ArrayList<Crypto> list = APIaccessDao.queryCoins(getPreferredIds(),mSymbolFactory.getCurrencyInfo(fxTicker));

        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.COMMON_FAV_PREFERENCES,
                Context.MODE_PRIVATE);
        for(Crypto c : list){
            try{
                String jsonFormat = prefs.getString(c.getId(), null);
                JSONObject jsonObject = Crypto.toJSONObject(jsonFormat);
                c.setSymbol(Crypto.readSymbol(jsonObject));
                c.setName(Crypto.readName(jsonObject));
                //System.out.println(c.getId() + " " + c.getSymbol() + " " + c.getName());
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        return list;

    }



  /*  private void updateRecycler(){
        assetsPrefAdapter.setUpdatedFileNodeList(getPreferredIds());
        prefRecView.setAdapter(assetsPrefAdapter);
        mSwipeRefresh.setRefreshing(false);
    }*/



    private List<String> getPreferredIds(){
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.COMMON_FAV_PREFERENCES,
                Context.MODE_PRIVATE);
        Set<String> cryptoIdsSet = new LinkedHashSet<>(prefs.getStringSet(MainActivity.FAV_STRINGSET_PREF_KEY, new LinkedHashSet<String>()) );

        List<String > idList = new ArrayList<>();

        for(String id : cryptoIdsSet){
            idList.add(id);
        }


        return idList;
    }

    public void updateCurrencies(ExchangeRates rates){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                rates.updateCurrencies();
            }
        });
        t.start();
        while(t.isAlive()){

        }
        mCurrencies = rates.getCurrencies();
    }

    private void initView(View rootView){
        prefRecView = (RecyclerView ) rootView.findViewById(R.id.assets_recycler_view);
        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);

        mLoadingLayout = (RelativeLayout) rootView.findViewById(R.id.assets_loading_layout);
        mMainLayout = (LinearLayout) rootView.findViewById(R.id.assets_configuration_layout);

        mFxCurrencySelector = (Spinner) rootView.findViewById(R.id.assets_select_fx_currency_spinner);
    }

    public void updateUIPrefs(){
        ArrayList<Crypto> updatedList =  queryPrefCoinsData(mSymbolFactory, mSelectedFxCurrencyIdentifier.getTicker(), getActivity());
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("HANDLER CODE FROM START");
                    AssetsRecViewAdapter adapter = (AssetsRecViewAdapter) prefRecView.getAdapter();
                                           /* for(Crypto crypto : updatedList){
                                                System.out.println(crypto.getSymbol() + " BEFOR"  + " "  + crypto.getMarket().getPrice(mSelectedFxCurrency.getTicker()));
                                            }*/
                    if( adapter == null){
                        //System.out.println("WAS NULLLLLLLLLLLLLLLLLLL prefs");
                        setupAssetsAdapter(mList, mSelectedFxCurrencyIdentifier , getActivity());
                    }
                    mList.clear();
                    mList.addAll(updatedList);
                                            /*for(Crypto crypto : mList){
                                                System.out.println(crypto.getSymbol() + " mlsit"  + " "  + crypto.getMarket().getPrice(mSelectedFxCurrency.getTicker()));
                                            }*/
                    //adapter.notifyDataSetChanged();
                    assetsPrefAdapter.notifyDataSetChanged();
                    prefRecView.setAdapter(assetsPrefAdapter);
                }
            });
        }

    }


    private void setupAssetsAdapter(ArrayList<Crypto> list, CurrencyIdentifier selectedFxCurrency, Activity activity){
        assetsPrefAdapter = new AssetsRecViewAdapter(activity.getApplicationContext(),
                R.layout.assets_single_crypto,
                list, new AssetsRecViewAdapter.OnVHItemClickedListener() {
            @Override
            public void onItemClickedAction(Crypto crypto, Bitmap logo) {
                AssetDetailsFragment assetFragment = AssetDetailsFragment.newInstance(crypto, selectedFxCurrency);
                ((MainActivity) getActivity()).openNewFragment(assetFragment);

                /*setCryptoSelection(crypto, logo);
                String selectedStr = getApplicationContext().getResources().getString(R.string.selected_from_recycler_text);
                Toast.makeText(getApplicationContext(),
                        selectedStr + crypto.getSymbol(),
                        Toast.LENGTH_SHORT).show();*/
                //sendResult(crypto, logo);
            }
        },
                selectedFxCurrency, new AssetsRecViewAdapter.OnPreferenceChangedListener() {
            @Override
            public void onPreferenceChanged(Crypto crypto) {
                updateUIPrefs();
            }
        }, false  );
    }

    private void setupFxCurrencySelector( ExchangeRates rates, Spinner currencySelector, SymbolFactory symbolFactory, Activity activity ){
        mFxSelectorAdapter = new CurrencyAdapter(activity.getApplicationContext(),
                R.layout.spinner_item_currency,
                new ArrayList<String>(rates.getTickers()) );
        mFxSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFxCurrencySelector.setAdapter(mFxSelectorAdapter);

        mFxCurrencySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTicker = (String) parent.getAdapter().getItem(position);
                setFxCurrencySelection(selectedTicker, rates, mFxSelectorAdapter, currencySelector, symbolFactory );
                assetsPrefAdapter.modifyCurrencyIdentifier(mSelectedFxCurrencyIdentifier);
                issueRecyclerUpdate(activity, mSelectedFxCurrencyIdentifier);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private CurrencyIdentifier setStartingSelection(ExchangeRates rates, CurrencyAdapter currencyAdapter, Spinner currencySelector, SymbolFactory symbolFactory, Activity activity){
        CurrencyIdentifier identifier = setFxCurrencySelection(STANDARD_FX_CURRENCY_TICKER, rates, currencyAdapter, currencySelector, symbolFactory);

        mSelectedCrypto = new Crypto(IDENTIFIER_STANDARD_SELECTION,
                SYMBOL_STANDARD_SELECTION,
                NAME_STANDARD_SELECTION);
        //mSelectedTicker = SYMBOL_STANDARD_SELECTION;

        mCurrentCoinLogo = ImageReader.readImage(activity.getApplicationContext(),
                IDENTIFIER_STANDARD_SELECTION,
                ImgType.SMALL);

        setCryptoSelection(mSelectedCrypto, mCurrentCoinLogo);
        return identifier;
    }

    private void setCryptoSelection(Crypto crypto, Bitmap logo){
       /* mSelectedTicker = crypto.getSymbol();
        mSelectedCrypto = crypto;
        mCurrentCoinLogo = logo;
*/
/*
        mCryptoTickerSelected.setText(crypto.getSymbol());
        mLogoViewSelected.setImageBitmap(logo);*/
    }

    private CurrencyIdentifier setFxCurrencySelection(String fxCurrencyTicker, ExchangeRates rates, CurrencyAdapter currencyAdapter, Spinner currencySelector, SymbolFactory symbolFactory ){
        mSelectedFxCurrency = rates.getCurrency(fxCurrencyTicker);
        currencySelector.setSelection( currencyAdapter.getPosition(fxCurrencyTicker) );

        mSelectedFxCurrencyIdentifier = symbolFactory.getCurrencyInfo(fxCurrencyTicker);
        return mSelectedFxCurrencyIdentifier;
/*
        mFxCurrencyTickerSelected.setText(mSelectedFxCurrency.getTicker());
        mFxCurrencySymbolSelected.setText(mSelectedFxCurrencyIdentifier.getSymbol());*/

    }


    // to call after data has been set and views have been initialized
    private void setupRecyclerView(Activity activity){
        if(visibleCryptos == null){
            visibleCryptos = new HashSet<>();
        }
        prefRecView.setHasFixedSize(true);
        prefRecView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));

        prefRecView.setAdapter(assetsPrefAdapter);
    }


    private void issueRecyclerUpdate(Activity activity, CurrencyIdentifier fxCurrency){
        if(NetworkUtils.isConnected(activity)) {
            handler.post(new Runnable() {
                public void run() {
                    ArrayList<Crypto> updatedList = queryPrefCoinsData(mSymbolFactory, fxCurrency.getTicker(), getActivity());

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //System.out.println("HANDLER CODE ISSUER");
                            AssetsRecViewAdapter adapter = (AssetsRecViewAdapter) prefRecView.getAdapter();
                            mList.clear();
                            mList.addAll(updatedList);
                      /*  for(Crypto crypto : mList){
                            System.out.println(crypto.getSymbol() + " issuer"  + " "  + crypto.getMarket().getPrice(mSelectedFxCurrency.getTicker()));
                        }*/
                            adapter.notifyDataSetChanged();

                        }
                    });
                    // each update would create to many postings
                    //handler.postDelayed(this, 15000);
                }
            });
        }else{
            Toast.makeText(activity, R.string.connection_required, Toast.LENGTH_LONG).show();
        }

    }

    private void setPrefsToUpdate(PreferencesFragment preferencesFragment){
        this.preferencesFragment = preferencesFragment;
    }
    public static PreferencesFragment newInstance(){
        PreferencesFragment prefFragment = new PreferencesFragment();
        prefFragment.setPrefsToUpdate(prefFragment);
        return prefFragment;
    }
}
