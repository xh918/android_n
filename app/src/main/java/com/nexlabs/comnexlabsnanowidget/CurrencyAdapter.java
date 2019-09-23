package com.nexlabs.comnexlabsnanowidget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class CurrencyAdapter extends ArrayAdapter<String> {

    Context context;
    int resource;
    int textViewResource;
    String data[];

    public CurrencyAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    public CurrencyAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.context = context;
        this.resource = resource;
        this.textViewResource = textViewResourceId;

    }

    public CurrencyAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.data = objects;
        Arrays.sort(objects);
    }

    public CurrencyAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull String[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.resource = resource;
        this.textViewResource = textViewResourceId;
        this.data = objects;
        Arrays.sort(objects);

    }

    public CurrencyAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.data = objects.toArray(new String[objects.size()]);
        Arrays.sort(this.data);
    }

    public CurrencyAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.resource = resource;
        this.textViewResource = textViewResourceId;
        this.data = objects.toArray(new String[objects.size()]);
        Arrays.sort(this.data);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public String getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        if(rootView == null){
            rootView = LayoutInflater.from(context).inflate(R.layout.spinner_item_currency, parent, false);
        }

        TextView textView = (TextView) rootView.findViewById(R.id.ticker_spinner_tv);

        textView.setText(data[position]);

        return rootView;
    }

    @Override
    public int getPosition(@Nullable String item) {
        return Arrays.binarySearch(this.data, item);
    }
}
