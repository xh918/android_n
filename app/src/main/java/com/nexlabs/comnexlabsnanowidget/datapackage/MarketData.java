package com.nexlabs.comnexlabsnanowidget.datapackage;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public class MarketData {
    private int marketCapRank;
    //private long circulatingSupply;


    private Map<String, BigDecimal> currentPrice;
    private Map<String, Long> marketCap;
    private Map<String, BigDecimal> totalVolume;
    private Map<String, BigDecimal> high24h;
    private Map<String, BigDecimal> low24h;

    private  Map<String, BigDecimal> priceChange24h;
    private Map<String, BigDecimal> priceChangePercentage24h;
    private Map<String, BigDecimal> priceChangePercentage7d;
    private  Map<String, BigDecimal> priceChangePercentage14d;
    private   Map<String, BigDecimal> priceChangePercentage30d;
    private   Map<String, BigDecimal> priceChangePercentage60d;
    private   Map<String, BigDecimal> priceChangePercentage200d;
    private    Map<String, BigDecimal> priceChangePercentage1y;

    private   Map<String, BigDecimal> marketCapChange24h;
    private   Map<String, BigDecimal> marketCapChangePercentage24h;
    private String  lastUpdated;

    public MarketData() {
        //,
        //long circulatingSupply
            this.marketCapRank = 0;
            //this.circulatingSupply = circulatingSupply;

            currentPrice = new TreeMap<>();
            marketCap = new TreeMap<>();
            totalVolume = new TreeMap<>();
            high24h = new TreeMap<>();
            low24h = new TreeMap<>();

            priceChange24h = new TreeMap<>();
            priceChangePercentage24h = new TreeMap<>();
            priceChangePercentage7d = new TreeMap<>();
            priceChangePercentage14d = new TreeMap<>();
            priceChangePercentage30d = new TreeMap<>();
            priceChangePercentage60d = new TreeMap<>();
            priceChangePercentage200d = new TreeMap<>();
            priceChangePercentage1y = new TreeMap<>();

            marketCapChange24h = new TreeMap<>();
            marketCapChangePercentage24h = new TreeMap<>();

        }


    public MarketData(int marketCapRank
            //,
                      //long circulatingSupply
                        ) {
        this.marketCapRank = marketCapRank;
        //this.circulatingSupply = circulatingSupply;

        currentPrice = new TreeMap<>();
        marketCap = new TreeMap<>();
        totalVolume = new TreeMap<>();
        high24h = new TreeMap<>();
        low24h = new TreeMap<>();

        priceChange24h = new TreeMap<>();
        priceChangePercentage24h = new TreeMap<>();
        priceChangePercentage7d = new TreeMap<>();
        priceChangePercentage14d = new TreeMap<>();
        priceChangePercentage30d = new TreeMap<>();
        priceChangePercentage60d = new TreeMap<>();
        priceChangePercentage200d = new TreeMap<>();
        priceChangePercentage1y = new TreeMap<>();

        marketCapChange24h = new TreeMap<>();
        marketCapChangePercentage24h = new TreeMap<>();


    }

    public void setLastUpdated(String lastUpdated){
        this.lastUpdated = lastUpdated;
    }
    public String getLastUpdated(){
        return lastUpdated;
    }

    public void setMarketCapRank(int rank) {
        this.marketCapRank = rank;
    }

    public int getMarketCapRank() {
        return marketCapRank;
    }
/*
    public long getCirculatingSupply() {
        return circulatingSupply;
    }*/

    public void setPrice(String currency, BigDecimal price){
        currentPrice.put(currency, price);
    }
    public void setMarketCap(String currency, BigDecimal marketcap){
        marketCap.put(currency, marketcap.longValue());
    }
    public void setTotalVolume(String currency, BigDecimal totVolume){
        totalVolume.put(currency, totVolume);
    }
    public void setHigh24h(String currency, BigDecimal priceHigh){
        high24h.put(currency, priceHigh);
    }
    public void setLow24h(String currency, BigDecimal priceLow){
        priceChange24h.put(currency, priceLow);
    }

    public void setPriceChange24h(String currency, BigDecimal priceChange24hval){
        priceChange24h.put(currency, priceChange24hval);

    }
    public void setPriceChangePercentage24h(String currency, BigDecimal priceChange24hval){
        priceChangePercentage24h.put(currency, priceChange24hval);

    }
    public void setPriceChangePercentage7d(String currency, BigDecimal priceChangePercentage7dval){
        priceChangePercentage7d.put(currency, priceChangePercentage7dval);
    }
    public void setPriceChangePercentage14d(String currency, BigDecimal priceChangePercentage14dval){
        priceChangePercentage14d.put(currency, priceChangePercentage14dval);
    }
    public void setPriceChangePercentage30d(String currency, BigDecimal priceChangePercentage30dval){
        priceChangePercentage30d.put(currency, priceChangePercentage30dval);
    }
    public void setPriceChangePercentage60d(String currency, BigDecimal priceChangePercentage60dval){
        priceChangePercentage60d.put(currency, priceChangePercentage60dval);
    }
    public void setPriceChangePercentage200d(String currency, BigDecimal priceChangePercentage200dval){
        priceChangePercentage200d.put(currency, priceChangePercentage200dval);
    }
    public void setPriceChangePercentage1y(String currency, BigDecimal priceChangePercentage1yval){
        priceChangePercentage1y.put(currency, priceChangePercentage1yval);
    }

    public void setMarketCapChange24h(String currency, BigDecimal marketcapChange24hval){
        marketCapChange24h.put(currency, marketcapChange24hval);
    }
    public void setMarketCapChangePercentage24h(String currency, BigDecimal marketcapChangePercentage24hval){
        marketCapChangePercentage24h.put(currency, marketcapChangePercentage24hval);
    }




    public BigDecimal getPrice(String currency){
        return currentPrice.get(currency);
    }
    public Long getMarketCap(String currency){
        return  marketCap.get(currency);
    }
    public BigDecimal getTotalVolume(String currency){
        return totalVolume.get(currency);
    }
    public BigDecimal getHigh24h(String currency){
        return  high24h.get(currency);
    }
    public BigDecimal getLow24h(String currency){
        return priceChange24h.get(currency);
    }

    public BigDecimal getPriceChange24h(String currency){
        return priceChange24h.get(currency);
    }
    public BigDecimal getPriceChangePercentage24h(String currency){
        return priceChangePercentage24h.get(currency);
    }
    public BigDecimal getPriceChangePercentage7d(String currency){
        return priceChangePercentage7d.get(currency);
    }
    public BigDecimal getPriceChangePercentage14d(String currency){
        return priceChangePercentage14d.get(currency);
    }
    public BigDecimal getPriceChangePercentage30d(String currency){
        return priceChangePercentage30d.get(currency);
    }
    public BigDecimal getPriceChangePercentage60d(String currency){
        return  priceChangePercentage60d.get(currency);
    }
    public BigDecimal getPriceChangePercentage200d(String currency){
        return  priceChangePercentage200d.get(currency);
    }
    public BigDecimal getPriceChangePercentage1y(String currency){
        return  priceChangePercentage1y.get(currency);
    }

    public BigDecimal getMarketCapChange24h(String currency){
        return  marketCapChange24h.get(currency);
    }
    public BigDecimal getMarketCapChangePercentage24h(String currency){
        return marketCapChangePercentage24h.get(currency);
    }





}
