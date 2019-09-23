package com.nexlabs.comnexlabsnanowidget.datapackage;

public class ImageData {
    private String smallUrl;
    private String thumbUrl;
    private String largeUrl;

    public ImageData(String smallUrl, String thumbUrl, String largeUrl) {
        this.smallUrl = smallUrl;
        this.thumbUrl = thumbUrl;
        this.largeUrl = largeUrl;
    }

    public String getSmallUrl() {
        return smallUrl;
    }

    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getLargeUrl() {
        return largeUrl;
    }

    public void setLargeUrl(String largeUrl) {
        this.largeUrl = largeUrl;
    }
}
