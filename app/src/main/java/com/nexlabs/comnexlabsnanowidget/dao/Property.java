package com.nexlabs.comnexlabsnanowidget.dao;

public enum Property {
	ID("id"),
    SYMBOL("symbol"),
    NAME("name"),
    IMAGE("image"),
    IMAGE_THUMB("thumb"),
    IMAGE_SMALL("small"),
    IMAGE_LARGE("large"),
    MARKET_DATA("market_data"),
    CURRENT_PRICE("current_price"),
    MARKET_CAP("market_cap"),
    MARKET_CAP_RANK("market_cap_rank"),
    TOTAL_VOLUME("total_volume"),
    PRICE_CHANGE_24H("price_change_24h"),
    PRICE_CHANGE_PERCENTAGE_24H("price_change_percentage_24h"),
    PRICE_CHANGE_PERCENTAGE_7D("price_change_percentage_7d"),
    PRICE_CHANGE_24H_IN_CURRENCY("price_change_24h_in_currency"),
    PRICE_CHANGE_PERCENTAGE_24H_IN_CURRENCY("price_change_percentage_24h_in_currency"),
    PRICE_CHANGE_PERCENTAGE_7D_IN_CURRENCY("price_change_percentage_7d_in_currency"),
    CIRCULATING_SUPPLY("circulating_supply");
    
    
	String property;
    private Property(String property) {
    	this.property = property;
    }
    
    public String toString() {
    	return this.property;
    }


}
