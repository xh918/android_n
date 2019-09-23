package com.nexlabs.comnexlabsnanowidget.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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

import com.binance.api.client.BinanceApiWebSocketClient;
import com.nexlabs.comnexlabsnanowidget.AssetsRecViewAdapter;
import com.nexlabs.comnexlabsnanowidget.CryptoPagerAdapter;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static com.nexlabs.comnexlabsnanowidget.MainActivity.COMMON_FAV_PREFERENCES;
import static com.nexlabs.comnexlabsnanowidget.MainActivity.IDENTIFIER_STANDARD_SELECTION;
import static com.nexlabs.comnexlabsnanowidget.MainActivity.NAME_STANDARD_SELECTION;
import static com.nexlabs.comnexlabsnanowidget.MainActivity.STANDARD_FX_CURRENCY_TICKER;
import static com.nexlabs.comnexlabsnanowidget.MainActivity.SYMBOL_STANDARD_SELECTION;


public class MarketFragment extends Fragment {


    private RecyclerView dataView;
    RelativeLayout mLoadingLayout;
    LinearLayout mMainLayout;
    RecyclerView mAssetsRecView;

    Spinner mFxCurrencySelector;

    CurrencyAdapter mFxSelectorAdapter;


    SymbolFactory mSymbolFactory;
    ExchangeRates mRates;
    ArrayList<Crypto> mList;
    Collection<FxCurrency> mCurrencies;
    AssetsRecViewAdapter mAssetsAdapter;

    ViewPager viewPager;
    TabLayout tabLayout;
    CryptoPagerAdapter pagerAdapter;

    private static final String SERIALIZED_FX_CURRENCY_IDENTIFIER_SAVED = "com.nexlabs.marketfragment.saved_serializedfxcurrencyidentifier";
    private static final String SERIALIZED_FX_CURRENCY_SAVED = "com.nexlabs.marketfragment.saved_serializedfxcurrency";


    private FxCurrency mSelectedFxCurrency = null;
    private CurrencyIdentifier mSelectedFxCurrencyIdentifier = null;



    private Crypto mSelectedCrypto = null;
    private Bitmap mCurrentCoinLogo = null;
    private HashSet<String> visibleCryptos = null;
    private BinanceApiWebSocketClient wsClient;

    private Handler handler;

    ActionBar actionBar;

    PreferencesFragment preferencesFragment;

    Handler retryHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_market, container, false);

        initView(rootView);



        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        Activity activity = getActivity();
        saveSelection(activity, mSelectedFxCurrencyIdentifier, mSelectedFxCurrency);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = ((AppCompatActivity)getActivity()).getApplicationContext();
        Activity activity = getActivity();
        readSavedSelection(activity);

        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        //System.out.println("Checking connection" + (getActivity() == null));
        if(getActivity() != null){

            if(NetworkUtils.isConnected(getActivity())){
                loadMainViewData();
            }else{
                //System.out.println("Checking connection" + (NetworkUtils.isConnected(getActivity())));
                Toast.makeText(getActivity().getApplicationContext(), R.string.retry_in_15s, Toast.LENGTH_LONG).show();
                retryHandler = new Handler();
                final Runnable updateRunnable = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("HANDLER : Checking connection" + (NetworkUtils.isConnected(getActivity())));
                        if(NetworkUtils.isConnected(getActivity())){
                            loadMainViewData();
                            retryHandler.removeCallbacks(this);
                        }else{
                            retryHandler.postDelayed(this, 10000);
                        }
                    }
                };
                retryHandler.postDelayed(updateRunnable, 10000);

            }
        }


        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getResources().getString(R.string.app_name) + " - " + fileName);
    }

    private void loadMainViewData(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO
                // FIRST LOAD SHOULD SELECT PREFERRED CURRENCY FROM SHAREDPREFS RATHER THAN DEFAULT USD
                mSymbolFactory = SymbolFactory.getInstance(getActivity().getApplicationContext());
                mRates = ExchangeRates.getInstance(getActivity().getApplicationContext());
                updateCurrencies(mRates);



                mList = loadData(mSymbolFactory.getCurrencyInfo(STANDARD_FX_CURRENCY_TICKER));
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
                        //System.out.println("SETUP COMPLETED");
                        handler.post(new Runnable() {
                            public void run() {
                                ArrayList<Crypto> updatedList = APIaccessDao.getRankedList(mSelectedFxCurrencyIdentifier);
                                if(updatedList.size() > 0) {
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //System.out.println("HANDLER CODE FROM START");
                                                AssetsRecViewAdapter adapter = (AssetsRecViewAdapter) mAssetsRecView.getAdapter();
                                                if (adapter == null) {
                                                    setupAssetsAdapter(mList, mSelectedFxCurrencyIdentifier, getActivity());
                                                }
                                           /* for(Crypto crypto : updatedList){
                                                System.out.println(crypto.getSymbol() + " BEFOR"  + " "  + crypto.getMarket().getPrice(mSelectedFxCurrency.getTicker()));
                                            }*/
                                                mList.clear();
                                                mList.addAll(updatedList);
                                            /*for(Crypto crypto : mList){
                                                System.out.println(crypto.getSymbol() + " mlsit"  + " "  + crypto.getMarket().getPrice(mSelectedFxCurrency.getTicker()));
                                            }*/
                                                //adapter.notifyDataSetChanged();
                                                mAssetsAdapter.notifyDataSetChanged();
                                                mAssetsRecView.setAdapter(mAssetsAdapter);
                                                if(  ((MainActivity)getActivity()).isSettingUp()){
                                                    ((MainActivity)getActivity()).setup();
                                                }
                                            }
                                        });
                                    }
                                }else{
                                    if(getActivity() != null) {
                                        Toast.makeText(getActivity(), R.string.server_error, Toast.LENGTH_LONG).show();
                                    }
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

    private void initView(View rootView){
        mLoadingLayout = (RelativeLayout) rootView.findViewById(R.id.assets_loading_layout);
        mMainLayout = (LinearLayout) rootView.findViewById(R.id.assets_configuration_layout);
        mAssetsRecView = (RecyclerView) rootView.findViewById(R.id.assets_recycler_view);
        mFxCurrencySelector = (Spinner) rootView.findViewById(R.id.assets_select_fx_currency_spinner);
    }

    private void issueRecyclerUpdate(Activity activity){
        if(NetworkUtils.isConnected(activity)) {
            handler.post(new Runnable() {
                public void run() {
                    ArrayList<Crypto> updatedList = APIaccessDao.getRankedList(mSelectedFxCurrencyIdentifier);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //System.out.println("HANDLER CODE ISSUER");
                            AssetsRecViewAdapter adapter = (AssetsRecViewAdapter) mAssetsRecView.getAdapter();
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
    private void setupAssetsAdapter(ArrayList<Crypto> list, CurrencyIdentifier selectedFxCurrencyIdentifier, Activity activity){
        mAssetsAdapter = new AssetsRecViewAdapter(activity.getApplicationContext(),
                R.layout.assets_single_crypto,
                list, new AssetsRecViewAdapter.OnVHItemClickedListener() {
            @Override
            public void onItemClickedAction(Crypto crypto, Bitmap logo) {
                AssetDetailsFragment assetFragment = AssetDetailsFragment.newInstance(crypto, selectedFxCurrencyIdentifier);
                ((MainActivity) getActivity()).openNewFragment(assetFragment);
                /*setCryptoSelection(crypto, logo);
                String selectedStr = getApplicationContext().getResources().getString(R.string.selected_from_recycler_text);
                Toast.makeText(getApplicationContext(),
                        selectedStr + crypto.getSymbol(),
                        Toast.LENGTH_SHORT).show();*/
                //sendResult(crypto, logo);
            }
        },
                selectedFxCurrencyIdentifier, new AssetsRecViewAdapter.OnPreferenceChangedListener() {
            @Override
            public void onPreferenceChanged(Crypto crypto) {
                preferencesFragment.updateUIPrefs();
            }
        }, true);
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

                setFxCurrencySelection(activity, selectedTicker, rates, mFxSelectorAdapter, currencySelector, symbolFactory );
                mAssetsAdapter.modifyCurrencyIdentifier(mSelectedFxCurrencyIdentifier);
                issueRecyclerUpdate(activity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private CurrencyIdentifier setStartingSelection(ExchangeRates rates, CurrencyAdapter currencyAdapter, Spinner currencySelector, SymbolFactory symbolFactory, Activity activity){
        CurrencyIdentifier identifier = setFxCurrencySelection(activity, STANDARD_FX_CURRENCY_TICKER, rates, currencyAdapter, currencySelector, symbolFactory);

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

    private CurrencyIdentifier setFxCurrencySelection(Activity activity, String fxCurrencyTicker, ExchangeRates rates, CurrencyAdapter currencyAdapter, Spinner currencySelector, SymbolFactory symbolFactory ){
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
        mAssetsRecView.setHasFixedSize(true);
        mAssetsRecView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));

        mAssetsRecView.setAdapter(mAssetsAdapter);

    }
    private ArrayList<Crypto> loadData(CurrencyIdentifier fxCurrencyIdentifier){
        return  APIaccessDao.getRankedList(fxCurrencyIdentifier);
    }

    private void loadData(ArrayList<Crypto> list){
        APIaccessDao.addToRankedList(list);
    }


    // this will be the fragment which will be updated when changes to preferences happen
    // --------> will be substituted with proper lifecycle call support
    private void setPrefsToUpdate(PreferencesFragment preferencesFragment){
        this.preferencesFragment = preferencesFragment;
    }

    public static MarketFragment newInstance(PreferencesFragment prefsToUpdate){
        MarketFragment marketFragment = new MarketFragment();
        marketFragment.setPrefsToUpdate(prefsToUpdate);
/*
        Bundle args = new Bundle();
        args.putString(MainActivity.FRAG_ARG_NAME_KEY, fileName);
        args.putString(MainActivity.FRAG_ARG_PATH_KEY, filePath);
        dataFragment.setArguments(args);*/

        return marketFragment;
    }


    private static void saveSelection(Activity activity, CurrencyIdentifier selectedIdentifier, FxCurrency selectedFxCurrency){
        if(activity != null){
            Context context = activity.getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(MainActivity.COMMON_FAV_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(SERIALIZED_FX_CURRENCY_IDENTIFIER_SAVED, selectedIdentifier.toJSONbaseData());
            editor.putString(SERIALIZED_FX_CURRENCY_SAVED, selectedFxCurrency.toJSONbaseData());
            editor.apply();


        }
    }
    private static void readSavedSelection(Activity activity){
        if(activity != null){
            Context context = activity.getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(MainActivity.COMMON_FAV_PREFERENCES, Context.MODE_PRIVATE);
            CurrencyIdentifier currencyIdentifier;
            FxCurrency fxCurrency;
            if(prefs.contains(SERIALIZED_FX_CURRENCY_IDENTIFIER_SAVED)){
                currencyIdentifier = CurrencyIdentifier.fromJSONbaseData(prefs.getString(SERIALIZED_FX_CURRENCY_IDENTIFIER_SAVED, null ));
            }
            if(prefs.contains(SERIALIZED_FX_CURRENCY_SAVED)){
                fxCurrency = FxCurrency.fromJSONbaseData(prefs.getString(SERIALIZED_FX_CURRENCY_SAVED, null));
            }

        }
    }





}
