package com.nexlabs.comnexlabsnanowidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.UnicodeSetSpanner;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Display;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.nexlabs.comnexlabsnanowidget.dao.APIaccessDao;
import com.nexlabs.comnexlabsnanowidget.datapackage.AssetInfo;
import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;
import com.nexlabs.comnexlabsnanowidget.datapackage.ImgType;
import com.nexlabs.comnexlabsnanowidget.datapackage.MarketData;
import com.nexlabs.comnexlabsnanowidget.geckoapi.GeckoInfoService;
import com.nexlabs.comnexlabsnanowidget.geckoapi.InfoDeserializer;
import com.nexlabs.comnexlabsnanowidget.geckoapi.RetrofitClientInstance;
import com.nexlabs.comnexlabsnanowidget.utils.ImageReader;
import com.nexlabs.comnexlabsnanowidget.utils.KeyGeneratorWidgetPrefs;
import static com.nexlabs.comnexlabsnanowidget.ConfigurationActivity.*;
import static com.nexlabs.comnexlabsnanowidget.utils.ImagePathUtils.getAssetLogoPath;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.security.Key;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CryptoWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_UPDATE_CRYPTO = "automaticCryptoUpdate";

    private String mId;
    private String mTicker;
    private String mFxTicker;
    private String mFxSymbol;

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        for(int i = 0; i < oldWidgetIds.length; i++){
            System.out.println("Called with id " + oldWidgetIds[i] + " " + newWidgetIds[i]);
            restoreWidgetData(context, oldWidgetIds[i]);

            saveWithNewWidgetIdentifier(context, oldWidgetIds[i], newWidgetIds[i]);
        }



        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    private void restoreWidgetData(Context context, int widgetId){
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

       /* Bundle options = manager.getAppWidgetOptions(widgetId);
        if(options != null){
            mId = options.getString(ConfigurationActivity.ID_KEY, "bitcoin" );
            mTicker = options.getString(ConfigurationActivity.TICKER_KEY, "BTC");
            mFxTicker = options.getString(ConfigurationActivity.FX_TICKER_KEY, "USD");
            mFxSymbol = options.getString(ConfigurationActivity.FX_SYMBOL_KEY, "$");
        }else{*/
            SharedPreferences prefs = context.getSharedPreferences(ConfigurationActivity.WIDGET_SHARED_PREFS, context.MODE_PRIVATE);

            //String cryptoId
            mId = prefs.getString(KeyGeneratorWidgetPrefs.getKey(widgetId, SUFFIX_ID_SHARED_PREF ),
                    KeyGeneratorWidgetPrefs.IDENTIFIER_STANDARD_SELECTION);

            //String cryptoTicker
            mTicker = prefs.getString(KeyGeneratorWidgetPrefs.getKey(widgetId, SUFFIX_TICKER_SHARED_PREF),
                    KeyGeneratorWidgetPrefs.SYMBOL_STANDARD_SELECTION);
            //String fxTicker
            mFxTicker = prefs.getString(KeyGeneratorWidgetPrefs.getKey(widgetId, SUFFIX_FX_TICKER_SHARED_PREF),
                    KeyGeneratorWidgetPrefs.STANDARD_FX_CURRENCY_TICKER);
            //String fxSymbol
            mFxSymbol = prefs.getString(KeyGeneratorWidgetPrefs.getKey(widgetId, SUFFIX_FX_SYMBOL_SHARED_PREF),
                    KeyGeneratorWidgetPrefs.STANDARD_FX_CURRENCY_SYMBOL);
        //}
    }
    private void saveWithNewWidgetIdentifier(Context context, int oldWidgetId, int newWidgetId){
        if(oldWidgetId != newWidgetId){
            SharedPreferences prefs = context.getSharedPreferences(WIDGET_SHARED_PREFS, MODE_PRIVATE);

            String oldIdKey = KeyGeneratorWidgetPrefs.getKey(oldWidgetId, SUFFIX_ID_SHARED_PREF );
            String oldTickerKey = KeyGeneratorWidgetPrefs.getKey(oldWidgetId, SUFFIX_TICKER_SHARED_PREF);
            String oldFxTickerKey = KeyGeneratorWidgetPrefs.getKey(oldWidgetId, SUFFIX_FX_TICKER_SHARED_PREF);
            String oldFxSymbolKey = KeyGeneratorWidgetPrefs.getKey(oldWidgetId, SUFFIX_FX_SYMBOL_SHARED_PREF);

            mId = prefs.getString(oldIdKey,
                    KeyGeneratorWidgetPrefs.IDENTIFIER_STANDARD_SELECTION);

            //String cryptoTicker
            mTicker = prefs.getString(oldTickerKey,
                    KeyGeneratorWidgetPrefs.SYMBOL_STANDARD_SELECTION);
            //String fxTicker
            mFxTicker = prefs.getString(oldFxTickerKey,
                    KeyGeneratorWidgetPrefs.STANDARD_FX_CURRENCY_TICKER);
            //String fxSymbol
            mFxSymbol = prefs.getString(oldFxSymbolKey,
                    KeyGeneratorWidgetPrefs.STANDARD_FX_CURRENCY_SYMBOL);

            SharedPreferences.Editor editor = prefs.edit();

            editor.remove(oldIdKey);
            editor.remove(oldTickerKey);
            editor.remove(oldFxTickerKey);
            editor.remove(oldFxSymbolKey);

            String newIdKey = KeyGeneratorWidgetPrefs.getKey(newWidgetId, SUFFIX_ID_SHARED_PREF );
            String newTickerKey = KeyGeneratorWidgetPrefs.getKey(newWidgetId, SUFFIX_TICKER_SHARED_PREF);
            String newFxTickerKey = KeyGeneratorWidgetPrefs.getKey(newWidgetId, SUFFIX_FX_TICKER_SHARED_PREF);
            String newFxSymbolKey = KeyGeneratorWidgetPrefs.getKey(newWidgetId, SUFFIX_FX_SYMBOL_SHARED_PREF);

            editor.putString(newIdKey, mId);
            editor.putString(newTickerKey, mTicker);
            editor.putString(newFxTickerKey, mFxTicker);
            editor.putString(newFxSymbolKey, mFxSymbol);
            editor.commit();

        }

    }

    private RemoteViews setupWidgetViews(Context context, int appWidgetId){


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_single_crypto);


        if(isConnected(context)){
            MarketData data = APIaccessDao.getMarketData(mId);
            if(data != null) {

                BigDecimal price = data.getPrice(mFxTicker.toLowerCase());
                BigDecimal change = data.getPriceChangePercentage24h(mFxTicker.toLowerCase());

                DecimalFormat df = new DecimalFormat("#,##0.00##");
                String priceStr = df.format(price) + mFxSymbol;
                DecimalFormat dfchange = new DecimalFormat("#,##0.00");
                String changeStr = dfchange.format(change) + " %";
                views.setTextViewText(R.id.price_tv, priceStr);
                views.setTextViewText(R.id.percentage_24h_tv, changeStr);
                int resourceColor = R.color.green_pump;
                if (change.signum() == -1) {
                    resourceColor = R.color.red_dump;
                }
                views.setTextColor(R.id.percentage_24h_tv, context.getResources().getColor(resourceColor));

                String utcDate = data.getLastUpdated();
                System.out.println("Date : " + utcDate);
                try {
                    //XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(utcDate);
                    //Calendar calendar = cal.toGregorianCalendar();

                    Calendar calendar = Calendar.getInstance();
                    //calendar.setTime(new Date());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    calendar.setTime(sdf.parse(utcDate));

                    //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    //print UTC
                    //System.out.println(sdf.format(calendar.getTime()));
                    //set my timezone
                    //sdf.setTimeZone(TimeZone.getDefault());
                    //Will print on your default Timezone

                    SimpleDateFormat sdfDestination = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    sdfDestination.setTimeZone(TimeZone.getDefault());
                    String lastUpdatedAt = sdfDestination.format(calendar.getTime());

                    views.setTextViewText(R.id.last_updated_at_tv, lastUpdatedAt);
                } catch (ParseException e) {
                    System.out.println("Parse  exc " + e.getMessage());
                    //System.out.println("Datatype config exc " + e.getMessage());
                }
            }else{
                views.setTextViewText(R.id.last_updated_at_tv, context.getResources().getString(R.string.server_error));
            }
        }else{
            //Toast.makeText(context, R.string.internet_required_for_price_data, Toast.LENGTH_SHORT).show();

            views.setTextViewText(R.id.last_updated_at_tv, context.getResources().getString(R.string.connection_required));
        }

        views.setTextViewText(R.id.ticker_tv, mTicker.toUpperCase());


        ImgType type = ImgType.SMALL;
        Bitmap logo;
        try{
            logo = BitmapFactory.decodeStream( context.getAssets()
                    .open(getAssetLogoPath( (new Crypto(mId, null, null)) , ImgType.SMALL) ));

            views.setImageViewBitmap(R.id.logo_view, logo);
        }catch(IOException e){
            File imageDir = new File(ImageReader.getImageDirStoragePath(context, ImgType.SMALL));
            if( !imageDir.exists()) {
                imageDir.mkdirs();
            }
            File imageFile = new File(ImageReader.getImageFileStoragePath(context, mId, ImgType.SMALL));
            if(imageFile.exists()){
                logo = BitmapFactory.decodeFile(imageFile.getPath());
                views.setImageViewBitmap(R.id.logo_view, logo);
            }else{
                GeckoInfoService infoService = RetrofitClientInstance.getRetrofitInstance(new TypeToken<AssetInfo>() {}.getType(), new InfoDeserializer())
                        .create(GeckoInfoService.class);
                Call<AssetInfo> infos = infoService.getCryptoFullDataList(mId, false, false, false, false, false, false);


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

                                    try (BufferedInputStream reader = new BufferedInputStream(connection.getInputStream()    );
                                         BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream( imageFile, true ))		){
                                        byte arr[] = new byte[1024];


                                        int n = 0;

                                        while( (n = reader.read(arr)) != -1 ) {
                                            writer.write(arr, 0, n);
                                        }

                                    }catch(IOException e) {

                                    }

                                }catch(IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        try {
                            t.start();

                            t.join();
                            if (views != null ) {
                                //if (((String) holder.rootView.getTag()).compareTo(cryptoId) == 0) {
                                    Bitmap downloadedLogo = BitmapFactory.decodeFile(imageFile.getPath());
                                    views.setImageViewBitmap(R.id.logo_view, downloadedLogo);
                                //}
                            }
                        }catch (InterruptedException interrExc){

                        }

                    }

                    @Override
                    public void onFailure(Call<AssetInfo> call, Throwable t) {

                    }
                });
            }
            e.printStackTrace();
        }


        Intent update = new Intent(context, getClass());

        update.setAction(ACTION_UPDATE_CRYPTO);

        Bundle b = new Bundle();
        b.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        update.putExtras(b);

        PendingIntent pendingUpdate =  PendingIntent.getBroadcast(context, appWidgetId, update, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.refresh_btn, pendingUpdate);
        return views;
    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

    /*    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        if(  ! netInfo.isConnected() ){
            Toast.makeText(context, R.string.internet_connection_required, Toast.LENGTH_LONG).show();
        }*/


        // Perform this loop procedure for each App Widget that belongs to this provider
        //if(isConnected(context)){
            for (int i=0; i<N; i++) {
                int appWidgetId = appWidgetIds[i];
                restoreWidgetData(context, appWidgetId);
                RemoteViews views = setupWidgetViews(context, appWidgetId);
                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, views);

           /* Bundle args = appWidgetManager.getAppWidgetOptions(appWidgetId);
            if(args != null){
                mId = args.getString(ConfigurationActivity.ID_KEY, "bitcoin" );
                mTicker = args.getString(ConfigurationActivity.TICKER_KEY, "BTC");
                mFxTicker = args.getString(ConfigurationActivity.FX_TICKER_KEY, "USD");
                mFxSymbol = args.getString(ConfigurationActivity.FX_SYMBOL_KEY, "$");
            }else{
                mId = "bitcoin";
                mTicker = "BTC";
                mFxTicker = "USD";
                mFxSymbol = "$";
            }
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_single_crypto);

            // System.out.println("CRYPTOWIDGETPROVIDER_ONUPDATE "  + mId + " " + appWidgetId);

            MarketData data = APIaccessDao.getMarketData(mId);

            BigDecimal price = data.getPrice(mFxTicker.toLowerCase());
            BigDecimal change = data.getPriceChangePercentage24h(mFxTicker.toLowerCase());

            DecimalFormat df = new DecimalFormat("#,##0.000");
            String priceStr = df.format(price) + mFxSymbol;
            DecimalFormat dfchange = new DecimalFormat("#,##0.00");
            String changeStr = dfchange.format(change) + " %";

            ImgType type = ImgType.SMALL;
            Bitmap logo;
            try{
                logo = BitmapFactory.decodeStream( context.getAssets()
                        .open(AssetsUtils.IMAGE_ASSETS_PATH +
                                mId  +"/" +
                                type.toString() + ".png") );

                views.setImageViewBitmap(R.id.logo_view, logo);
            }catch(IOException e){
                e.printStackTrace();
            }

            views.setTextViewText(R.id.ticker_tv, mTicker.toUpperCase());
            views.setTextViewText(R.id.price_tv,  priceStr );
            views.setTextViewText(R.id.percentage_24h_tv, changeStr );

            int resourceColor = R.color.green_pump;
            if(change.signum() == -1){
                resourceColor = R.color.red_dump;
            }

            views.setTextColor(R.id.percentage_24h_tv, context.getResources().getColor(resourceColor) );

            Intent update = new Intent(context, getClass());

            update.setAction(ACTION_UPDATE_CRYPTO);

            Bundle b = new Bundle();
            b.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            update.putExtras(b);

            PendingIntent pendingUpdate =  PendingIntent.getBroadcast(context, appWidgetId, update, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.refresh_btn, pendingUpdate);
            System.out.println("broad");*/
            }
       /* }else{
            Toast.makeText(context, R.string.internet_required_for_price_data, Toast.LENGTH_SHORT).show();
        }*/


    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Chain up to the super class so the onEnabled, etc callbacks get dispatched


       // int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        super.onReceive(context, intent);

        if(
                //isConnected(context) &&
                        ACTION_UPDATE_CRYPTO.equals(intent.getAction())){
            int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);


            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            restoreWidgetData(context, appWidgetId);
            RemoteViews views = setupWidgetViews(context, appWidgetId);
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
      /*     *//* Bundle extra = intent.getExtras();
            int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            if(extra != null){*//*

                                //extra.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);


                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_single_crypto);
                //ComponentName widgetComp = new ComponentName(context, CryptoWidgetProvider.class);

                Bundle args = appWidgetManager.getAppWidgetOptions(appWidgetId);
                if(args != null){
                    mId = args.getString(ConfigurationActivity.ID_KEY, "bitcoin" );
                    mTicker = args.getString(ConfigurationActivity.TICKER_KEY, "BTC");
                    mFxTicker = args.getString(ConfigurationActivity.FX_TICKER_KEY, "USD");
                    mFxSymbol = args.getString(ConfigurationActivity.FX_SYMBOL_KEY, "$");
                }else{
                    mId = "bitcoin";
                    mTicker = "BTC";
                    mFxTicker = "USD";
                    mFxSymbol = "$";
                }

            System.out.println( (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID )+ " " + mId + " " + mTicker + " " + appWidgetId);

                MarketData data = APIaccessDao.getMarketData(mId);

                BigDecimal price = data.getPrice(mFxTicker.toLowerCase());
                BigDecimal change = data.getPriceChangePercentage24h(mFxTicker.toLowerCase());

                DecimalFormat df = new DecimalFormat("#,##0.000");
                String priceStr = df.format(price) + mFxSymbol;
                DecimalFormat dfchange = new DecimalFormat("#,##0.00");
                String changeStr = dfchange.format(change) + " %";


                ImgType type = ImgType.SMALL;
                Bitmap logo;
                try{
                    logo = BitmapFactory.decodeStream( context.getAssets()
                            .open(AssetsUtils.IMAGE_ASSETS_PATH +
                                    mId  +"/" +
                                    type.toString() + ".png") );

                    views.setImageViewBitmap(R.id.logo_view, logo);
                }catch(IOException e){
                    e.printStackTrace();
                }

                views.setTextViewText(R.id.ticker_tv, mTicker.toUpperCase());
                views.setTextViewText(R.id.price_tv,  priceStr );
                views.setTextViewText(R.id.percentage_24h_tv, changeStr );

                int resourceColor = R.color.green_pump;
                if(change.signum() == -1){
                    resourceColor = R.color.red_dump;
                }

                views.setTextColor(R.id.percentage_24h_tv, context.getResources().getColor(resourceColor) );
                System.out.println("called");

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            //}*/




        }
        /*else{
            Toast.makeText(context, R.string.internet_required_for_price_data, Toast.LENGTH_SHORT).show();
        }*/
    }


    private boolean isConnected(Context context ){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
