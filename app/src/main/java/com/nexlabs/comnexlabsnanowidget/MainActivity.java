package com.nexlabs.comnexlabsnanowidget;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.AllMarketTickersEvent;
import com.nexlabs.comnexlabsnanowidget.currencymanager.ExchangeRates;
import com.nexlabs.comnexlabsnanowidget.currencymanager.FxCurrency;
import com.nexlabs.comnexlabsnanowidget.currencymanager.symbols.CurrencyIdentifier;
import com.nexlabs.comnexlabsnanowidget.currencymanager.symbols.SymbolFactory;
import com.nexlabs.comnexlabsnanowidget.dao.APIaccessDao;
import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;
import com.nexlabs.comnexlabsnanowidget.datapackage.ImgType;
import com.nexlabs.comnexlabsnanowidget.datapackage.MarketData;
import com.nexlabs.comnexlabsnanowidget.fragments.BackStackFragment;
import com.nexlabs.comnexlabsnanowidget.fragments.HostFragment;
import com.nexlabs.comnexlabsnanowidget.fragments.MarketFragment;
import com.nexlabs.comnexlabsnanowidget.utils.ImageReader;
import com.nexlabs.comnexlabsnanowidget.utils.NetworkUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    public static final String NEW_CHANGE_NOTIFICATION_PREFERENCES = "com.nexlabs.mainactivity.nexlabs_watch.common_fav_preferences.new_update.jdsaydyuqdyhas217eyFD";
    public static final String COMMON_FAV_PREFERENCES = "com.nexlabs.mainactivity.nexlabs_watch.common_fav_preferences.sndsud19dhftwebdfjdskfwiwefmeaddn83d";
    public static final String FAV_STRINGSET_PREF_KEY = "com.nexlabs.mainactivity.nexlabs_watch.fav_stringset_pref_key.dhsyd124ishdadydadusadwdh28qodijnhff";


    public static final String IDENTIFIER_STANDARD_SELECTION = "bitcoin";
    public static final String SYMBOL_STANDARD_SELECTION = "BTC";
    public static final String NAME_STANDARD_SELECTION = "Bitcoin";



    public static final String STANDARD_FX_CURRENCY_TICKER = "USD";


    public static final String ID_KEY = "appwidget.key.id";
    public static final String TICKER_KEY = "appwidget.key.ticker";

    public static final String FX_TICKER_KEY = "appwidget.key.fx_ticker_key";
    public static final String FX_SYMBOL_KEY = "appwidget.key.fx_symbol_key";


    public static final String PRICE_KEY = "appwidget.key.price";
    public static final String PERCENT_CHANGE_24H_CURRENCY = "appwidget.key.percent_change_24h_currency";


    // suffixes to append to widget id given during onrestore  widget procedure

    public static final String WIDGET_SHARED_PREFS = "com.nexlabs.widgetapp.sharedprefsshill";

    public static final String SUFFIX_ID_SHARED_PREF = "id";
    public static final String SUFFIX_TICKER_SHARED_PREF = "ticker";
    public static final String SUFFIX_FX_TICKER_SHARED_PREF = "fx_ticker";
    public static final String SUFFIX_FX_SYMBOL_SHARED_PREF = "fx_symbol";

    boolean pressedOnceToExit = false;


    Toolbar mToolbar;


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


    private FxCurrency mSelectedFxCurrency = null;
    private CurrencyIdentifier mSelectedFxCurrencyIdentifier = null;



    private Crypto mSelectedCrypto = null;
    private Bitmap mCurrentCoinLogo = null;
    private HashSet<String> visibleCryptos = null;
    private BinanceApiWebSocketClient wsClient;

    private Handler handler;

    boolean settingUp = true;

    public boolean isSettingUp(){
        return settingUp;
    }
    public void setup(){

        this.settingUp = false;
    }



    static class Counter{
        int counter = 0;
        public Counter(){

        }
        public void increment(){
            counter++;
        }
    }

    public void openNewFragment(Fragment fragment) {
        HostFragment hostFragment = (HostFragment) pagerAdapter.getItem(viewPager.getCurrentItem());
        hostFragment.replaceFragment(fragment, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        setContentView(R.layout.activity_main);


        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);

        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.viewpager_tab);

        pagerAdapter = new CryptoPagerAdapter(getSupportFragmentManager());
        tabLayout.setupWithViewPager(viewPager);

       /* for (int i = 0; i < tabLayout.getTabCount(); i++) {
            //noinspection ConstantConditions
            TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_textview,null);
            //tv.setTypeface(Typeface);
            tabLayout.getTabAt(i).setCustomView(tv);
        }*/
        viewPager.setAdapter(pagerAdapter);







        setSupportActionBar(mToolbar);

        /*
        handler = new Handler();
        initView();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO
                    // FIRST LOAD SHOULD SELECT PREFERRED CURRENCY FROM SHAREDPREFS RATHER THAN DEFAULT USD
                    mSymbolFactory = SymbolFactory.getInstance(getApplicationContext());
                    mRates = ExchangeRates.getInstance(getApplicationContext());
                    updateCurrencies(mRates);
                    mList = loadData(mSymbolFactory.getCurrencyInfo(STANDARD_FX_CURRENCY_TICKER));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingLayout.setVisibility(View.GONE);
                            mMainLayout.setVisibility(View.VISIBLE);

                            setupFxCurrencySelector(mRates, mFxCurrencySelector, mSymbolFactory);
                            // setStartingselection sets up field mSelectedFxCurrencyIdentifier
                            setStartingSelection(mRates, mFxSelectorAdapter, mFxCurrencySelector, mSymbolFactory);
                            setupAssetsAdapter(mList, mSelectedFxCurrencyIdentifier );
                            setupRecyclerView();
                            //System.out.println("SETUP COMPLETED");
                            handler.post(new Runnable() {
                                public void run() {
                                    ArrayList<Crypto> updatedList = APIaccessDao.getRankedList(mSelectedFxCurrencyIdentifier);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //System.out.println("HANDLER CODE FROM START");
                                            AssetsRecViewAdapter adapter = (AssetsRecViewAdapter) mAssetsRecView.getAdapter();
                                            for(Crypto crypto : updatedList){
                                                System.out.println(crypto.getSymbol() + " BEFOR"  + " "  + crypto.getMarket().getPrice(mSelectedFxCurrency.getTicker()));
                                            }
                                            mList.clear();
                                            mList.addAll(updatedList);
                                            for(Crypto crypto : mList){
                                                System.out.println(crypto.getSymbol() + " mlsit"  + " "  + crypto.getMarket().getPrice(mSelectedFxCurrency.getTicker()));
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                    handler.postDelayed(this, 15000);
                                }
                            });



                        }
                    });

                }


            });
            t.start();*/






    }

    private void issueRecyclerUpdate(){
        handler.post(new Runnable() {
            public void run() {
                ArrayList<Crypto> updatedList = APIaccessDao.getRankedList(mSelectedFxCurrencyIdentifier);

                runOnUiThread(new Runnable() {
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

    }
    /*
    private void setupAssetsAdapter(ArrayList<Crypto> list, CurrencyIdentifier selectedFxCurrency){
        mAssetsAdapter = new AssetsRecViewAdapter(getApplicationContext(),
                R.layout.assets_single_crypto,
                list, new AssetsRecViewAdapter.OnVHItemClickedListener() {
            @Override
            public void onItemClickedAction(Crypto crypto, Bitmap logo) {
                /*setCryptoSelection(crypto, logo);
                String selectedStr = getApplicationContext().getResources().getString(R.string.selected_from_recycler_text);
                Toast.makeText(getApplicationContext(),
                        selectedStr + crypto.getSymbol(),
                        Toast.LENGTH_SHORT).show();*/
                //sendResult(crypto, logo);
            //}
       // },
     //           selectedFxCurrency);
    //}



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

    private void setupFxCurrencySelector( ExchangeRates rates, Spinner currencySelector, SymbolFactory symbolFactory ){
        mFxSelectorAdapter = new CurrencyAdapter(getApplicationContext(),
                R.layout.spinner_item_currency,
                new ArrayList<String>(rates.getTickers()) );
        mFxSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFxCurrencySelector.setAdapter(mFxSelectorAdapter);

        mFxCurrencySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTicker = (String) parent.getAdapter().getItem(position);
                setFxCurrencySelection(selectedTicker, rates, mFxSelectorAdapter, currencySelector, symbolFactory );
                mAssetsAdapter.modifyCurrencyIdentifier(mSelectedFxCurrencyIdentifier);
                issueRecyclerUpdate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private CurrencyIdentifier setStartingSelection(ExchangeRates rates, CurrencyAdapter currencyAdapter, Spinner currencySelector, SymbolFactory symbolFactory){
        CurrencyIdentifier identifier = setFxCurrencySelection(STANDARD_FX_CURRENCY_TICKER, rates, currencyAdapter, currencySelector, symbolFactory);

        mSelectedCrypto = new Crypto(IDENTIFIER_STANDARD_SELECTION,
                SYMBOL_STANDARD_SELECTION,
                NAME_STANDARD_SELECTION);
        //mSelectedTicker = SYMBOL_STANDARD_SELECTION;

        mCurrentCoinLogo = ImageReader.readImage(getApplicationContext(),
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
    private void setupRecyclerView(){
        if(visibleCryptos == null){
            visibleCryptos = new HashSet<>();
        }
        mAssetsRecView.setHasFixedSize(true);
        mAssetsRecView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mAssetsRecView.setAdapter(mAssetsAdapter);
    }
    private ArrayList<Crypto> loadData(CurrencyIdentifier fxCurrencyIdentifier){
        return  APIaccessDao.getRankedList(fxCurrencyIdentifier);
    }

    private void loadData(ArrayList<Crypto> list){
        APIaccessDao.addToRankedList(list);
    }

    private void initView(){
        mLoadingLayout = (RelativeLayout) findViewById(R.id.assets_loading_layout);
        mMainLayout = (LinearLayout) findViewById(R.id.assets_configuration_layout);
        mAssetsRecView = (RecyclerView) findViewById(R.id.assets_recycler_view);
        mFxCurrencySelector = (Spinner) findViewById(R.id.assets_select_fx_currency_spinner);
    }





    /*
    @Override
    public void onBackPressed()
    {
        if(!BackStackFragment.handleBackPressed(getSupportFragmentManager())){
            super.onBackPressed();
        }
    }
    */
    @Override
    public void onBackPressed(){
        if( !BackStackFragment.handleBackPressed(getSupportFragmentManager()) ){

            if(pressedOnceToExit){
                super.onBackPressed();
                return;
            }
            this.pressedOnceToExit = true;
            Toast.makeText(this, R.string.press_back_again_exit, Toast.LENGTH_SHORT)
                    .show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pressedOnceToExit = false;
                }
            }, 2000);
        }
    }



}
