package com.nexlabs.comnexlabsnanowidget.currencymanager.symbols;

import com.nexlabs.comnexlabsnanowidget.currencymanager.FxCurrency;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class CurrencyIdentifier {
    private static final String TICKER_FIELD = "ticker";
    private static final String SYMBOL_FIELD = "symbol";
    private static final String NAME_FIELD = "name";

    String name;
    String ticker;
    String symbol;


    public CurrencyIdentifier(String name, String ticker, String symbol) {
        this.name = name;
        this.ticker = ticker;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }



    public String toJSONbaseData(){
        try{
            JSONObject fxCurrencyIdentifier = new JSONObject();
            fxCurrencyIdentifier.put(TICKER_FIELD, this.ticker);
            fxCurrencyIdentifier.put(SYMBOL_FIELD, this.symbol);
            fxCurrencyIdentifier.put(NAME_FIELD, this.name);

            return fxCurrencyIdentifier.toString();

        }catch (JSONException e){

        }

        return null;
    }

    public static CurrencyIdentifier fromJSONbaseData(String json){
        CurrencyIdentifier fxCurrencyIdentifier = null;
        try {
            JSONObject object = new JSONObject(json);
            String ticker = object.getString(TICKER_FIELD);
            String symbol = object.getString(SYMBOL_FIELD);
            String name = object.getString(NAME_FIELD);

            fxCurrencyIdentifier = new CurrencyIdentifier(name, ticker, symbol);

        }catch (JSONException e){

        }
        return fxCurrencyIdentifier;
    }
}
