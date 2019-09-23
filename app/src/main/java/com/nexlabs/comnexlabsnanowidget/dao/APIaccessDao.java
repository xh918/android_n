package com.nexlabs.comnexlabsnanowidget.dao;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nexlabs.comnexlabsnanowidget.currencymanager.symbols.CurrencyIdentifier;
import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;
import com.nexlabs.comnexlabsnanowidget.datapackage.MarketData;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.nexlabs.comnexlabsnanowidget.currencymanager.CurrencyUtils.CURRENCY_CONTEXT;

public class APIaccessDao {


    private static final String ABSENT_VALUE = "null";

    private static final String API_MAIN_URL = "https://api.coingecko.com/api/v3/";


    private static final String SIMPLE_API = "simple/price?";

    private static final String IDS_SIMPLE_API = "ids=";
    private static final String VS_CURRENCIES_SIMPLE_API = "vs_currencies=";
    private static final String INCLUDE_MARKET_CAP_SIMPLE_API = "include_market_cap=";
    private static final String INCLUDE_24H_VOL_SIMPLE_API = "include_24hr_vol=";
    private static final String INCLUDE_24H_CHANGE_SIMPLE_API = "include_24hr_change=";
    private static final String INCLUDE_LAST_UPDATED_AT_SIMPLE_API = "include_last_updated_at=";

    private static final String CURRENCY_RESPONSE_SIMPLE_API = "";
    private static final String CURRENCY_MARKET_CAP_RESPONSE_SIMPLE_API = "_market_cap";
    private static final String CURRENCY_24H_VOL__RESPONSE_SIMPLE_API = "_24h_vol";
    private static final String CURRENCY_24H_CHANGE__RESPONSE_SIMPLE_API = "_24h_change";
    private static final String LAST_UPDATED_AT_RESPONSE__RESPONSE_SIMPLE_API = "last_updated_at";



    public static ArrayList<Crypto> queryCoins(List<String> coinIds, CurrencyIdentifier fxCurrency){
        try {
            StringBuilder sb = new StringBuilder("");
            sb.append(API_MAIN_URL);
            sb.append(SIMPLE_API);
            sb.append(IDS_SIMPLE_API);
            // all the ids
            for(int i = 0; i < coinIds.size(); i++){
                sb.append(coinIds.get(i));
                if(i < coinIds.size() - 1 ){
                    sb.append(",");
                }
            }
            //
            sb.append("&");
            sb.append(VS_CURRENCIES_SIMPLE_API);
            sb.append(fxCurrency.getTicker());
            sb.append("&");
            sb.append(INCLUDE_MARKET_CAP_SIMPLE_API);
            sb.append("true&");
            sb.append(INCLUDE_24H_CHANGE_SIMPLE_API);
            sb.append("true&");
            sb.append(INCLUDE_LAST_UPDATED_AT_SIMPLE_API);
            sb.append("true");
            //System.out.println(sb.toString());
            URL url = new URL(sb.toString());
            SimpleAccessAsyncTask simpleAccessAsyncTask = new SimpleAccessAsyncTask(coinIds, fxCurrency);
            simpleAccessAsyncTask.execute(url);

            return simpleAccessAsyncTask.get();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return  null;

    }

    private static class SimpleAccessAsyncTask extends AsyncTask<URL, Void, ArrayList<Crypto>>{
        List<String> coinIds;
        CurrencyIdentifier fxCurrency;

        public SimpleAccessAsyncTask(List<String> coinIds, CurrencyIdentifier fxCurrency) {
            super();
            this.coinIds = coinIds;
            this.fxCurrency = fxCurrency;
        }


        @Override
        protected ArrayList<Crypto> doInBackground(URL... urls) {
            return getSimpleAccessList(urls[0], fxCurrency, coinIds);
        }
    }

    // Crypto returned only have id as field therefore need completion which for efficiency reasons has to be done outside
    private static ArrayList<Crypto> getSimpleAccessList(URL url, CurrencyIdentifier fxCurrency, List<String> coinIds){
        ArrayList<Crypto> list = new ArrayList<>();
        String fxTicker = fxCurrency.getTicker();
        try{


            URLConnection conn = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()) );

            String line = null;

            StringBuffer buff = new StringBuffer();


            while( (line = reader.readLine()) !=  null ){
                buff.append(line +"\n");
            }
            reader.close();

           /* private static final String CURRENCY_RESPONSE_SIMPLE_API = "";
            private static final String CURRENCY_MARKET_CAP_RESPONSE_SIMPLE_API = "_market_cap";
            private static final String CURRENCY_24H_VOL__RESPONSE_SIMPLE_API = "_24h_vol";
            private static final String CURRENCY_24H_CHANGE__RESPONSE_SIMPLE_API = "_24h_change";
            private static final String LAST_UPDATED_AT_RESPONSE__RESPONSE_SIMPLE_API = "last_updated_at";*/
            String ticker = fxCurrency.getTicker().toLowerCase();
            String accessPrice = CURRENCY_RESPONSE_SIMPLE_API + ticker;
            String accessMarketcap =  ticker + CURRENCY_MARKET_CAP_RESPONSE_SIMPLE_API;
            String accessChange24h = ticker + CURRENCY_24H_CHANGE__RESPONSE_SIMPLE_API;
            String accessLastUpdated = LAST_UPDATED_AT_RESPONSE__RESPONSE_SIMPLE_API;

            try{
                JSONObject container = new JSONObject(  new String(buff));

                for(int i = 0; i < coinIds.size(); i++) {
                    JSONObject jsonCryptoData = container.getJSONObject(coinIds.get(i));
                    BigDecimal marketCap = new BigDecimal(jsonCryptoData.getLong(accessMarketcap)
                            , CURRENCY_CONTEXT);
                    BigDecimal price = new BigDecimal(jsonCryptoData.getDouble(accessPrice), CURRENCY_CONTEXT);
                    BigDecimal change24h = new BigDecimal(jsonCryptoData.getDouble(accessChange24h),
                            CURRENCY_CONTEXT);
                    Crypto crypto = new Crypto(coinIds.get(i), null, null);
                    MarketData data = new MarketData(0);
                    data.setMarketCap(fxCurrency.getTicker(), marketCap);
                    data.setPrice(fxCurrency.getTicker(), price);
                    data.setPriceChangePercentage24h(fxCurrency.getTicker(), change24h);
                    data.setLastUpdated(accessLastUpdated);
                    crypto.setMarket(data);
                    list.add(crypto);
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

        }catch (IOException e){
            e.printStackTrace();
        }



        return list;
    }





    /*
        at most
        //currency : ,
          currency_market_cap : val,
          currency_24h_vol :
          currency_24h_change :
            ..
            ..
          last_updated_at

     */




    // name, price, market, developer, community etc
    /*Parameters
        order       string
        per_page    int
        page        int
        localization string
        sparkline boolean
     */
    private static final String QUERY_COINS = "coins";

    // possible query for specific coins data with
    //  coins/{coin_id}





    // id, name, symbol
    /*
        no parameters
     */
    private static final String QUERY_COINS_LIST = "coins/list";




    // price, marketcap, volume, market related data
    /*
        vs_currency REQUIRED String
        ids string --> list of specific coins requests
        order string
        sparkline boolean
     */
    private static final String QUERY_COINS_MARKET_DATA = "coins/markets";





    /*
        REQUIRED PATH is        /coins/{id}/history

        Parameters
        id REQUIRED string
        date REQUIRED string
        localization REQUIRED string --> false to exclude localized languages in response

     */
    private static final String QUERY_HISTORICAL = "history";





    /*
        REQUIRED PATH is  /coins/{id}/market_chart

        Parameters
        id  REQUIRED string
        vs_currency REQUIRED string
        days REQUIRED string
     */
    private static final String QUERY_MARKET_CHART = "market_chart";



    private static final String ID = "id";
    private static final String SYMBOL = "symbol";
    private static final String NAME = "name";
    private static final String IMAGE = "image";
    private static final String IMAGE_THUMB = "thumb";
    private static final String IMAGE_SMALL = "small";
    private static final String IMAGE_LARGE = "large";
    private static final String MARKET_DATA = "market_data";
    private static final String CURRENT_PRICE = "current_price";
    private static final String MARKET_CAP = "market_cap";
    private static final String MARKET_CAP_RANK = "market_cap_rank";
    private static final String TOTAL_VOLUME = "total_volume";
    private static final String PRICE_CHANGE_24H = "price_change_24h";
    private static final String PRICE_CHANGE_PERCENTAGE_24H = "price_change_percentage_24h";
    private static final String PRICE_CHANGE_PERCENTAGE_7D = "price_change_percentage_7d";
    private static final String PRICE_CHANGE_24H_IN_CURRENCY = "price_change_24h_in_currency";
    private static final String PRICE_CHANGE_PERCENTAGE_24H_IN_CURRENCY = "price_change_percentage_24h_in_currency";

    private static final String PRICE_CHANGE_PERCENTAGE_7D_IN_CURRENCY = "price_change_percentage_7d_in_currency";
    private static final String CIRCULATING_SUPPLY = "circulating_supply";
    private static final String LAST_UPDATED_FIELD = "last_updated";

    public static MarketData getMarketData(String id){
        try{
            URL url = new URL( API_MAIN_URL + QUERY_COINS +"/" + id );

            MarketDataAccessAsyncTask task = new MarketDataAccessAsyncTask();
            task.execute(url);
            MarketData data = task.get();
            return data;
        }catch (IOException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }catch (ExecutionException e){
            e.printStackTrace();
        }


        return null;
    }


    private static class MarketDataAccessAsyncTask extends AsyncTask<URL, Void, MarketData>{
        @Override
        protected MarketData doInBackground(URL... urls) {
            URL url = urls[0];

            try{
                BufferedReader reader = new BufferedReader( new InputStreamReader(url.openStream()) );

                String line = null;
                StringBuffer sb = new StringBuffer();
                while( (line = reader.readLine()) != null){
                    sb.append(line);
                }

                JSONObject jsonCrypto = new JSONObject( new String(sb));
                JSONObject jsonMarketData = jsonCrypto.getJSONObject(MARKET_DATA);

                JSONObject jsonCurrentPrice = jsonMarketData.getJSONObject(CURRENT_PRICE);
                JSONObject jsonMarketCap = jsonMarketData.getJSONObject(MARKET_CAP);
                int marketCapRank = jsonMarketData.getInt(MARKET_CAP_RANK);
                double circulatingSupply = jsonMarketData.getDouble(CIRCULATING_SUPPLY);

                JSONObject jsonTotalVolume = jsonMarketData.getJSONObject(TOTAL_VOLUME);
                JSONObject jsonPriceChange24h  = jsonMarketData.getJSONObject(PRICE_CHANGE_24H_IN_CURRENCY);
                JSONObject jsonPriceChangePercentage24h = jsonMarketData.getJSONObject(PRICE_CHANGE_PERCENTAGE_24H_IN_CURRENCY);


                System.out.println(url.toString() +"  " +new String(sb));

                MarketData marketData = new MarketData(marketCapRank);

                String lastUpdated = jsonCrypto.getString(LAST_UPDATED_FIELD);
                marketData.setLastUpdated(lastUpdated);
                Iterator<String> currencyKeys = jsonCurrentPrice.keys();
                   // System.out.println(name);
                while(currencyKeys.hasNext()){

                    String currency = currencyKeys.next();

                    double priceInCurrency = jsonCurrentPrice.getDouble(currency);
                    double marketCapInCurrency = jsonMarketCap.getDouble(currency);
                    double totalVolume = jsonTotalVolume.getDouble(currency);
                    double priceChange24h = jsonPriceChange24h.getDouble(currency);
                    double priceChangePercentage24h = jsonPriceChangePercentage24h.getDouble(currency);

                    marketData.setPrice(currency, BigDecimal.valueOf(priceInCurrency));
                    marketData.setMarketCap(currency, BigDecimal.valueOf(marketCapInCurrency));
                    marketData.setTotalVolume(currency, BigDecimal.valueOf(totalVolume));
                    marketData.setPriceChange24h(currency, BigDecimal.valueOf(priceChange24h));
                    marketData.setPriceChangePercentage24h(currency, BigDecimal.valueOf(priceChangePercentage24h));
                    //System.out.println( BigDecimal.valueOf(priceChangePercentage24h));
                }

                //System.out.println(marketData.getPriceChangePercentage24h("usd"));


                reader.close();

                return marketData;

            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }



            return null;
        }
    }


    // fills only id, name, symbol, marketCapRank

    // GENERALIZED TO SELECTING ANY CURRENCY
   /* public static ArrayList<Crypto> getRankedList() {

        try {
            URL url = new URL(API_MAIN_URL +
                    QUERY_COINS_MARKET_DATA +
                    "?vs_currency=usd&order=market_cap_desc"
                    //QUERY_COINS
                    //+ "?order=market_cap_desc&per_page=1000000"
            );
            RankedAccessAsyncTask rankedAccessTask = new RankedAccessAsyncTask();
            rankedAccessTask.execute(url);

            return rankedAccessTask.get();

        } catch (IOException e) {
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }catch (ExecutionException e){
            e.printStackTrace();
        }


        return null;
    }*/

    public static ArrayList<Crypto> getRankedList(CurrencyIdentifier fxCurrency) {

        try {
            URL url = new URL(API_MAIN_URL +
                    QUERY_COINS_MARKET_DATA +
                    "?vs_currency=" + fxCurrency.getTicker() + "&order=market_cap_desc"
                    //QUERY_COINS
                    //+ "?order=market_cap_desc&per_page=1000000"
            );
            RankedAccessAsyncTask rankedAccessTask = new RankedAccessAsyncTask(fxCurrency);
            rankedAccessTask.execute(url);

            return rankedAccessTask.get();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return  null;
    }


    public static void addToRankedList(final ArrayList<Crypto> list) {

        try {
            final URL url = new URL(API_MAIN_URL +
                    QUERY_COINS_MARKET_DATA +
                    "?vs_currency=usd&order=market_cap_desc"
                    //QUERY_COINS
                    //+ "?order=market_cap_desc&per_page=1000000"
            );
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    addToList(url, list);
                }
            });

            t.start();



        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    private static class RankedAccessAsyncTask extends AsyncTask<URL, Void, ArrayList<Crypto>>{
        CurrencyIdentifier fxCurrency;
        public RankedAccessAsyncTask(CurrencyIdentifier fxCurrency) {
            super();
            this.fxCurrency = fxCurrency;
        }


        @Override
        protected ArrayList<Crypto> doInBackground(URL... urls) {
            return getList(urls[0], fxCurrency);
        }
    }


    private static ArrayList<Crypto> getList(URL url){
        ArrayList<Crypto> list = new ArrayList<>();

        try{


            URLConnection conn = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()) );

            String line = null;

            StringBuffer buff = new StringBuffer();


            while( (line = reader.readLine()) !=  null ){
                buff.append(line +"\n");
            }

            try{
                JSONArray array = new JSONArray(  new String(buff));


                for(int i = 0; i < array.length(); i++){


                    JSONObject jsonCrypto = array.getJSONObject(i);

                    String id = jsonCrypto.getString(ID);
                    String symbol = jsonCrypto.getString(SYMBOL);
                    String name = jsonCrypto.getString(NAME);


                    String marketCapRankStr = jsonCrypto.getString(MARKET_CAP_RANK);
                    String marketCapStr = jsonCrypto.getString(MARKET_CAP);
                    //JSONObject jsonMarketData = jsonCrypto.getJSONObject(MARKET_DATA);



                    double currentUsdPrice = jsonCrypto.getDouble(CURRENT_PRICE);
                    long usdMarketCap = jsonCrypto.getLong(MARKET_CAP);
                    long circulatingSupply = jsonCrypto.getLong(CIRCULATING_SUPPLY);

                    long usdTotalVolume = jsonCrypto.getLong(TOTAL_VOLUME);
                    double usdPriceChange24h  = jsonCrypto.getDouble(PRICE_CHANGE_24H);
                    double usdPriceChangePercentage24h = jsonCrypto.getDouble(PRICE_CHANGE_PERCENTAGE_24H);

                    if(  (marketCapRankStr.equals(ABSENT_VALUE) || marketCapStr.equals(ABSENT_VALUE) ) == false ){

                        int marketCapRank = Integer.valueOf(marketCapRankStr);
                        double marketCap = Double.valueOf(marketCapRank);


                        // exclusion of low cap coins with no volume
                        if(marketCap > 0){

                            Crypto crypto = new Crypto(id, symbol, name, marketCapRank);
                            crypto.setCirculatingSupply(circulatingSupply);

                            MarketData marketData = crypto.getMarket();
                            String usedCurrency = "usd";

                            marketData.setMarketCap(usedCurrency, BigDecimal.valueOf(usdMarketCap));
                            marketData.setPrice(usedCurrency, BigDecimal.valueOf(currentUsdPrice));
                            marketData.setTotalVolume(usedCurrency, BigDecimal.valueOf(usdTotalVolume));
                            marketData.setPriceChange24h(usedCurrency, BigDecimal.valueOf(usdPriceChange24h));
                            marketData.setPriceChangePercentage24h(usedCurrency, BigDecimal.valueOf(usdPriceChangePercentage24h));

                            list.add(crypto);
                        }
                    }



                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        Collections.sort(list,new Comparator<Crypto>() {
            @Override
            public int compare(Crypto c1, Crypto c2) {
                return c1.getMarket().getMarketCapRank() - c2.getMarket().getMarketCapRank();
            }
        } );

        return list;
    }
    private static ArrayList<Crypto> getList(URL url, CurrencyIdentifier fxCurrency){
        ArrayList<Crypto> list = new ArrayList<>();
        String fxTicker = fxCurrency.getTicker();
        try{


            URLConnection conn = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()) );

            String line = null;

            StringBuffer buff = new StringBuffer();


            while( (line = reader.readLine()) !=  null ){
                buff.append(line +"\n");
            }

            try{
                JSONArray array = new JSONArray(  new String(buff));

                //System.out.println("FROM INNER CYCLE");
                for(int i = 0; i < array.length(); i++){


                    JSONObject jsonCrypto = array.getJSONObject(i);

                    String id = jsonCrypto.getString(ID);
                    String symbol = jsonCrypto.getString(SYMBOL);
                    String name = jsonCrypto.getString(NAME);


                    String marketCapRankStr = jsonCrypto.getString(MARKET_CAP_RANK);
                    String marketCapStr = jsonCrypto.getString(MARKET_CAP);
                    //JSONObject jsonMarketData = jsonCrypto.getJSONObject(MARKET_DATA);



                    double currentPrice = jsonCrypto.getDouble(CURRENT_PRICE);
                    long marketCap = jsonCrypto.getLong(MARKET_CAP);
                    long circulatingSupply = jsonCrypto.getLong(CIRCULATING_SUPPLY);

                    long totalVolume = jsonCrypto.getLong(TOTAL_VOLUME);
                    double priceChange24h  = jsonCrypto.getDouble(PRICE_CHANGE_24H);
                    double priceChangePercentage24h = jsonCrypto.getDouble(PRICE_CHANGE_PERCENTAGE_24H);

                    if(  (marketCapRankStr.equals(ABSENT_VALUE) || marketCapStr.equals(ABSENT_VALUE) ) == false ){

                        int marketCapRank = Integer.valueOf(marketCapRankStr);
                        //double marketCap = Double.valueOf(marketCapRank);


                        // exclusion of low cap coins with no volume
                        if(marketCap > 0){

                            Crypto crypto = new Crypto(id, symbol, name, marketCapRank);
                            crypto.setCirculatingSupply(circulatingSupply);

                            MarketData marketData = crypto.getMarket();

                            marketData.setMarketCap(fxTicker, BigDecimal.valueOf(marketCap));
                            marketData.setPrice(fxTicker, BigDecimal.valueOf(currentPrice));
                            marketData.setTotalVolume(fxTicker, BigDecimal.valueOf(totalVolume));
                            marketData.setPriceChange24h(fxTicker, BigDecimal.valueOf(priceChange24h));
                            marketData.setPriceChangePercentage24h(fxTicker, BigDecimal.valueOf(priceChangePercentage24h));
                            //System.out.println(crypto.getSymbol() + " " + " fxTicker" + fxTicker  + " "  + marketData.getPrice(fxTicker));
                            list.add(crypto);
                        }
                    }



                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        Collections.sort(list,new Comparator<Crypto>() {
            @Override
            public int compare(Crypto c1, Crypto c2) {
                return c1.getMarket().getMarketCapRank() - c2.getMarket().getMarketCapRank();
            }
        } );

        return list;
    }


    private static void addToList(URL url,final ArrayList<Crypto> list){
        try{


            URLConnection conn = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()) );

            String line = null;

            StringBuffer buff = new StringBuffer();


            while( (line = reader.readLine()) !=  null ){
                buff.append(line +"\n");
            }

            try{
                JSONArray array = new JSONArray(  new String(buff));


                for(int i = 0; i < array.length(); i++){


                    JSONObject jsonCrypto = array.getJSONObject(i);

                    String id = jsonCrypto.getString(ID);
                    String symbol = jsonCrypto.getString(SYMBOL);
                    String name = jsonCrypto.getString(NAME);


                    String marketCapRankStr = jsonCrypto.getString(MARKET_CAP_RANK);
                    String marketCapStr = jsonCrypto.getString(MARKET_CAP);
                    if(  (marketCapRankStr.equals(ABSENT_VALUE) || marketCapStr.equals(ABSENT_VALUE) ) == false ){

                        int marketCapRank = Integer.valueOf(marketCapRankStr);
                        double marketCap = Double.valueOf(marketCapRank);


                        // exclusion of low cap coins with no volume
                        if(marketCap > 0){

                            Crypto crypto = new Crypto(id, symbol, name, marketCapRank);


                            list.add(crypto);
                        }
                    }


                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        Collections.sort(list,new Comparator<Crypto>() {
            @Override
            public int compare(Crypto c1, Crypto c2) {
                return c1.getMarket().getMarketCapRank() - c2.getMarket().getMarketCapRank();
            }
        } );

    }




}
