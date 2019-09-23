package com.nexlabs.comnexlabsnanowidget.datapackage;

import com.google.gson.annotations.SerializedName;

public class CommunityData {
    @SerializedName("facebook_likes")
    int facebookLikes;
    @SerializedName("twitter_followers")
    int twitterFollowers;
    @SerializedName("reddit_average_posts_48h")
    double redditAveragePosts48h;
    @SerializedName("reddit_subscribers")
    int redditSubscribers;
    @SerializedName("reddit_accounts_active_48h")
    int redditAccountsActive48h;


    private CommunityData() {
    }

    public CommunityData(int facebookLikes, int twitterFollowers, double redditAveragePosts48h, int redditSubscribers, int redditAccountsActive48h) {
        this.facebookLikes = facebookLikes;
        this.twitterFollowers = twitterFollowers;
        this.redditAveragePosts48h = redditAveragePosts48h;
        this.redditSubscribers = redditSubscribers;
        this.redditAccountsActive48h = redditAccountsActive48h;
    }

    public void setFacebookLikes(int facebookLikes) {
        this.facebookLikes = facebookLikes;
    }

    public void setTwitterFollowers(int twitterFollowers) {
        this.twitterFollowers = twitterFollowers;
    }

    public void setRedditAveragePosts48h(double redditAveragePosts48h) {
        this.redditAveragePosts48h = redditAveragePosts48h;
    }

    public void setRedditSubscribers(int redditSubscribers) {
        this.redditSubscribers = redditSubscribers;
    }

    public void setRedditAccountsActive48h(int redditAccountsActive48h) {
        this.redditAccountsActive48h = redditAccountsActive48h;
    }

    public int getFacebookLikes() {
        return facebookLikes;
    }

    public int getTwitterFollowers() {
        return twitterFollowers;
    }

    public double getRedditAveragePosts48h() {
        return redditAveragePosts48h;
    }

    public int getRedditSubscribers() {
        return redditSubscribers;
    }

    public int getRedditAccountsActive48h() {
        return redditAccountsActive48h;
    }
}
