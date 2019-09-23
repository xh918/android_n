package com.nexlabs.comnexlabsnanowidget.geckoapi;

import java.math.BigDecimal;

public class SimplePriceData {
    long unixTime;
    BigDecimal priceData;

    public SimplePriceData(BigDecimal priceData, long unixTime){
        this.unixTime = unixTime;
        this.priceData = priceData;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    public BigDecimal getPriceData() {
        return priceData;
    }

    public void setPriceData(BigDecimal priceData) {
        this.priceData = priceData;
    }
}
