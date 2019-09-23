package com.nexlabs.comnexlabsnanowidget.geckoapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.nexlabs.comnexlabsnanowidget.datapackage.Crypto;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PortfolioServiceDeserializer implements JsonDeserializer<List<Crypto>> {

    private static final String ABSENT_VALUE = "null";

    private static final String ID_FIELD = "id";
    private static final String SYMBOL_FIELD = "symbol";
    private static final String NAME_FIELD = "name";
    private static final String IMAGE_FIELD = "image";
    private static final String CURRENT_PRICE = "current_price";
    private static final String MARKET_CAP = "market_cap";
    private static final String MARKET_CAP_RANK = "market_cap_rank";
    private static final String TOTAL_VOLUME = "total_volume";
    private static final String HIGH_24H = "high_24h";
    private static final String LOW_24H = "low_24h";
    private static final String PRICE_CHANGE_PERCENTAGE_24H = "price_change_percentage_24h";
    private static final String CIRCULATING_SUPPLY = "circulating_supply";

    @Override
    public List<Crypto> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Crypto> cryptos = new ArrayList<>();


        JsonArray cryptoArray = json.getAsJsonArray();

        for( JsonElement element : cryptoArray){

            JsonObject singleCrypto = element.getAsJsonObject();
            String id = singleCrypto.get(ID_FIELD).getAsString();
            String symbol = singleCrypto.get(SYMBOL_FIELD).getAsString();
            String name = singleCrypto.get(NAME_FIELD).getAsString();

            String imageUrl = singleCrypto.get(IMAGE_FIELD).getAsString();
            BigDecimal currentPrice = singleCrypto.get(CURRENT_PRICE).getAsBigDecimal();
            long marketCap = singleCrypto.get(MARKET_CAP).getAsLong();
            int marketCapRank = singleCrypto.get(MARKET_CAP_RANK).getAsInt();
            BigDecimal totalVolume = singleCrypto.get(TOTAL_VOLUME).getAsBigDecimal();
            BigDecimal high24h = singleCrypto.get(HIGH_24H).getAsBigDecimal();
            BigDecimal low24h = singleCrypto.get(LOW_24H).getAsBigDecimal();
            BigDecimal priceChangePercent24h = singleCrypto.get(PRICE_CHANGE_PERCENTAGE_24H).getAsBigDecimal();
            long circulatingSupply = singleCrypto.get(CIRCULATING_SUPPLY).getAsLong();

            Crypto crypto = new Crypto(id, symbol, name, marketCapRank);

            crypto.setThumbImgURL(imageUrl);
            crypto.setLargeImgURL(imageUrl);
            crypto.setSmallImgURL(imageUrl);
            crypto.setCurrentPrice(currentPrice);

            cryptos.add(crypto);

        }

        return cryptos;
    }
}
