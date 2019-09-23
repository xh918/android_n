package com.nexlabs.comnexlabsnanowidget.geckoapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MarketChartDeserializer implements JsonDeserializer<List<Price>> {
    private static final String PRICE_ARRAY_FIELD = "prices";
    /*
     List items = new ArrayList<>();

          final JsonObject jsonObject = json.getAsJsonObject();
          final Boolean success = jsonObject.get("success").getAsBoolean();
          final JsonArray itemsJsonArray = jsonObject.get("data").getAsJsonArray();

          for (JsonElement itemsJsonElement : itemsJsonArray) {
              final JsonObject itemJsonObject = itemsJsonElement.getAsJsonObject();
              final String id = itemJsonObject.get("id").getAsString();
              final String name = itemJsonObject.get("name").getAsString();
              final int amount = itemJsonObject.get("amount").getAsInteger();

              items.add(new Item(id, name, amount));
          }

          return items;
     */
    @Override
    public List<Price> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Price> prices = new ArrayList<>();

        JsonObject pricesObject = json.getAsJsonObject();

        JsonArray pricesArray = pricesObject.get(PRICE_ARRAY_FIELD).getAsJsonArray();

        for( JsonElement element : pricesArray){

            JsonArray singlePrice = element.getAsJsonArray();
            long unixTimeStamp = singlePrice.get(0).getAsLong();
            BigDecimal price = singlePrice.get(1).getAsBigDecimal();

            prices.add(new Price(price, unixTimeStamp));

        }

        return prices;
    }
}
