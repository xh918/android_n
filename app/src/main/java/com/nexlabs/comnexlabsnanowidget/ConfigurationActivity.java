package com.nexlabs.comnexlabsnanowidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nexlabs.comnexlabsnanowidget.currencymanager.ExchangeRates;
import com.nexlabs.comnexlabsnanowidget.currencymanager.FxCurrency;
import com.nexlabs.comnexlabsnanowidget.currencymanager.symbols.CurrencyIdentifier;
import com.nexlabs.comnexlabsnanowidget.currencymanager.symbols.SymbolFactory;
import com.nexlabs.comnexlabsnanowidget.dao.APIaccessDao;
import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;
import com.nexlabs.comnexlabsnanowidget.datapackage.ImgType;
import com.nexlabs.comnexlabsnanowidget.datapackage.MarketData;
import com.nexlabs.comnexlabsnanowidget.utils.ImageReader;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.CRC32;

public class ConfigurationActivity extends AppCompatActivity {


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



    int mAppWidgetId;
    RelativeLayout mLoadingLayout;
    LinearLayout mMainLayout;
    RecyclerView mCryptoRecView;
    ImageView mLogoViewSelected;
    TextView mCryptoTickerSelected;
    TextView mFxCurrencySymbolSelected;
    TextView mFxCurrencyTickerSelected;
    Spinner mFxCurrencySelector;
    TextView mConfirmConfigurationButton;

    CurrencyAdapter mFxSelectorAdapter;


    SymbolFactory mSymbolFactory;
    ExchangeRates mRates;
    ArrayList<Crypto> mList;
    Collection<FxCurrency> mCurrencies;


    private FxCurrency mSelectedFxCurrency = null;
    private CurrencyIdentifier mSelectedFxCurrencyIdentifier = null;

    private Crypto mSelectedCrypto = null;
    private Bitmap mCurrentCoinLogo = null;
    private String mSelectedTicker = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);


        // received configuration intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        initView();


        //setupModel();
        //loadData(mList);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mSymbolFactory = SymbolFactory.getInstance(getApplicationContext());
                mRates = ExchangeRates.getInstance(getApplicationContext());
                updateCurrencies();
                mList = loadData(mSymbolFactory.getCurrencyInfo(STANDARD_FX_CURRENCY_TICKER));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingLayout.setVisibility(View.GONE);
                        mMainLayout.setVisibility(View.VISIBLE);
                        setupRecyclerView();
                        setupFxCurrencySelector();
                        setStartingSelection();
                    }
                });
            }
        });
        t.start();







        long timeStart, timeEnd;

      /*  mList = loadData();
        mSymbolFactory = SymbolFactory.getInstance(getApplicationContext());
        updateCurrencies();*/

        //mSymbolFactory = SymbolFactory.getInstance(getApplicationContext());

        //mRates = ExchangeRates.getInstance(getApplicationContext());

        //updateCurrencies();


    }

    public void setupModel(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mSymbolFactory = SymbolFactory.getInstance(getApplicationContext());
                mRates = ExchangeRates.getInstance(getApplicationContext());
                updateCurrencies();
                mList = loadData(mSymbolFactory.getCurrencyInfo(STANDARD_FX_CURRENCY_TICKER));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setupRecyclerView();
                        setupFxCurrencySelector();
                        setStartingSelection();
                    }
                });
            }
        });
        t.start();


    }

    public void logTime(String logString, long timeStart, long timeEnd){
        System.out.println(logString + " " + (timeEnd - timeStart)  );
    }



    public void updateCurrencies(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mRates.updateCurrencies();
            }
        });
        t.start();
        while(t.isAlive()){

        }
        mCurrencies = mRates.getCurrencies();


    }

    private void setupFxCurrencySelector(){
        mFxSelectorAdapter = new CurrencyAdapter(getApplicationContext(),
                                            R.layout.spinner_item_currency,
                                        new ArrayList<String>(mRates.getTickers()) );
        mFxSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFxCurrencySelector.setAdapter(mFxSelectorAdapter);

        mFxCurrencySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getAdapter().getItem(position);
                setFxCurrencySelection(selected);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void setStartingSelection(){
        setFxCurrencySelection(STANDARD_FX_CURRENCY_TICKER);

        mSelectedCrypto = new Crypto(IDENTIFIER_STANDARD_SELECTION,
                                    SYMBOL_STANDARD_SELECTION,
                                    NAME_STANDARD_SELECTION);
        mSelectedTicker = SYMBOL_STANDARD_SELECTION;

        mCurrentCoinLogo = ImageReader.readImage(getApplicationContext(),
                                                IDENTIFIER_STANDARD_SELECTION,
                                                ImgType.SMALL);

        setCryptoSelection(mSelectedCrypto, mCurrentCoinLogo);
    }

    private void setCryptoSelection(Crypto crypto, Bitmap logo){
        mSelectedTicker = crypto.getSymbol();
        mSelectedCrypto = crypto;
        mCurrentCoinLogo = logo;


        mCryptoTickerSelected.setText(crypto.getSymbol());
        mLogoViewSelected.setImageBitmap(logo);
    }

    private void setFxCurrencySelection(String fxCurrencyTicker){
        mSelectedFxCurrency = mRates.getCurrency(fxCurrencyTicker);
        mFxCurrencySelector.setSelection( mFxSelectorAdapter.getPosition(fxCurrencyTicker) );

        mSelectedFxCurrencyIdentifier = mSymbolFactory.getCurrencyInfo(fxCurrencyTicker);

        mFxCurrencyTickerSelected.setText(mSelectedFxCurrency.getTicker());
        mFxCurrencySymbolSelected.setText(mSelectedFxCurrencyIdentifier.getSymbol());

    }


    // to call after data has been set and views have been initialized
    private void setupRecyclerView(){
        mCryptoRecView.setHasFixedSize(true);
        mCryptoRecView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        CryptoRecViewAdapter adapter = new CryptoRecViewAdapter(getApplicationContext(),
                R.layout.list_item,
                mList, new CryptoRecViewAdapter.OnVHItemClickedListener() {
            @Override
            public void onItemClickedAction(Crypto crypto, Bitmap logo) {
                setCryptoSelection(crypto, logo);
                String selectedStr = getApplicationContext().getResources().getString(R.string.selected_from_recycler_text);
                Toast.makeText(getApplicationContext(),
                                selectedStr + crypto.getSymbol(),
                                Toast.LENGTH_SHORT).show();
                //sendResult(crypto, logo);
            }
        });
        mCryptoRecView.setAdapter(adapter);
    }
    private ArrayList<Crypto> loadData(CurrencyIdentifier fxCurrencyIdentifier){
        return  APIaccessDao.getRankedList(fxCurrencyIdentifier);
    }

    private void loadData(ArrayList<Crypto> list){
        APIaccessDao.addToRankedList(list);
    }

    private void initView(){
        mLoadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        mMainLayout = (LinearLayout) findViewById(R.id.configuration_layout);
        mCryptoRecView = (RecyclerView) findViewById(R.id.main_recycler_view);
        mLogoViewSelected = (ImageView) findViewById(R.id.logo_view_selected);
        mCryptoTickerSelected = (TextView) findViewById(R.id.crypto_ticker_tv_selected);
        mFxCurrencySymbolSelected = (TextView) findViewById(R.id.fx_currency_symbol_tv_selected);
        mFxCurrencyTickerSelected = (TextView) findViewById(R.id.fx_currency_ticker_tv_selected);
        mFxCurrencySelector = (Spinner) findViewById(R.id.select_fx_currency_spinner);
        mConfirmConfigurationButton = (TextView) findViewById(R.id.confirm_btn);

        mConfirmConfigurationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(mSelectedCrypto, mSelectedFxCurrency, mSelectedFxCurrencyIdentifier, mCurrentCoinLogo );
            }
        });

    }



    private void sendResult(Crypto crypto, FxCurrency fxCurrency, CurrencyIdentifier fxIdentifiers, Bitmap logo){
        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());


        RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(),
                                            R.layout.widget_single_crypto);

       /* MarketData data = APIaccessDao.getMarketData(crypto.getId());

        views.setImageViewBitmap(R.id.logo_view, logo);
        views.setTextViewText(R.id.ticker_tv, crypto.getSymbol());
        views.setTextViewText(R.id.price_tv, crypto);

        manager.updateAppWidget(mAppWidgetId, views);*/


        MarketData data = APIaccessDao.getMarketData(crypto.getId());

        BigDecimal price = data.getPrice(mSelectedFxCurrency.getTicker().toLowerCase());
        BigDecimal change = data.getPriceChangePercentage24h(mSelectedFxCurrency.getTicker().toLowerCase());
        DecimalFormat df = new DecimalFormat("#,##0.000");
        String priceStr = df.format(price) + mSelectedFxCurrencyIdentifier.getSymbol();
        DecimalFormat dfchange = new DecimalFormat("#,##0.00");
        String changeStr = dfchange.format(change) + " %";




        views.setImageViewBitmap(R.id.logo_view, logo);
        views.setTextViewText(R.id.ticker_tv, crypto.getSymbol().toUpperCase());
        views.setTextViewText(R.id.price_tv,  priceStr );
        views.setTextViewText(R.id.percentage_24h_tv, changeStr );

        int resourceColor = R.color.green_pump;
        if(change.signum() == -1){
            resourceColor = R.color.red_dump;
        }

        views.setTextColor(R.id.percentage_24h_tv, getApplicationContext().getResources().getColor(resourceColor) );

        Intent update = new Intent(getApplicationContext(), CryptoWidgetProvider.class);

        update.setAction(CryptoWidgetProvider.ACTION_UPDATE_CRYPTO);
        Bundle b = new Bundle();
        b.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId );
        update.putExtras(b);
        //update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

        PendingIntent pendingUpdate =  PendingIntent.getBroadcast(getApplicationContext(), mAppWidgetId, update, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.refresh_btn, pendingUpdate);

        Bundle args = manager.getAppWidgetOptions(mAppWidgetId);
        args.putString(ID_KEY, crypto.getId());
        args.putString(TICKER_KEY, crypto.getSymbol());

        args.putString(FX_TICKER_KEY, fxCurrency.getTicker());
        args.putString(FX_SYMBOL_KEY, fxIdentifiers.getSymbol());


        manager.updateAppWidgetOptions(mAppWidgetId, args);
        manager.updateAppWidget(mAppWidgetId, views);

        //System.out.println("CONFIG_ACTIVITY " +  manager.getAppWidgetOptions(mAppWidgetId).getString(ID_KEY)  + " " + mAppWidgetId);
        saveWidgetData();

        Intent result = new Intent();
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, result);
        finish();
    }



    private void saveWidgetData(){
        SharedPreferences prefs = getSharedPreferences(WIDGET_SHARED_PREFS ,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(mAppWidgetId +SUFFIX_ID_SHARED_PREF, mSelectedCrypto.getId());
        editor.putString(mAppWidgetId + SUFFIX_TICKER_SHARED_PREF, mSelectedCrypto.getSymbol());
        editor.putString(mAppWidgetId + SUFFIX_FX_TICKER_SHARED_PREF, mSelectedFxCurrency.getTicker());
        editor.putString(mAppWidgetId + SUFFIX_FX_SYMBOL_SHARED_PREF, mSelectedFxCurrencyIdentifier.getSymbol());
        editor.commit();


    }



}
