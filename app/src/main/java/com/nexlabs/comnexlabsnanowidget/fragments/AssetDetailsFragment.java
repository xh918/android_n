package com.nexlabs.comnexlabsnanowidget.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.reflect.TypeToken;
import com.nexlabs.comnexlabsnanowidget.MainActivity;
import com.nexlabs.comnexlabsnanowidget.R;
import com.nexlabs.comnexlabsnanowidget.currencymanager.CurrencyUtils;
import com.nexlabs.comnexlabsnanowidget.currencymanager.symbols.CurrencyIdentifier;
import com.nexlabs.comnexlabsnanowidget.datapackage.AssetInfo;
import com.nexlabs.comnexlabsnanowidget.datapackage.BaseCryptoData;
import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;
import com.nexlabs.comnexlabsnanowidget.datapackage.ImgType;
import com.nexlabs.comnexlabsnanowidget.datapackage.LinkData;
import com.nexlabs.comnexlabsnanowidget.geckoapi.GeckoChartService;
import com.nexlabs.comnexlabsnanowidget.geckoapi.GeckoInfoService;
import com.nexlabs.comnexlabsnanowidget.geckoapi.InfoDeserializer;
import com.nexlabs.comnexlabsnanowidget.geckoapi.MarketChartDeserializer;
import com.nexlabs.comnexlabsnanowidget.geckoapi.Price;
import com.nexlabs.comnexlabsnanowidget.geckoapi.RetrofitClientInstance;
import com.nexlabs.comnexlabsnanowidget.marker.LineMarkerView;
import com.nexlabs.comnexlabsnanowidget.utils.ImageReader;
import com.nexlabs.comnexlabsnanowidget.utils.NetworkUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.nexlabs.comnexlabsnanowidget.utils.ImagePathUtils.getAssetLogoPath;

public class AssetDetailsFragment extends Fragment {

    private static final String CRYPTO_ARG_JSON = "_crypto_json";
    private static final String FXCURRENCY_IDENTIFIER_ARG_JSON = "_fxcurrencyidentifier_json";

    private static final String HAS_SAVED_INSTANCE_STATE_DATA = "has_saved_instance_state_data";
    private static final String CURRENCYIDENTIFIER_STATE_DATA = "currencyidentifier_state_data";
    private static final String CRYPTO_STATE_DATE = "crypto_state_data";
    private static final String FROM_DATE_STATE_DATA_MILLIS= "from_date_state_data";
    private static final String TO_DATE_STATE_DATA_MILLIS = "to_date_state_data";



    private static final long TIME_START = 1500000000;
    private static final long TIME_END = System.currentTimeMillis()/1000;

    private long startUnixTime = 1500000000;
    private long endUnixTime = System.currentTimeMillis()/1000;
    Crypto crypto;
    CurrencyIdentifier fxCurrencyIdentifier;

    CandleStickChart mCandleStickChart;
    LineChart lineChart;
    ArrayList<CandleEntry> mYValsCandleStick;
    CandleDataSet mSet1;
    CandleData mData;
    Legend l;

    Retrofit retrofit;

    ActionBar actionBar;
    TextView infoTotalSupply;
    TextView valueTotalSupply;
    TextView infoCircSupply;
    TextView valueCircSupply;
    TextView infoMarketCap;
    TextView valueMarketCap;
    TextView infoTotalVolume;
    TextView valueTotalVolume;
    TextView infoCodeRepos;
    TextView valueCodeRepos;
    TextView infoDescription;
    TextView valueDescription;

    EditText fromDate;
    EditText toDate;
    CardView selectDates;


    View rootView;

    DatePickerDialog pickerFrom;
    DatePickerDialog pickerTo;

    Date from = new Date(startUnixTime * 1000);
    Date to = new Date(endUnixTime * 1000);


    GeckoChartService chartService;
    GeckoInfoService infoService;

    Handler retryHandler;

    // caller identifier to make args keys unique
    public static AssetDetailsFragment newInstance(Crypto crypto, CurrencyIdentifier selectedFxCurrencyIdentifier){
        AssetDetailsFragment assetFragment = new AssetDetailsFragment();
        Bundle args = new Bundle();
        args.putString(CRYPTO_ARG_JSON, crypto.toJSONbaseData() );
        args.putString(FXCURRENCY_IDENTIFIER_ARG_JSON, selectedFxCurrencyIdentifier.toJSONbaseData());
        assetFragment.setArguments(args);
/*
        Bundle args = new Bundle();
        args.putString(MainActivity.FRAG_ARG_NAME_KEY, fileName);
        args.putString(MainActivity.FRAG_ARG_PATH_KEY, filePath);
        dataFragment.setArguments(args);*/

        return assetFragment;
    }

    private void retrofitInfoSetup( Crypto selectedCrypto, CurrencyIdentifier selectedCurrencyIdentifier){
        GeckoInfoService infoService = RetrofitClientInstance.getRetrofitInstance(new TypeToken<List<Price>>() {}.getType(), new MarketChartDeserializer())
                .create(GeckoInfoService.class);
    }

    private void setupChartService(){
        chartService = RetrofitClientInstance.getRetrofitInstance(new TypeToken<List<Price>>() {}.getType(), new MarketChartDeserializer())
                .create(GeckoChartService.class);
    }
    private void setupInfoService(){
        infoService = RetrofitClientInstance.getRetrofitInstance(new TypeToken<AssetInfo>() {}.getType(), new InfoDeserializer())
                .create(GeckoInfoService.class);
    }
    private void queryChartService(Context applicationContext, Crypto selectedCrypto, CurrencyIdentifier selectedCurrencyIdentifier, long startUnixTime, long endUnixTime){
        Call<List<Price>> prices = chartService.getChartPrices(selectedCrypto.getId(), selectedCurrencyIdentifier.getTicker().toLowerCase(),
                startUnixTime, endUnixTime);
        prices.enqueue(new Callback<List<Price>>() {
            @Override
            public void onResponse(Call<List<Price>> call, Response<List<Price>> response) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
                System.out.println(response.toString());
                System.out.println(response.message());
                List<Price> priceList = response.body();

                /*
                System.out.println("price\tdate");
                for(Price price : priceList) {
                    System.out.println(price.getValue().toString() + " " +
                            formatter.format(new Date(price.getUnixTimeStamp()))
                    );
                }
                System.out.println( "Minutes : " + ((endUnixTime - startUnixTime)/priceList.size())/60 );
                */
                long minutes = ((endUnixTime - startUnixTime)/priceList.size())/60;
                setupLineChart(priceList, minutes, rootView, applicationContext);


            }

            @Override
            public void onFailure(Call<List<Price>> call, Throwable t) {

            }
        });

    }
    private void queryInfoService(Crypto selectedCrypto, boolean hasLocalizedResults, boolean hasTickers, boolean hasMarketData, boolean hasCommunityData, boolean hasDeveloperData, boolean hasSparklineData ){
        Call<AssetInfo> infos = infoService.getCryptoFullDataList(selectedCrypto.getId(), hasLocalizedResults, hasTickers, hasMarketData, hasCommunityData, hasDeveloperData, hasSparklineData);


        infos.enqueue(new Callback<AssetInfo>() {
            @Override
            public void onResponse(Call<AssetInfo> call, Response<AssetInfo> response) {
                AssetInfo assetInfo = response.body();

                BaseCryptoData baseData = assetInfo.getCryptoData();
                LinkData linkData = assetInfo.getLinkData();
                List<String> reposList = linkData.getLinks();

                if( baseData.getTotalSupply() == -1){
                    valueTotalSupply.setText( "unlimited" );
                }else{
                    valueTotalSupply.setText(CurrencyUtils.formatLongValue(baseData.getTotalSupply())  );
                }

                valueCircSupply.setText(CurrencyUtils.formatLongValue(baseData.getCirculatingSupply()));
                valueMarketCap.setText(CurrencyUtils.formatLongValue(baseData.getMarketCapCurrency(fxCurrencyIdentifier.getTicker().toLowerCase())) + " " + fxCurrencyIdentifier.getSymbol());
                valueTotalVolume.setText(CurrencyUtils.formatLongValue(baseData.getTotalVolumeCurrency(fxCurrencyIdentifier.getTicker().toLowerCase())) + " " + fxCurrencyIdentifier.getSymbol() );

                StringBuffer sb = new StringBuffer();
                for(String repo : reposList){
                    sb.append(repo).append("\n");
                }
                valueCodeRepos.setText(new String(sb));


                Locale locale;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    locale = Resources.getSystem().getConfiguration().getLocales().get(0);
                } else {
                    //noinspection deprecation
                    locale = Resources.getSystem().getConfiguration().locale;
                }

                //System.out.println("ASSETINFO " + locale.getLanguage());
                String usedLanguage = locale.getLanguage();

                valueDescription.setText(Html.fromHtml(baseData.getLocalizedDescription(usedLanguage))  );

            }

            @Override
            public void onFailure(Call<AssetInfo> call, Throwable t) {
                //System.out.println("ERROR    " + t.getMessage());
            }
        });
    }


    private void retrofitSetup(View rootView, Context applicationContext, Crypto selectedCrypto, CurrencyIdentifier selectedCurrencyIdentifier){
        GeckoChartService chartService = RetrofitClientInstance.getRetrofitInstance(new TypeToken<List<Price>>() {}.getType(), new MarketChartDeserializer())
                .create(GeckoChartService.class);
        final long startUnixTime = 1400000000;
        final long endUnixTime = 1567195055;

        Call<List<Price>> prices = chartService.getChartPrices(selectedCrypto.getId(), selectedCurrencyIdentifier.getTicker().toLowerCase(),
                startUnixTime, endUnixTime);
        prices.enqueue(new Callback<List<Price>>() {
            @Override
            public void onResponse(Call<List<Price>> call, Response<List<Price>> response) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
                System.out.println(response.toString());
                System.out.println(response.message());
                List<Price> priceList = response.body();

                /*
                System.out.println("price\tdate");
                for(Price price : priceList) {
                    System.out.println(price.getValue().toString() + " " +
                            formatter.format(new Date(price.getUnixTimeStamp()))
                    );
                }
                System.out.println( "Minutes : " + ((endUnixTime - startUnixTime)/priceList.size())/60 );
                */
                long minutes = ((endUnixTime - startUnixTime)/priceList.size())/60;
                setupLineChart(priceList, minutes, rootView, applicationContext);


            }

            @Override
            public void onFailure(Call<List<Price>> call, Throwable t) {

            }
        });


        GeckoInfoService infoService = RetrofitClientInstance.getRetrofitInstance(new TypeToken<AssetInfo>() {}.getType(), new InfoDeserializer())
                .create(GeckoInfoService.class);

        Call<AssetInfo> infos = infoService.getCryptoFullDataList(crypto.getId(), true, true, true, true, true, true);


        infos.enqueue(new Callback<AssetInfo>() {
            @Override
            public void onResponse(Call<AssetInfo> call, Response<AssetInfo> response) {
                AssetInfo assetInfo = response.body();

                BaseCryptoData baseData = assetInfo.getCryptoData();
                LinkData linkData = assetInfo.getLinkData();
                List<String> reposList = linkData.getLinks();

                if( baseData.getTotalSupply() == -1){
                    valueTotalSupply.setText( "unlimited" );
                }else{
                    valueTotalSupply.setText(CurrencyUtils.formatLongValue(baseData.getTotalSupply())  );
                }

                valueCircSupply.setText(CurrencyUtils.formatLongValue(baseData.getCirculatingSupply()));
                valueMarketCap.setText(CurrencyUtils.formatLongValue(baseData.getMarketCapCurrency(fxCurrencyIdentifier.getTicker().toLowerCase())) + " " + fxCurrencyIdentifier.getSymbol());
                valueTotalVolume.setText(CurrencyUtils.formatLongValue(baseData.getTotalVolumeCurrency(fxCurrencyIdentifier.getTicker().toLowerCase())) + " " + fxCurrencyIdentifier.getSymbol() );

                StringBuffer sb = new StringBuffer();
                for(String repo : reposList){
                    sb.append(repo).append("\n");
                }
                valueCodeRepos.setText(new String(sb));


                Locale locale;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    locale = Resources.getSystem().getConfiguration().getLocales().get(0);
                } else {
                    //noinspection deprecation
                    locale = Resources.getSystem().getConfiguration().locale;
                }

                //System.out.println("ASSETINFO " + locale.getLanguage());
                String usedLanguage = locale.getLanguage();

                valueDescription.setText(Html.fromHtml(baseData.getLocalizedDescription(usedLanguage))  );

            }

            @Override
            public void onFailure(Call<AssetInfo> call, Throwable t) {
                //System.out.println("ERROR    " + t.getMessage());
            }
        });


    }

    private void initView(View rootView){
        infoTotalSupply = (TextView) rootView.findViewById(R.id.info_asset_total_supply_tv);
        valueTotalSupply = (TextView) rootView.findViewById(R.id.value_asset_total_supply_tv);
        infoCircSupply = (TextView) rootView.findViewById(R.id.info_asset_circulating_supply_tv);
        valueCircSupply = (TextView) rootView.findViewById(R.id.value_asset_circulating_supply_tv);
        infoMarketCap = (TextView) rootView.findViewById(R.id.info_asset_market_cap_tv);
        valueMarketCap = (TextView) rootView.findViewById(R.id.value_asset_market_cap_tv);
        infoTotalVolume = (TextView) rootView.findViewById(R.id.info_asset_total_volume_tv);
        valueTotalVolume = (TextView) rootView.findViewById(R.id.value_asset_total_volume_tv);
        infoCodeRepos = (TextView) rootView.findViewById(R.id.info_asset_code_repos_tv);
        valueCodeRepos = (TextView) rootView.findViewById(R.id.value_asset_code_repos_tv);
        infoDescription = (TextView) rootView.findViewById(R.id.info_asset_description);
        valueDescription = (TextView) rootView.findViewById(R.id.value_asset_description);

        fromDate = (EditText) rootView.findViewById(R.id.chart_from_date);
        toDate = (EditText) rootView.findViewById(R.id.chart_to_date);
        selectDates = (CardView) rootView.findViewById(R.id.select_dates_btn);

        fromDate.setInputType(InputType.TYPE_NULL);
        toDate.setInputType(InputType.TYPE_NULL);



    }
    @Override
    public void onStop() {
        super.onStop();
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setCustomView(null);
        actionBar.setDisplayShowTitleEnabled(true);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(CURRENCYIDENTIFIER_STATE_DATA, fxCurrencyIdentifier.toJSONbaseData());
        outState.putString(CRYPTO_STATE_DATE, crypto.toJSONbaseData());
        if(from != null && to != null) {
            outState.putLong(FROM_DATE_STATE_DATA_MILLIS, from.getTime());
            outState.putLong(TO_DATE_STATE_DATA_MILLIS, to.getTime());
        }else{
            outState.putLong(FROM_DATE_STATE_DATA_MILLIS, TIME_START);
            outState.putLong(TO_DATE_STATE_DATA_MILLIS, TIME_END);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        crypto = Crypto.fromJSONbaseData(args.getString(CRYPTO_ARG_JSON, null));
        fxCurrencyIdentifier = CurrencyIdentifier.fromJSONbaseData(args.getString(FXCURRENCY_IDENTIFIER_ARG_JSON, null));
        setupChartService();
        setupInfoService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_asset_details, container, false);
        this.rootView = rootView;

        initView(rootView);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //retrofitSetup(rootView, getActivity().getApplicationContext(), crypto, fxCurrencyIdentifier );

        if(savedInstanceState != null) {
            crypto = Crypto.fromJSONbaseData(savedInstanceState.getString(CRYPTO_STATE_DATE));
            fxCurrencyIdentifier = CurrencyIdentifier.fromJSONbaseData(savedInstanceState.getString(CURRENCYIDENTIFIER_STATE_DATA));
            from = new Date(savedInstanceState.getLong(FROM_DATE_STATE_DATA_MILLIS));
            to = new Date(savedInstanceState.getLong(TO_DATE_STATE_DATA_MILLIS));
            startUnixTime = from.getTime() / 1000;
            endUnixTime = to.getTime() / 1000;
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(from);
        int dayOfMonth = calendar2.get(Calendar.DAY_OF_MONTH);
        int monthOfYear = calendar2.get(Calendar.MONTH);
        int year = calendar2.get(Calendar.YEAR);

        fromDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        calendar2.setTime(to);

        dayOfMonth = calendar2.get(Calendar.DAY_OF_MONTH);
        monthOfYear = calendar2.get(Calendar.MONTH);
        year = calendar2.get(Calendar.YEAR);
        toDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);



        Context context = ((AppCompatActivity)getActivity()).getApplicationContext();
        AppCompatActivity activity = ((AppCompatActivity)getActivity());
        if(getActivity() != null && NetworkUtils.isConnected(getActivity())) {
            queryChartService(context, crypto, fxCurrencyIdentifier, startUnixTime, endUnixTime);
            queryInfoService(crypto, true, true, true, true, true, false);
        }else{
            Toast.makeText(getActivity(), R.string.retry_in_15s, Toast.LENGTH_LONG).show();
            retryHandler = new Handler();
            final Runnable updateRunnable = new Runnable() {
                @Override
                public void run() {
                    //System.out.println("HANDLER : Checking connection" + (NetworkUtils.isConnected(getActivity())));
                    if(NetworkUtils.isConnected(getActivity())){
                        queryChartService(context, crypto, fxCurrencyIdentifier, startUnixTime, endUnixTime);
                        queryInfoService(crypto, true, true, true, true, true, false);
                        retryHandler.removeCallbacks(this);
                    }else{
                        retryHandler.postDelayed(this, 10000);
                    }
                }
            };
            retryHandler.postDelayed(updateRunnable, 10000);


        }
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        View actionBarView = LayoutInflater.from( context).inflate(R.layout.asset_actionbar, null);

        TextView nameTv = actionBarView.findViewById(R.id.action_bar_crypto_name);
        ImageView logoView = actionBarView.findViewById(R.id.action_bar_crypto_logo);
        nameTv.setText( crypto.getName()  );

        Bitmap logo = null;
        try{
            logo = BitmapFactory.decodeStream( context.getAssets()
                    .open(getAssetLogoPath(crypto, ImgType.SMALL) ));
            logoView.setImageBitmap(logo);
        }catch(IOException e){
            File imageDir = new File(ImageReader.getImageDirStoragePath(context, ImgType.SMALL));
            if( !imageDir.exists()) {
                imageDir.mkdirs();
            }
            File imageFile = new File(ImageReader.getImageFileStoragePath(context, crypto.getId(), ImgType.SMALL));
            if(imageFile.exists()){
                logo = BitmapFactory.decodeFile(imageFile.getPath());
                logoView.setImageBitmap(logo);
            }else{
                if( getActivity() != null &&   NetworkUtils.isConnected(getActivity())) {
                    GeckoInfoService infoService = RetrofitClientInstance.getRetrofitInstance(new TypeToken<AssetInfo>() {
                    }.getType(), new InfoDeserializer())
                            .create(GeckoInfoService.class);
                    Call<AssetInfo> infos = infoService.getCryptoFullDataList(crypto.getId(), false, false, false, false, false, false);


                    infos.enqueue(new Callback<AssetInfo>() {
                        @Override
                        public void onResponse(Call<AssetInfo> call, Response<AssetInfo> response) {
                            AssetInfo info = response.body();
                            String smallUrl = info.getImageData().getSmallUrl();
                            String cryptoId = info.getCryptoData().getId();
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    URLConnection connection = null;
                                    try {
                                        URL url = new URL(smallUrl);
                                        connection = url.openConnection();
                                        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

                                        try (BufferedInputStream reader = new BufferedInputStream(connection.getInputStream());
                                             BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(imageFile, true))) {
                                            byte arr[] = new byte[1024];


                                            int n = 0;

                                            while ((n = reader.read(arr)) != -1) {
                                                writer.write(arr, 0, n);
                                            }

                                        } catch (IOException e) {

                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            try {
                                t.start();

                                t.join();
                                if (actionBar.getCustomView() != null) {
                                    Bitmap downloadedLogo = BitmapFactory.decodeFile(imageFile.getPath());
                                    logoView.setImageBitmap(downloadedLogo);
                                }
                            } catch (InterruptedException interrExc) {

                            }

                        }

                        @Override
                        public void onFailure(Call<AssetInfo> call, Throwable t) {

                        }
                    });
                }
            }
            e.printStackTrace();
        }

        actionBar.setCustomView(actionBarView);


        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                // date picker dialog
                pickerFrom = new DatePickerDialog( activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.YEAR, year);
                                Date selectedFromDate = new Date(calendar.getTimeInMillis());
                                if ( to == null || selectedFromDate.compareTo(to) < 0  ){
                                    from = selectedFromDate;
                                    fromDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                }else{
                                    Toast.makeText(activity, R.string.from_date_error, Toast.LENGTH_LONG).show();
                                    fromDate.setText("");
                                    from = null;
                                }

                            }
                        }, year, month, day);
                pickerFrom.show();
            }
        });
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                // date picker dialog
                pickerTo = new DatePickerDialog( activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar2 = Calendar.getInstance();
                                calendar2.setTime(new Date());
                                calendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                calendar2.set(Calendar.MONTH, monthOfYear);
                                calendar2.set(Calendar.YEAR, year);
                                Date selectedToDate = new Date(calendar2.getTimeInMillis());

                                if ( from == null || selectedToDate.compareTo(from) > 0  ){
                                    to = selectedToDate;
                                    toDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                                }else{
                                    Toast.makeText(activity, R.string.to_date_error, Toast.LENGTH_LONG).show();
                                    toDate.setText("");
                                    to = null;
                                }

                            }
                        }, year, month, day);
                pickerTo.show();
            }
        });

        selectDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from != null && to != null){
                    if(getActivity() != null && NetworkUtils.isConnected(getContext())) {
                        queryChartService(context, crypto, fxCurrencyIdentifier, from.getTime() / 1000, to.getTime() / 1000);
                    }else{
                        Toast.makeText(getActivity(), R.string.connection_required, Toast.LENGTH_LONG).show();
                    }
                }else{
                    if(from == null && to == null){
                        Toast.makeText(context, R.string.missing_both_dates, Toast.LENGTH_LONG).show();
                    }else if( from == null && to != null){
                        Toast.makeText(context, R.string.missing_from_date, Toast.LENGTH_LONG).show();
                    }else if(from != null && to == null){
                        Toast.makeText(context, R.string.missing_to_date, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }




    public void setupLineChart(List<Price> prices, long minutesUnit, View rootView, Context applicationContext){
        lineChart = rootView.findViewById(R.id.chart);
        lineChart.setTouchEnabled(true);
        lineChart.setNoDataText("Loading data");
        lineChart.setPinchZoom(true);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setScaleEnabled(true);
        LineMarkerView mv = new LineMarkerView(applicationContext, R.layout.custom_marker_view);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);
        renderData(prices, minutesUnit);
    }


    public void renderData(final List<Price> prices, final long minutesUnit) {
        float max = 0;
        List<Entry> priceEntries = new ArrayList<>();
        for(int i = 1 ; i <= prices.size(); i++){
            //float unixTimeStamp = prices.get(i-1).getUnixTimeStamp();
            float price = prices.get(i-1).getValue().floatValue();
            priceEntries.add(new Entry(i, price ));
            if (max < price){
                max = price;
            }
        }
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        /*
        final HashMap<Integer, String> monthMap = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("MMMM");
        SimpleDateFormat numMonthFormat = new SimpleDateFormat("MM");

        // Get last 5 month

        for(int i=-4; i<=0; i++){
            Calendar cal=Calendar.getInstance();
            cal.add(Calendar.MONTH, i);
            Date date = cal.getTime();

            String dateOutput = format.format(date);
            String numMonth= numMonthFormat.format(date);

            monthMap.put(numMonth, dateOutput);
        }
        */
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisMaximum(prices.size());
        xAxis.setAxisMinimum(0f);

        xAxis.setLabelCount(5);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                System.out.println(lineChart.getScaleX());
                int index = ((int)value);
                if(index >= prices.size()){
                    index = prices.size()-1;
                }
                System.out.println(index);
                // less than half day means hours are required to distinguish between two timestamps in same day and year is not required
                if(minutesUnit < 86400/60){
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM HH:mm");
                    return formatter.format(new Date(  prices.get( index).getUnixTimeStamp()  ));
                }else{
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    return formatter.format(new Date(  prices.get( index).getUnixTimeStamp()  ));
                }

            }
        });

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(45);




        //XAxis xAxis = lineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setDrawLimitLinesBehindData(true);

        LimitLine ll1 = new LimitLine(215f, "Maximum Limit");
        ll1.setLineWidth(3f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(70f, "Minimum Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);


        YAxis leftAxis = lineChart.getAxisLeft();
        //leftAxis.removeAllLimitLines();
        //leftAxis.addLimitLine(ll1);
        //leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum( max * 1.2f );
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(true);

        lineChart.getAxisRight().setEnabled(false);
        setData(priceEntries);
    }

    private void setData(List<Entry> values) {

        /*
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(1, 50));
        values.add(new Entry(2, 100));
        values.add(new Entry(3, 80));
        values.add(new Entry(4, 120));
        values.add(new Entry(5, 110));
        values.add(new Entry(7, 150));
        values.add(new Entry(8, 250));
        values.add(new Entry(9, 190));
        */
        LineDataSet set1;
        /*if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {*/
            set1 = new LineDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setDrawValues(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.parseColor("#1B5E20"));
            set1.setLineWidth(1f);

            set1.setColor(Color.parseColor("#1B5E20"));
            set1.setCircleColor(Color.parseColor("#1B5E20"));
            set1.setCircleRadius(1.07f);
            set1.setDrawCircleHole(true);

            set1.setDrawCircles(true);

            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(6f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if( this.getContext() != null){
                if (Utils.getSDKInt() >= 18) {
                    Drawable drawable = ContextCompat.getDrawable(this.getContext(), R.drawable.fade_green);
                    drawable.setAlpha(75);
                    set1.setFillDrawable(drawable);
                } else {
                    set1.setFillColor(Color.DKGRAY);
                }

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);
                LineData data = new LineData(dataSets);
                lineChart.setData(data);
                lineChart.invalidate();
            }


        //}
    }
}
