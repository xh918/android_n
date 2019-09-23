package com.nexlabs.comnexlabsnanowidget.geckoapi;

import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GeckoChartService {
    @GET("coins/{id}/market_chart/range")
    Call<List<Price>> getChartPrices(@Path("id") String coinId,
                                     @Query("vs_currency") String fxCurrencyTicker,
                                     @Query("from") long fromUnixTimestamp,
                                     @Query("to") long toUnixTimeStamp);
    /*
    @GET("simple/price")
    Call<List<Crypto>> groupSimplePricesVsCurrencies(@Query("ids") String cryptoIds, @Query("vs_currencies") String fiatCurrencies,
                                                     @Query("include_market_cap") String includeMarketCap,
                                                     @Query("include_24hr_vol") String include24hrVol,
                                                     @Query("include_24hr_change") String include24hrChange,
                                                     @Query("includeLastUpdatedAt") String includeLastUpdatedAt);
    */
    @GET("simple/supported_vs_currencies")
    Call<List<String>> getFiatCurrencies();


    @GET("coins/{id}/market_chart/range")
    Call<ChartData> getChartPrices(@Path("id") String id, @Query("vs_currency") String currency, @Query("from") String unixFrom, @Query("to") String unixTo);

    @GET("coins/list")
    Call<List<Crypto>> getCryptoList();

    @GET("coins/{id}")
    Call<List<Crypto>> getCryptoFullDataList(@Path("id") String cryptoId);

}


