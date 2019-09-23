package com.nexlabs.comnexlabsnanowidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binance.api.client.domain.general.Asset;
import com.google.gson.reflect.TypeToken;
import com.nexlabs.comnexlabsnanowidget.currencymanager.CurrencyUtils;
import com.nexlabs.comnexlabsnanowidget.currencymanager.symbols.CurrencyIdentifier;
import com.nexlabs.comnexlabsnanowidget.currencymanager.symbols.SymbolFactory;
import com.nexlabs.comnexlabsnanowidget.datapackage.AssetInfo;
import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;
import com.nexlabs.comnexlabsnanowidget.datapackage.ImgType;
import com.nexlabs.comnexlabsnanowidget.datapackage.MarketData;
import com.nexlabs.comnexlabsnanowidget.fragments.PreferencesFragment;
import com.nexlabs.comnexlabsnanowidget.geckoapi.GeckoInfoService;
import com.nexlabs.comnexlabsnanowidget.geckoapi.InfoDeserializer;
import com.nexlabs.comnexlabsnanowidget.geckoapi.RetrofitClientInstance;
import com.nexlabs.comnexlabsnanowidget.utils.ImageReader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nexlabs.comnexlabsnanowidget.utils.ImagePathUtils.getAssetLogoPath;


public class AssetsRecViewAdapter extends RecyclerView.Adapter<AssetsVH> {


    static Set<String> cryptoPrefs = null;
    public interface OnVHItemClickedListener{
        public void onItemClickedAction(Crypto crypto, Bitmap logo);
    }
    public interface OnPreferenceChangedListener{
        public void onPreferenceChanged(Crypto crypto);
    }

    private static BigDecimal zero = new BigDecimal(0);

    CurrencyIdentifier selectedFxCurrency;
    Context context;
    int itemResource;
    List<Crypto> list;
    HashSet<String> visibleCryptos;
    OnVHItemClickedListener listener;
    OnPreferenceChangedListener preferenceChangedListener;
    boolean showRank;

    public AssetsRecViewAdapter(Context context, int itemResouce, List<Crypto> list, OnVHItemClickedListener listener, CurrencyIdentifier selectedFxCurrency, OnPreferenceChangedListener preferenceChangedListener, boolean showRank ){
        this.context = context;
        this.itemResource = itemResouce;
        this.list = list;
        this.listener = listener;
        this.selectedFxCurrency = selectedFxCurrency;
        this.preferenceChangedListener = preferenceChangedListener;
        this.showRank = showRank;
        if(cryptoPrefs == null){

            this.cryptoPrefs = new LinkedHashSet<>(getStringSetPreferences(context) );
        }
    }

    @NonNull
    @Override
    public AssetsVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {
        View rootView = LayoutInflater.from(context).inflate(itemResource, viewGroup, false);

        return new AssetsVH(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AssetsVH holder, int pos) {
        ImgType type = ImgType.SMALL;
        final Crypto c = list.get(pos);

        //visibleCryptos.add(c.getSymbol().toUpperCase());
        if(showRank){
            holder.rankTv.setText(c.getMarket().getMarketCapRank() + "");
        }else{
            holder.rankTv.setVisibility(View.GONE);
        }

        holder.rootView.setTag(c.getId());
        holder.tickerTv.setText(c.getSymbol().toUpperCase());



        MarketData data = c.getMarket();
        String usedCurrency = selectedFxCurrency.getTicker();
      // System.out.println(c.getSymbol() + " " + usedCurrency + " "  + data.getPrice(usedCurrency) + " " + data.getPrice("usd") + " " + data.getMarketCapRank());
        if(data != null){
            holder.priceTv.setText(CurrencyUtils.formatValue(data.getPrice(usedCurrency)) + selectedFxCurrency.getSymbol() );
            holder.assetsMarketCapTv.setText(CurrencyUtils.formatCmcnum(data.getMarketCap(selectedFxCurrency.getTicker())));
            BigDecimal priceChangePercent = data.getPriceChangePercentage24h(usedCurrency);
            int res = priceChangePercent.compareTo(zero);
            if(res > 0){

                holder.priceChangeCard.setCardBackgroundColor(context.getResources().getColor(R.color.green_pump));
                holder.changeTv.setTextColor(context.getResources().getColor(R.color.white_high));
            }else if( res < 0){
                holder.priceChangeCard.setCardBackgroundColor(context.getResources().getColor(R.color.red_dump));
                holder.changeTv.setTextColor(context.getResources().getColor(R.color.white_high));
            }else{
                holder.priceChangeCard.setCardBackgroundColor(context.getResources().getColor(R.color.grey_dull));
                holder.changeTv.setTextColor(context.getResources().getColor(R.color.black_dull));
            }
            //vh.changeTv.setText(String.format("%.2f %", e.getPriceChangePercent() ) );

            holder.changeTv.setText( CurrencyUtils.formatPercentageValue(data.getPriceChangePercentage24h(usedCurrency)) +"%");


            if(cryptoPrefs.contains(c.getId()) ){
                holder.assetsPrefImage.setTag("" +R.drawable.save_check_fin);
                holder.assetsPrefImage.setImageResource(R.drawable.save_check_fin);
            }else{
                holder.assetsPrefImage.setTag("" +R.drawable.preferable_fin);
                holder.assetsPrefImage.setImageResource(R.drawable.preferable_fin);

            }

            holder.assetsPrefImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String stateTag = (String) holder.assetsPrefImage.getTag();
                    if(stateTag.equals(""+ R.drawable.preferable_fin)){
                        // state change from preferable to saved pref
                        holder.assetsPrefImage.setImageResource(R.drawable.save_check_fin);
                        holder.assetsPrefImage.setTag("" + R.drawable.save_check_fin);

                        savePref(c);
                    }else{
                        holder.assetsPrefImage.setImageResource(R.drawable.preferable_fin);
                        holder.assetsPrefImage.setTag("" + R.drawable.preferable_fin);

                        removePref(c.getId());
                    }
                    preferenceChangedListener.onPreferenceChanged(c);
                }
            });

        }


        //holder.nameTv.setText(c.getName());
        Bitmap logo = null;
        try{
            logo = BitmapFactory.decodeStream( context.getAssets()
                .open(getAssetLogoPath(c, ImgType.SMALL) ));

            holder.logoView.setImageBitmap(logo);


        }catch(IOException e){
            File imageDir = new File(ImageReader.getImageDirStoragePath(context, ImgType.SMALL));
            if( !imageDir.exists()) {
                imageDir.mkdirs();
            }
            File imageFile = new File(ImageReader.getImageFileStoragePath(context, c.getId(), ImgType.SMALL));
            if(imageFile.exists()){
                logo = BitmapFactory.decodeFile(imageFile.getPath());
                holder.logoView.setImageBitmap(logo);
            }else{
                GeckoInfoService infoService = RetrofitClientInstance.getRetrofitInstance(new TypeToken<AssetInfo>() {}.getType(), new InfoDeserializer())
                        .create(GeckoInfoService.class);
                Call<AssetInfo> infos = infoService.getCryptoFullDataList(c.getId(), false, false, false, false, false, false);


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
                            if (holder != null && holder.rootView != null && holder.rootView.getTag() != null && holder.rootView.getTag() instanceof String) {
                                if (((String) holder.rootView.getTag()).compareTo(cryptoId) == 0) {
                                    Bitmap downloadedLogo = BitmapFactory.decodeFile(imageFile.getPath());
                                    holder.logoView.setImageBitmap(downloadedLogo);

                                }
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
        final Bitmap bitmap = logo;
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickedAction(c, bitmap );
            }
        });

    }

    public CurrencyIdentifier modifyCurrencyIdentifier(CurrencyIdentifier newCurrencyIdentifier){
        CurrencyIdentifier older = this.selectedFxCurrency;
        this.selectedFxCurrency = newCurrencyIdentifier;
        return older;
    }

    public CurrencyIdentifier getSelectedFxCurrency() {
        return selectedFxCurrency;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void savePref(Crypto crypto){
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.COMMON_FAV_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(MainActivity.NEW_CHANGE_NOTIFICATION_PREFERENCES, true);
        editor.putString(crypto.getId(), crypto.toJSONbaseData());
       /* if( ! prefs.contains(MainActivity.FAV_FILE_STRINGSET_PREF_KEY) ){
            Set<String> fileSet = new LinkedHashSet<>();
            prefs.edit().putStringSet(MainActivity.FAV_FILE_STRINGSET_PREF_KEY,
                                    fileSet).commit();
        }
        Set<String> fileSet = prefs.getStringSet(MainActivity.FAV_FILE_STRINGSET_PREF_KEY, new LinkedHashSet<String>());*/
        cryptoPrefs.add(crypto.getId());
        editor.putStringSet(MainActivity.FAV_STRINGSET_PREF_KEY,
                        cryptoPrefs);
        editor.apply();
    }

    private void removePref(String id){
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.COMMON_FAV_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(MainActivity.NEW_CHANGE_NOTIFICATION_PREFERENCES, true);
        editor.remove(id);
       /* if( ! prefs.contains(MainActivity.FAV_FILE_STRINGSET_PREF_KEY) ){
            Set<String> fileSet = new LinkedHashSet<>();
            prefs.edit().putStringSet(MainActivity.FAV_FILE_STRINGSET_PREF_KEY,
                                    fileSet).commit();
        }
        Set<String> fileSet = prefs.getStringSet(MainActivity.FAV_FILE_STRINGSET_PREF_KEY, new LinkedHashSet<String>());*/
        cryptoPrefs.remove(id);
        editor
                .putStringSet(MainActivity.FAV_STRINGSET_PREF_KEY,
                        cryptoPrefs);
        editor.apply();


    }

    private Set<String> getStringSetPreferences(Context context){
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.COMMON_FAV_PREFERENCES, Context.MODE_PRIVATE);
        Set<String> cryptoSet = null;
        if( ! prefs.contains(MainActivity.FAV_STRINGSET_PREF_KEY) ){
            cryptoSet = new LinkedHashSet<>();
            prefs.edit().putStringSet(MainActivity.FAV_STRINGSET_PREF_KEY,
                    cryptoSet).commit();
        }else{
            cryptoSet = prefs.getStringSet(MainActivity.FAV_STRINGSET_PREF_KEY, new LinkedHashSet<String>());
        }
        return cryptoSet;
    }
}
