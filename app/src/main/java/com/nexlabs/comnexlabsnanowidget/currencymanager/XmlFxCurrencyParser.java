package com.nexlabs.comnexlabsnanowidget.currencymanager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import static com.nexlabs.comnexlabsnanowidget.currencymanager.CurrencyUtils.CURRENCY_CONTEXT;

public class XmlFxCurrencyParser {

    // data read from central bank has euro as base currency (EUR/xxx)

    private static final String currenciesURLString = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private static final int CURRENCY_ATTR_POSITION = 0;
    private static final int RATE_ATTR_POSITION = 1;

    private static final String CURRENCY_ATTR_KEY = "currency";
    private static final String RATE_ATTR_KEY = "rate";




    public static Map<String, FxCurrency> parse(){
        try{
            TreeMap<String, FxCurrency> currencyTreeMap = new TreeMap<>();
            URL url = new URL(currenciesURLString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line = null;
            StringBuffer data = new StringBuffer();
            while( ( line = reader.readLine() ) != null ){

                data.append(line);
            }
            String input = new String(data);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(new StringReader(input));


            // self loop to identify base currency
            FxCurrency eur = new FxCurrency("EUR", new BigDecimal(1));
            currencyTreeMap.put(eur.getTicker(), eur);

            int eventType = XmlPullParser.START_DOCUMENT;
            while( (eventType = parser.getEventType() )  != XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_TAG &&
                            parser.getAttributeCount() == 2 &&
                            parser.getAttributeName(CURRENCY_ATTR_POSITION).equals(CURRENCY_ATTR_KEY) &&
                            parser.getAttributeName(RATE_ATTR_POSITION).equals(RATE_ATTR_KEY)
                        ){
                    String ticker = parser.getAttributeValue(CURRENCY_ATTR_POSITION);
                    BigDecimal val = new BigDecimal(parser.getAttributeValue(RATE_ATTR_POSITION), CURRENCY_CONTEXT);

                    // 1/(EURxxx) gives the rate for currency against eur
                    BigDecimal rateAgainstEUR =  new BigDecimal(1, CURRENCY_CONTEXT).divide( val, CURRENCY_CONTEXT );

                    FxCurrency currency = new FxCurrency(ticker, rateAgainstEUR);
                    currencyTreeMap.put(ticker, currency );
                }
                parser.next();
            }
            reader.close();
            return currencyTreeMap;
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }


        return  null;
    }



}
