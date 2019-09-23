package com.nexlabs.comnexlabsnanowidget.datapackage;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class Crypto {

    private static final String ID_FIELD = "id";
    private static final String SYMBOL_FIELD = "symbol";
    private static final String NAME_FIELD = "name";

    private String id;
    private String symbol;
    private String name;
    private long circulatingSupply;

    private String thumbImgURL;
    private String smallImgURL;
    private String largeImgURL;


    private BigDecimal currentPrice;
    
//    
//    private String imgFolderKey;
//    
//    private String imagePaths[] = new String[ImgType.values().length];
  
    MarketData market;


    /*private double price;
    private long marketcap;
    private int marketcapRank;
    private long totalVolume;
    private double high24h;
    private double low24h;
    private double priceChange24h;
    private double priceChangePercentage24h;
    private double getPriceChangePercentage7d;*/


    private Crypto(){
    	
    }
    public Crypto(String id, String symbol, String name){
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.market = null;
        this.currentPrice = null;
        //imgFolderKey = ImageSet.getKey(symbol, name);
    }


    public Crypto(String id, String symbol, String name, int marketCapRank){
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.market =  new MarketData(marketCapRank);
        this.currentPrice = null;

        //imgFolderKey = ImageSet.getKey(symbol, name);
    }

    public void setCurrentPrice(BigDecimal currentPrice){
        this.currentPrice = currentPrice;
    }
    public BigDecimal getCurrentPrice(){
        return currentPrice;
    }


   /* public Crypto(String id,
                  String symbol,
                  String name,
                  long circulatingSupply,
                  String thumbImgURL,
                  String smallImgURL,
                  String largeImgURL
                  ) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.thumbImgURL = thumbImgURL;
        this.smallImgURL = smallImgURL;
        this.largeImgURL = largeImgURL;
        this.circulatingSupply = circulatingSupply;
        this.market = new MarketData(ma);
       // imgFolderKey = ImageSet.getKey(symbol, name);
    }*/

    public void setId(String id) {
        this.id = id;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCirculatingSupply(long circulatingSupply) {
        this.circulatingSupply = circulatingSupply;
    }

    public void setThumbImgURL(String thumbImgURL) {
        this.thumbImgURL = thumbImgURL;
    }

    public void setSmallImgURL(String smallImgURL) {
        this.smallImgURL = smallImgURL;
    }

    public void setLargeImgURL(String largeImgURL) {
        this.largeImgURL = largeImgURL;
    }

    public void setMarket(MarketData market) {
        this.market = market;
    }

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public long getCirculatingSupply() {
        return circulatingSupply;
    }

    public String getThumbImgURL() {
        return thumbImgURL;
    }

    public String getSmallImgURL() {
        return smallImgURL;
    }

    public String getLargeImgURL() {
        return largeImgURL;
    }

    public MarketData getMarket() {
        return market;
    }
    public String getImageRelativePath(ImgType type) {
    	return 
    			id +
    			//ImageSet.getKey(symbol, name) + 
    			"/" + type.toString() + ".png";
    }

    public String toJSONbaseData(){
        try{
            JSONObject crypto = new JSONObject();
            crypto.put(ID_FIELD, this.id);
            crypto.put(SYMBOL_FIELD, this.symbol);
            crypto.put(NAME_FIELD, this.name);
            return crypto.toString();
        }catch (JSONException e){

        }

        return null;
    }
    public static JSONObject toJSONObject(String json) throws JSONException{
        return new JSONObject(json);
    }
    public static String readId(JSONObject crypto)throws JSONException{
        return crypto.getString(ID_FIELD);
    }
    public static String readSymbol(JSONObject crypto)throws JSONException{
        return crypto.getString(SYMBOL_FIELD);
    }
    public static String readName(JSONObject crypto)throws JSONException{
        return crypto.getString(NAME_FIELD);
    }


    public static Crypto fromJSONbaseData(String json){
        Crypto crypto = null;
        try {
            JSONObject object = new JSONObject(json);
            String id = object.getString("id");
            String symbol = object.getString("symbol");
            String name = object.getString("name");
            crypto = new Crypto(id, symbol, name);

        }catch (JSONException e){

        }
        return crypto;
    }

}
