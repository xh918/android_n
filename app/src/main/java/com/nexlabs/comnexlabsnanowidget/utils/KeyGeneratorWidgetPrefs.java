package com.nexlabs.comnexlabsnanowidget.utils;

public class KeyGeneratorWidgetPrefs {

    public static final String IDENTIFIER_STANDARD_SELECTION = "bitcoin";
    public static final String SYMBOL_STANDARD_SELECTION = "BTC";
    public static final String NAME_STANDARD_SELECTION = "Bitcoin";

    public static final String STANDARD_FX_CURRENCY_TICKER = "USD";
    public static final String STANDARD_FX_CURRENCY_SYMBOL = "$";
    public static String getKey(int widgetId, String suffix){
        return  widgetId + suffix;
    }

}
