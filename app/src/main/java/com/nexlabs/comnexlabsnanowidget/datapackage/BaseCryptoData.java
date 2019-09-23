package com.nexlabs.comnexlabsnanowidget.datapackage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BaseCryptoData {
    String id;
    Map<String, String> localizedName;
    String symbol;
    long totalSupply;
    long circulatingSupply;
    Map<String, String> localizedDescription;
    Map<String, Long> marketCap;
    Map<String, Long> totalVolume;
    Map<String, BigDecimal> currentPrice;


    public BaseCryptoData(String id, String symbol) {
        this.id = id;
        this.symbol = symbol;

        this.localizedName = new HashMap<>();
        this.localizedDescription = new HashMap<>();

        this.marketCap = new HashMap<>();
        this.totalVolume = new HashMap<>();

        this.currentPrice = new HashMap<>();
    }
    public BaseCryptoData(String id, String symbol,  long totalSupply, long circulatingSupply) {
        this.id = id;
        this.symbol = symbol;
        this.totalSupply = totalSupply;
        this.circulatingSupply = circulatingSupply;

        this.localizedName = new HashMap<>();
        this.localizedDescription = new HashMap<>();

        this.marketCap = new HashMap<>();
        this.totalVolume = new HashMap<>();

        this.currentPrice = new HashMap<>();
    }
    public void insertCurrentPriceCurrency(String currency, BigDecimal value){
        this.currentPrice.put(currency, value);
    }

    public void insertMarketCapCurrency(String currency, long value){
        this.marketCap.put(currency, value);
    }
    public void insertTotalVolumeCurrency(String currency, long value){
        this.totalVolume.put(currency, value);
    }
    public void insertLocalizedName(String languageId, String name){
        this.localizedName.put(languageId, name);
    }
    public void insertLocalizedDescription(String languageId, String description){
        this.localizedDescription.put(languageId, description);
    }
    public String getLocalizedName(String languageId){
        return localizedName.get(languageId);
    }
    public String getLocalizedDescription(String languageId){
        return localizedDescription.get(languageId);
    }
    public long getMarketCapCurrency(String currency){
        return this.marketCap.get(currency);
    }
    public long getTotalVolumeCurrency(String currency){
        return this.totalVolume.get(currency);
    }
    public BigDecimal getCurrentPriceCurrency(String currency){
        return this.currentPrice.get(currency);
    }


    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }



    public long getTotalSupply() {
        return totalSupply;
    }

    public long getCirculatingSupply() {
        return circulatingSupply;
    }


    public void setTotalSupply(long totalSupply) {
        this.totalSupply = totalSupply;
    }

    public void setCirculatingSupply(long circulatingSupply) {
        this.circulatingSupply = circulatingSupply;
    }
}
