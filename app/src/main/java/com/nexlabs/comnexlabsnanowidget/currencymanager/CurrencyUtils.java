package com.nexlabs.comnexlabsnanowidget.currencymanager;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyUtils {

    public static final MathContext CURRENCY_CONTEXT = new MathContext(8, RoundingMode.HALF_UP);

    public static String formatValueMagnitude(BigDecimal val){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        if(val.doubleValue() >= 1.0) {
            DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
            return df.format(val);
        }
        DecimalFormat df = new DecimalFormat("#,##0.00##", symbols);

        return df.format(val);
    }

    public static String formatValue(BigDecimal val){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#,##0.0###", symbols);

        return df.format(val);
    }

    public static String formatLongValue(Long val){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#,##0", symbols);

        return df.format(val);
    }

    public static String formatPercentageValue(BigDecimal val){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#,##0.0#", symbols);

        return df.format(val);
    }

    public static String formatCmcnum(long num) {
        char curr[] = {
                ' ',
                'K',
                'M',
                'B',
                'T'
        };

        int n = 0;

        long v = num;
        long rest = 0;
        while(v/1000 > 0) {
            rest = v%1000;
            v /=1000;

            n++;
        }

        return String.format( "%3d.%2d%c", v, rest/10, curr[n]   );
    }
}
