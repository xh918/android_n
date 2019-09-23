package com.nexlabs.comnexlabsnanowidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.nexlabs.comnexlabsnanowidget.datapackage.AssetInfo;
import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;
import com.nexlabs.comnexlabsnanowidget.datapackage.ImgType;
import com.nexlabs.comnexlabsnanowidget.geckoapi.GeckoInfoService;
import com.nexlabs.comnexlabsnanowidget.geckoapi.InfoDeserializer;
import com.nexlabs.comnexlabsnanowidget.geckoapi.RetrofitClientInstance;
import com.nexlabs.comnexlabsnanowidget.utils.ImageReader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nexlabs.comnexlabsnanowidget.utils.ImagePathUtils.getAssetLogoPath;


public class CryptoRecViewAdapter extends RecyclerView.Adapter<CryptoVH> {


    public interface OnVHItemClickedListener{
        public void onItemClickedAction(Crypto crypto, Bitmap logo);
    }


    Context context;
    int itemResource;
    List<Crypto> list;
    OnVHItemClickedListener listener;

    public CryptoRecViewAdapter(Context context, int itemResouce, List<Crypto> list, OnVHItemClickedListener listener){
        this.context = context;
        this.itemResource = itemResouce;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CryptoVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {
        View rootView = LayoutInflater.from(context).inflate(itemResource, viewGroup, false);

        return new CryptoVH(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CryptoVH holder, int pos) {
        ImgType type = ImgType.SMALL;
        final Crypto c = list.get(pos);



        holder.rankTv.setText(c.getMarket().getMarketCapRank() + "");
        holder.tickerTv.setText(c.getSymbol());
        holder.nameTv.setText(c.getName());
        Bitmap logo = null;
        try{
            logo = BitmapFactory.decodeStream( context.getAssets()
                    .open(getAssetLogoPath(c, ImgType.SMALL) ) );

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
                listener.onItemClickedAction(c, ((BitmapDrawable)holder.logoView.getDrawable()).getBitmap() );
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
