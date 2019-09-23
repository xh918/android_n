package com.nexlabs.comnexlabsnanowidget.geckoapi;

import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeckoPortfolioService {

    public static final String USD = "usd";
    public static final String JPY = "jpy";
    public static final String EUR = "eur";

    public static final String MARKET_CAP_DESC = "market_cap_desc";
    public static final String GECKO_DESC = "gecko_desc";
    public static final String GECKO_ASC = "gecko_asc";
    public static final String MARKET_CAP_ASC = "market_cap_asc";
    public static final String VOLUME_ASC = "volume_asc";
    public static final String VOLUME_DESC = "volume_desc";

    public static final String PRICE_CHANGE_1H = "1h";
    public static final String PRICE_CHANGE_24H = "24h";
    public static final String PRICE_CHANGE_7D = "7d";
    public static final String PRICE_CHANGE_14D = "14d";
    public static final String PRICE_CHANGE_30D = "30d";
    public static final String PRICE_CHANGE_200D = "200d";
    public static final String PRICE_CHANGE_1Y = "1y";




    @GET("coins/markets")
    Call<List<Crypto>> getRankedList(@Query("vs_currency") String vsCurrency,
                                     @Query("order") String order,
                                     @Query("per_page") int perPage,
                                     @Query("page") int page,
                                     @Query("sparkline") boolean hasSparkline,
                                     @Query("price_change_percentage") String changePercentPeriod);

}
