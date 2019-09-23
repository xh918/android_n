package com.nexlabs.comnexlabsnanowidget.geckoapi;

import java.math.BigDecimal;

public class Price {
	BigDecimal value;
	//String fxCurrencyTicker;
	long unixTimeStamp;
	//long fromUnixTimeStamp;
	//long toUnixTimeStamp;


	public Price(BigDecimal value, long unixTimeStamp) {
		this.value = value;
		this.unixTimeStamp = unixTimeStamp;
	}


	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public void setUnixTimeStamp(long unixTimeStamp) {
		this.unixTimeStamp = unixTimeStamp;
	}

	public BigDecimal getValue() {
		return value;
	}

	public long getUnixTimeStamp() {
		return unixTimeStamp;
	}
	/*
	public Price(BigDecimal value, String fxCurrencyTicker, long fromUnixTimeStamp, long toUnixTimeStamp) {
		super();
		this.value = value;
		this.fxCurrencyTicker = fxCurrencyTicker;
		this.fromUnixTimeStamp = fromUnixTimeStamp;
		this.toUnixTimeStamp = toUnixTimeStamp;
	}
	
	*/

	/*

	public String getFxCurrencyTicker() {
		return fxCurrencyTicker;
	}

	public void setFxCurrencyTicker(String fxCurrencyTicker) {
		this.fxCurrencyTicker = fxCurrencyTicker;
	}

	public long getFromUnixTimeStamp() {
		return fromUnixTimeStamp;
	}

	public void setFromUnixTimeStamp(long fromUnixTimeStamp) {
		this.fromUnixTimeStamp = fromUnixTimeStamp;
	}

	public long getToUnixTimeStamp() {
		return toUnixTimeStamp;
	}

	public void setToUnixTimeStamp(long toUnixTimeStamp) {
		this.toUnixTimeStamp = toUnixTimeStamp;
	}

	*/
	
}
