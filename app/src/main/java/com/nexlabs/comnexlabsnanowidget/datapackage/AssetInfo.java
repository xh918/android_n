package com.nexlabs.comnexlabsnanowidget.datapackage;

public class AssetInfo {
    BaseCryptoData cryptoData;
    CommunityData communityData;
    DeveloperData developerData;
    PublicInterestStats publicInterestStats;
    LinkData linkData;
    ImageData imageData;

    public AssetInfo(BaseCryptoData cryptoData, CommunityData communityData, DeveloperData developerData, PublicInterestStats publicInterestStats, LinkData linkData, ImageData imageData) {
        this.cryptoData = cryptoData;
        this.communityData = communityData;
        this.developerData = developerData;
        this.publicInterestStats = publicInterestStats;
        this.linkData = linkData;
        this.imageData = imageData;
    }

    public BaseCryptoData getCryptoData() {
        return cryptoData;
    }

    public void setCryptoData(BaseCryptoData cryptoData) {
        this.cryptoData = cryptoData;
    }

    public CommunityData getCommunityData() {
        return communityData;
    }

    public void setCommunityData(CommunityData communityData) {
        this.communityData = communityData;
    }

    public DeveloperData getDeveloperData() {
        return developerData;
    }

    public void setDeveloperData(DeveloperData developerData) {
        this.developerData = developerData;
    }

    public PublicInterestStats getPublicInterestStats() {
        return publicInterestStats;
    }

    public void setPublicInterestStats(PublicInterestStats publicInterestStats) {
        this.publicInterestStats = publicInterestStats;
    }

    public void setLinkData(LinkData linkData) {
        this.linkData = linkData;
    }

    public LinkData getLinkData() {
        return linkData;
    }

    public ImageData getImageData() {
        return imageData;
    }

    public void setImageData(ImageData imageData) {
        this.imageData = imageData;
    }
}
