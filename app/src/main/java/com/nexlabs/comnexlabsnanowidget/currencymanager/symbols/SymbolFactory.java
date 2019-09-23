package com.nexlabs.comnexlabsnanowidget.currencymanager.symbols;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeMap;

public class SymbolFactory {

    private static final String CURRENCY_JSON_PATH = "currencyidentifiers/currencies_supported.json";

    private static final String nameKey = "name";
    private static final String tickerKey = "ticker";
    private static final String symbolKey = "symbol";

    private Context context;
    private static SymbolFactory factory = null;
    private TreeMap<String, CurrencyIdentifier> identifiers = null;




    private SymbolFactory(Context context){
        this.context = context;
    }


    public static SymbolFactory getInstance(Context context){
        if(factory == null){
            factory = new SymbolFactory(context);
            factory.identifiers = factory.readCurrencies(context);
        }
        return factory;
    }

    public String getSymbol(String currencyTicker){
        if(identifiers != null){
            return identifiers.get(currencyTicker).getSymbol();
        }
        return null;
    }
    public String getFullName(String currencyTicker){
        if(identifiers != null){
            return identifiers.get(currencyTicker).getName();
        }
        return null;
    }
    public CurrencyIdentifier getCurrencyInfo(String currencyTicker){
        if(identifiers != null){
            return identifiers.get(currencyTicker);
        }
        return null;
    }
    public boolean isSupported(String currencyTicker){
        return identifiers.containsKey(currencyTicker);
    }

    private TreeMap<String, CurrencyIdentifier> readCurrencies(Context context){
        TreeMap<String, CurrencyIdentifier> map = new TreeMap<>();
        try{
            BufferedReader reader = new BufferedReader( new InputStreamReader(context.getAssets().open(CURRENCY_JSON_PATH), "utf-8"  ) );

            String line = null;
            StringBuffer sb = new StringBuffer();
            while( (line = reader.readLine())!= null ){
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(new String(sb));
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString(nameKey);
                String ticker = jsonObject.getString(tickerKey);
                String symbol = jsonObject.getString(symbolKey);
                CurrencyIdentifier identifier = new CurrencyIdentifier(name, ticker, symbol);
                map.put(ticker, identifier);
            }
            reader.close();
            return map;
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

}
