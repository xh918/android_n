package com.nexlabs.comnexlabsnanowidget.geckoapi;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class PriceGroup {
    @SerializedName("prices")
    BigDecimal prices[][];

    @SerializedName("market_caps")
    BigDecimal marketCaps[][];

    public PriceGroup(BigDecimal[][] prices, BigDecimal[][] marketCaps) {
        this.prices = prices;
        this.marketCaps = marketCaps;
    }

    public BigDecimal[][] getPrices() {
        return prices;
    }

    public BigDecimal[][] getMarketCaps() {
        return marketCaps;
    }

    public void setPrices(BigDecimal[][] prices) {
        this.prices = prices;
    }

    public void setMarketCaps(BigDecimal[][] marketCaps) {
        this.marketCaps = marketCaps;
    }
}
