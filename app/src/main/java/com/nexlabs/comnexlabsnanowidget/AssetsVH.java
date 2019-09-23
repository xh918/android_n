package com.nexlabs.comnexlabsnanowidget;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AssetsVH extends RecyclerView.ViewHolder {

    View rootView;
    CardView priceChangeCard;
    TextView rankTv;
    ImageView logoView;
    TextView tickerTv;
   // TextView nameTv;
    TextView priceTv;
    TextView changeTv;
    TextView assetsMarketCapTv;
    ImageView assetsPrefImage;
    LinearLayout assetsPrefLayout;

    public AssetsVH(@NonNull View itemView) {
        super(itemView);
        rootView = itemView;
        rankTv = (TextView) itemView.findViewById(R.id.assets_rank_tv);
        logoView = (ImageView) itemView.findViewById(R.id.assets_logo_view);
        tickerTv = (TextView) itemView.findViewById(R.id.assets_ticker_tv);
        //nameTv = (TextView) itemView.findViewById(R.id);
        priceTv = (TextView) itemView.findViewById(R.id.assets_price_tv);
        changeTv = (TextView ) itemView.findViewById(R.id.assets_percentage_24h_tv);
        priceChangeCard = (CardView) itemView.findViewById(R.id.assets_price_change_cardview);
        assetsMarketCapTv = (TextView) itemView.findViewById(R.id.assets_marketcap_tv);
        assetsPrefImage = (ImageView) itemView.findViewById(R.id.preference_img_view);
        assetsPrefLayout = (LinearLayout) itemView.findViewById(R.id.preference_layout);

    }
}
