package com.nexlabs.comnexlabsnanowidget.datapackage;

import com.google.gson.annotations.SerializedName;

public class PublicInterestStats {

    @SerializedName("alexa_rank")
    long alexaRank;
    @SerializedName("bing_matches")
    long bingMatches;
    private PublicInterestStats(){

    }

    public PublicInterestStats(long alexaRank, long bingMatches) {
        this.alexaRank = alexaRank;
        this.bingMatches = bingMatches;
    }


    public void setAlexaRank(long alexaRank) {
        this.alexaRank = alexaRank;
    }

    public void setBingMatches(long bingMatches) {
        this.bingMatches = bingMatches;
    }

    public long getAlexaRank() {
        return alexaRank;
    }

    public long getBingMatches() {
        return bingMatches;
    }
}
