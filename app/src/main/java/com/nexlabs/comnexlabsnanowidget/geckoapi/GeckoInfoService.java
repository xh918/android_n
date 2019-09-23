package com.nexlabs.comnexlabsnanowidget.geckoapi;

import com.nexlabs.comnexlabsnanowidget.datapackage.AssetInfo;
import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GeckoInfoService {
    /*
    @GET("simple/supported_vs_currencies")
    Call<List<String>> getFiatCurrencies();
    */

    @GET("coins/{id}")
    Call<AssetInfo> getCryptoFullDataList(@Path("id") String cryptoId,
                                          @Query("localization") boolean hasLocalizedResults,
                                          @Query("tickers") boolean hasTickers,
                                          @Query("market_data") boolean hasMarketData,
                                          @Query("community_data") boolean hasCommunityData,
                                          @Query("developer_data") boolean hasDeveloperData,
                                          @Query("sparkline") boolean hasSparklineData);
}
