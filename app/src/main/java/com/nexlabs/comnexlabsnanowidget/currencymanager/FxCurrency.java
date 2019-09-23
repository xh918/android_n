package com.nexlabs.comnexlabsnanowidget.currencymanager;

import android.support.annotation.NonNull;

import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class FxCurrency implements Comparable<FxCurrency> {

    private static final String TICKER_FIELD = "ticker";
    private static final String SYMBOL_FIELD = "symbol";
    private static final String NAME_FIELD = "name";
    private static final String RATE_AGAINST_EUR = "rate_against_eur";

    // us dollar
    private String name = null;
    //usd
    private String ticker = null;
    // $
    private String symbol = null;

    // all pairings are read as xxx/EUR
    private BigDecimal rateAgainstEUR = null;


    public FxCurrency(String ticker, BigDecimal rateAgainstEUR) {
        this.ticker = ticker;
        this.rateAgainstEUR = rateAgainstEUR;
    }

    public FxCurrency(String name, String ticker, String symbol, BigDecimal rateAgainstEUR) {
        this.name = name;
        this.ticker = ticker;
        this.symbol = symbol;
        this.rateAgainstEUR = rateAgainstEUR;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public String getSymbol() {
        return symbol;
    }

    public void updateRateAgainstEUR(BigDecimal newRate){
        this.rateAgainstEUR = newRate;
    }

    public BigDecimal getRateAgainstEUR() {
        return rateAgainstEUR;
    }

    @Override
    public int compareTo(@NonNull FxCurrency o) {
        return this.ticker.compareTo(o.ticker);
    }

    public String toJSONbaseData(){
        try{
            JSONObject fxCurrency = new JSONObject();
            fxCurrency.put(TICKER_FIELD, this.ticker);
            fxCurrency.put(SYMBOL_FIELD, this.symbol);
            fxCurrency.put(NAME_FIELD, this.name);
            fxCurrency.put(RATE_AGAINST_EUR, this.getRateAgainstEUR().toString());
            return fxCurrency.toString();

        }catch (JSONException e){

        }

        return null;
    }

    public static FxCurrency fromJSONbaseData(String json){
        FxCurrency fxCurrency = null;
        try {
            JSONObject object = new JSONObject(json);
            String ticker = object.getString(TICKER_FIELD);
            String symbol = object.getString(SYMBOL_FIELD);
            String name = object.getString(NAME_FIELD);
            BigDecimal rateAgainstEUR = new BigDecimal(object.getString(RATE_AGAINST_EUR));
            fxCurrency = new FxCurrency(name, ticker, symbol, rateAgainstEUR);

        }catch (JSONException e){

        }
        return fxCurrency;
    }


}
