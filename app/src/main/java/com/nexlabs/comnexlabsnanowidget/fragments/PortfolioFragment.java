package com.nexlabs.comnexlabsnanowidget.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nexlabs.comnexlabsnanowidget.R;


public class PortfolioFragment extends Fragment{
    /*
    ImageView logoView;
    TextView tickerTv;
    TextView currentPriceTv;
    TextView amountTv;
    TextView valueTv;
    TextView fromDateTv;
    TextView toDateTv;
    */
    ImageButton addPositionBtn;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_portfolio, container, false);

        addPositionBtn = (ImageButton) rootView.findViewById(R.id.add_position_btn);


        /*
        logoView = (ImageView) rootView.findViewById(R.id.position_logo_view);
        tickerTv = (TextView) rootView.findViewById(R.id.portfolio_ticker_tv);
        currentPriceTv = (TextView) rootView.findViewById(R.id.portfolio_item_price_tv);
        amountTv = (TextView) rootView.findViewById(R.id.position_amount_tv);
        valueTv = (TextView) rootView.findViewById(R.id.position_value_tv);
        fromDateTv = (TextView) rootView.findViewById(R.id.from_position_date_tv);
        toDateTv = (TextView) rootView.findViewById(R.id.to_position_date_tv);
        */
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPositionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = (DialogFragment) DialogFragment.instantiate(getActivity(), "Hello world");
                dialog.show(getFragmentManager(), "dialog");
            }
        });
    }
}
