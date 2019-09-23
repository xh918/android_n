package com.nexlabs.comnexlabsnanowidget.currencymanager;

import android.content.Context;

import com.nexlabs.comnexlabsnanowidget.currencymanager.symbols.SymbolFactory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nexlabs.comnexlabsnanowidget.currencymanager.CurrencyUtils.CURRENCY_CONTEXT;


public class ExchangeRates {


    private static ExchangeRates rates = null;
    private static SymbolFactory symbols = null;

    private Map<String, FxCurrency> currencies = null;


    private ExchangeRates(){

    }

    public static ExchangeRates getInstance(Context context){
        if(rates == null){
            rates = new ExchangeRates();
            rates.currencies = new TreeMap<>();
            symbols = SymbolFactory.getInstance(context);
            //rates.currencies = XmlFxCurrencyParser.parse();
        }
        return rates;
    }
    public synchronized void updateCurrencies(){
        rates.currencies.clear();
        rates.currencies.putAll(XmlFxCurrencyParser.parse());

        Iterator<Map.Entry<String,FxCurrency >> it = rates.currencies.entrySet().iterator();

        while(it.hasNext()){
            Map.Entry<String, FxCurrency> entry = it.next();
            if( ! symbols.isSupported(entry.getKey())){
                it.remove();
            }
        }

    }

    public FxCurrency getCurrency(String ticker){
        return currencies.get(ticker);
    }

    public Collection<FxCurrency> getCurrencies(){
        return currencies.values();
    }

    public Collection<String> getTickers(){
        return  currencies.keySet();
    }

    public BigDecimal getRate(String baseCurrency, String counterCurrency ) throws  UnsupportedCurrencyException{
        if( (!currencies.containsKey(baseCurrency) ) || (!currencies.containsKey(counterCurrency) )  ){
            throw new UnsupportedCurrencyException();
        }
        //          JPY/EUR
        BigDecimal baseRateToEur = currencies.get(baseCurrency).getRateAgainstEUR();
        //          CAD/EUR
        BigDecimal counterRateToEur = currencies.get(counterCurrency).getRateAgainstEUR();

        //          EUR/CAD
        BigDecimal eurToCounterRate = new BigDecimal(1, CURRENCY_CONTEXT).divide(counterRateToEur, CURRENCY_CONTEXT);
        //          JPY/CAD
        return baseRateToEur.multiply(eurToCounterRate);

    }

    public BigDecimal getRate(String quote) throws QuoteFormattingException, UnsupportedCurrencyException{
        if(quote == null){
            throw new QuoteFormattingException();
        }
        // for forex 3 letters in ticker is a always valid, in stocks instead the separator would be required
        Pattern quotePattern = Pattern.compile("([A-Za-z]{3})([^$A-Za-z\n\r])?([A-Za-z]{3})" );
        Matcher m = quotePattern.matcher(quote.toUpperCase());

        if(m.matches()){
            return getRate(m.group(1), m.group(3));

        }else{
            throw new QuoteFormattingException();
        }





    }

    public BigDecimal convert(BigDecimal value, String base, String counter) throws  UnsupportedCurrencyException{
        if(base != null && counter != null && value != null){
            BigDecimal conversionRate = getRate(base, counter);
            return value.multiply(conversionRate, CurrencyUtils.CURRENCY_CONTEXT);
        }else{
            throw new UnsupportedCurrencyException();
        }

    }


    public boolean isCurrency(String ticker){
        return currencies.containsKey(ticker);
    }







}
