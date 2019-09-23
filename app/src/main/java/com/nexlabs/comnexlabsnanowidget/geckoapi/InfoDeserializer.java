package com.nexlabs.comnexlabsnanowidget.geckoapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.nexlabs.comnexlabsnanowidget.datapackage.AssetInfo;
import com.nexlabs.comnexlabsnanowidget.datapackage.BaseCryptoData;
import com.nexlabs.comnexlabsnanowidget.datapackage.CommunityData;
import com.nexlabs.comnexlabsnanowidget.datapackage.DeveloperData;
import com.nexlabs.comnexlabsnanowidget.datapackage.ImageData;
import com.nexlabs.comnexlabsnanowidget.datapackage.LinkData;
import com.nexlabs.comnexlabsnanowidget.datapackage.PublicInterestStats;
import com.nexlabs.comnexlabsnanowidget.fragments.AssetDetailsFragment;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class InfoDeserializer implements JsonDeserializer<AssetInfo> {
    private static final String ABSENT_VALUE = "null";

    private static final String ID_FIELD = "id";
    private static final String SYMBOL_FIELD = "symbol";
    private static final String NAME_FIELD = "name";

    // LOCALIZATION DATA
    // to then find each language---> if it is empty then default to english lang
    private static final String LOCALIZATION_NAME_FIELD = "localization";
    private static final String LOCALIZATION_DESCRIPTION_FIELD = "description";

    // LINKS DATA
    private static final String LINKS_FIELD = "links";
    private static final String HOMEPAGE_INSIDE_LINKS_FIELD = "homepage";
    private static final String SUBREDDIT_URL_INSIDE_LINKS_FIELD = "subreddit_url";
    private static final String REPOS_URL_INSIDE_LINKS_FIELD = "repos_url";
    private static final String GITHUB_URLS_INSIDE_REPOS_FIELD = "github";

    // COMMUNITY DATA
    private static final String COMMUNITY_DATA_FIELD = "community_data";
    private static final String FACEBOOK_LIKES_FIELD_IN_COMMUNITY = "facebook_likes";
    private static final String TWITTER_FOLLOWERS_FIELD_IN_COMMUNITY = "twitter_followers";
    private static final String REDDIT_AVERAGE_POSTS_48H_FIELD_IN_COMMUNITY = "twitter_followers";
    private static final String REDDIT_SUBSCRIBERS_FIELD_IN_COMMUNITY = "reddit_subscribers";
    private static final String REDDIT_ACCOUNTS_ACTIVE_FIELD_IN_COMMUNITY = "reddit_accounts_active_48h";

    // PUBLIC INTEREST DATA
    private static final String PUBLIC_INTEREST_STATS_FIELD = "public_interest_stats";
    private static final String ALEXA_RANK_FIELD_IN_PUBSTATS = "alexa_rank";
    private static final String BING_MATCHES_FIELD_IN_PUBSTATS = "bing_matches";

    // DEVELOPER DATA
    private static final String DEVELOPER_DATA_FIELD = "developer_data";
    private static final String FORKS_FIELD_IN_DEVELOPER = "forks";
    private static final String STARS_FIELD_IN_DEVELOPER = "stars";
    private static final String SUBSCRIBERS_FIELD_IN_DEVELOPER = "subscribers";
    private static final String TOTAL_ISSUES_FIELD_IN_DEVELOPER = "total_issues";
    private static final String CLOSED_ISSUES_FIELD_IN_DEVELOPER = "closed_issues";
    private static final String PULL_REQUESTS_MERGED_FIELD_IN_DEVELOPER = "pull_requests_merged";
    private static final String PULL_REQUEST_CONTRIBS_FIELD_IN_DEVELOPER = "pull_request_contributors";
    private static final String COMMIT_COUNT_4_WEEKS_FIELD_IN_DEVELOPER =  "commit_count_4_weeks";

    // MARKET_DATA
    private static final String MARKET_DATA_FIELD = "market_data";
    // for each currency there s a field to extract to get its respective price
    // these are the supported currencies
    private static final String CURRENT_PRICE_IN_MARKET = "current_price";
    private static final String MARKETCAP_IN_MARKET = "market_cap";
    private static final String TOTAL_VOLUME_IN_MARKET = "total_volume";
    private static final String TOTAL_SUPPLY_IN_MARKET = "total_supply";
    private static final String CIRCULATING_SUPPLY_IN_MARKET = "circulating_supply";



    // IMAGE DATA

    private static final String IMAGE_DATA_FIELD = "image";
    private static final String SMALL_IMAGE = "small";
    private static final String THUMB_IMAGE = "thumb";
    private static final String LARGE_IMAGE = "large";


    @Override
    public AssetInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject object = json.getAsJsonObject();

        String id = object.get(ID_FIELD).getAsString();
        String symbol = object.get(SYMBOL_FIELD).getAsString();
        //String name = object.get(LOCALIZATION_NAME_FIELD).getAsString();

        BaseCryptoData data = new BaseCryptoData(id, symbol);
        LinkData linkData = new LinkData();
        if(object.has(MARKET_DATA_FIELD)){
            JsonObject marketObject  = object.getAsJsonObject(MARKET_DATA_FIELD);

            JsonElement totalSupplyElement = marketObject.get(TOTAL_SUPPLY_IN_MARKET);

            long totalSupply;
            if(totalSupplyElement.toString().equals(ABSENT_VALUE) ){
                totalSupply = -1;
            }else{
                totalSupply = marketObject.get(TOTAL_SUPPLY_IN_MARKET).getAsLong();
            }

            long circulatingSupply = marketObject.get(CIRCULATING_SUPPLY_IN_MARKET).getAsLong();

            data.setCirculatingSupply(circulatingSupply);
            data.setTotalSupply(totalSupply);
            JsonObject marketCapInCurrency = marketObject.getAsJsonObject(MARKETCAP_IN_MARKET);

            for(Map.Entry<String, JsonElement> entry : marketCapInCurrency.entrySet()  ){
                data.insertMarketCapCurrency(entry.getKey(), entry.getValue().getAsLong());
            }



            JsonObject totalVolumeInCurrency = marketObject.getAsJsonObject(TOTAL_VOLUME_IN_MARKET);
            for(Map.Entry<String, JsonElement> entry : totalVolumeInCurrency.entrySet()  ){
                data.insertTotalVolumeCurrency(entry.getKey(), entry.getValue().getAsLong());
            }
            JsonObject currentPriceInCurrency = marketObject.getAsJsonObject(CURRENT_PRICE_IN_MARKET);

            for(Map.Entry<String, JsonElement> entry : currentPriceInCurrency.entrySet()  ){
                data.insertCurrentPriceCurrency(entry.getKey(), entry.getValue().getAsBigDecimal());
            }
        }


        if(object.has(LOCALIZATION_NAME_FIELD)) {
            JsonObject localizedNames = object.getAsJsonObject(LOCALIZATION_NAME_FIELD);
            for (Map.Entry<String, JsonElement> entry : localizedNames.entrySet()) {
                data.insertLocalizedName(entry.getKey(), entry.getValue().getAsString());
            }
        }

        if(object.has(LOCALIZATION_DESCRIPTION_FIELD)) {

            JsonObject localizedDescription = object.getAsJsonObject(LOCALIZATION_DESCRIPTION_FIELD);
            for (Map.Entry<String, JsonElement> entry : localizedDescription.entrySet()) {
                data.insertLocalizedDescription(entry.getKey(), entry.getValue().getAsString());
            }
        }
        if(object.has(LINKS_FIELD)) {
            JsonObject links = object.getAsJsonObject(LINKS_FIELD);

            JsonArray homepage = links.getAsJsonArray(HOMEPAGE_INSIDE_LINKS_FIELD);

            JsonObject repos = links.getAsJsonObject(REPOS_URL_INSIDE_LINKS_FIELD);

            JsonArray github = repos.getAsJsonArray(GITHUB_URLS_INSIDE_REPOS_FIELD);


            for (JsonElement githubLink : github) {
                linkData.addLink(githubLink.getAsString());
            }
        }

        CommunityData communityData = null;
        DeveloperData developerData = null;
        PublicInterestStats stats = null;
        // Dev data
        if(object.has(DEVELOPER_DATA_FIELD)) {
            JsonObject developerJson = object.getAsJsonObject(DEVELOPER_DATA_FIELD);

            int forks = developerJson.get(FORKS_FIELD_IN_DEVELOPER).getAsInt();
            int stars = developerJson.get(STARS_FIELD_IN_DEVELOPER).getAsInt();
            int subscribers = developerJson.get(SUBSCRIBERS_FIELD_IN_DEVELOPER).getAsInt();
            int totalIssues = developerJson.get(TOTAL_ISSUES_FIELD_IN_DEVELOPER).getAsInt();
            int closedIssues = developerJson.get(CLOSED_ISSUES_FIELD_IN_DEVELOPER).getAsInt();
            int pullRequestsMerged = developerJson.get(PULL_REQUESTS_MERGED_FIELD_IN_DEVELOPER).getAsInt();
            int pullRequestContributors = developerJson.get(PULL_REQUEST_CONTRIBS_FIELD_IN_DEVELOPER).getAsInt();
            int commitCount4Weeks = developerJson.get(COMMIT_COUNT_4_WEEKS_FIELD_IN_DEVELOPER).getAsInt();

            developerData = new DeveloperData(forks, stars, subscribers, totalIssues, closedIssues, pullRequestsMerged, pullRequestContributors, commitCount4Weeks);
        }
                /*
        for(Map.Entry<String, JsonElement> entry : developerJson.entrySet()  ){
            developerData.insertLocalizedDescription(entry.getKey(), entry.getValue().getAsString());
        }
        */
        if(object.has(COMMUNITY_DATA_FIELD)) {
            JsonObject communityJson = object.getAsJsonObject(COMMUNITY_DATA_FIELD);
            int facebookLikes = communityJson.get(FACEBOOK_LIKES_FIELD_IN_COMMUNITY).getAsInt();
            int twitterFollowers = communityJson.get(TWITTER_FOLLOWERS_FIELD_IN_COMMUNITY).getAsInt();
            double redditAvgPosts48h = communityJson.get(REDDIT_AVERAGE_POSTS_48H_FIELD_IN_COMMUNITY).getAsDouble();
            int redditSubscribers = communityJson.get(REDDIT_SUBSCRIBERS_FIELD_IN_COMMUNITY).getAsInt();
            int redditActiveAccounts = communityJson.get(REDDIT_ACCOUNTS_ACTIVE_FIELD_IN_COMMUNITY).getAsInt();

            communityData = new CommunityData(facebookLikes, twitterFollowers,
                    redditAvgPosts48h, redditSubscribers, redditActiveAccounts);

        }
        if(object.has(PUBLIC_INTEREST_STATS_FIELD)) {


            JsonObject publicInterestJson = object.getAsJsonObject(PUBLIC_INTEREST_STATS_FIELD);

            int alexaRank = publicInterestJson.get(ALEXA_RANK_FIELD_IN_PUBSTATS).getAsInt();
            int bingMatches = publicInterestJson.get(BING_MATCHES_FIELD_IN_PUBSTATS).getAsInt();

            stats = new PublicInterestStats(alexaRank, bingMatches);

        }



        JsonObject imageObject = object.getAsJsonObject(IMAGE_DATA_FIELD);
        String smallImage = imageObject.get(SMALL_IMAGE).getAsString();
        String thumbImage = imageObject.get(THUMB_IMAGE).getAsString();
        String largeImage = imageObject.get(LARGE_IMAGE).getAsString();
        ImageData imageData = new ImageData(smallImage, thumbImage, largeImage);



        AssetInfo infos = new AssetInfo(data, communityData, developerData, stats, linkData, imageData);


        return infos;
    }
}
