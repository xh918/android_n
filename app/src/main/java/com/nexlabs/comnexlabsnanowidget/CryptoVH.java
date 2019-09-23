package com.nexlabs.comnexlabsnanowidget;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TintContextWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CryptoVH extends RecyclerView.ViewHolder {

    View rootView;
    TextView rankTv;
    ImageView logoView;
    TextView tickerTv;
    TextView nameTv;



    public CryptoVH(@NonNull View itemView) {
        super(itemView);
        rootView = itemView;
        rankTv = (TextView) itemView.findViewById(R.id.rank_tv);
        logoView = (ImageView) itemView.findViewById(R.id.logo_view);
        tickerTv = (TextView) itemView.findViewById(R.id.ticker_tv);
        nameTv = (TextView) itemView.findViewById(R.id.name_tv);
    }
}
