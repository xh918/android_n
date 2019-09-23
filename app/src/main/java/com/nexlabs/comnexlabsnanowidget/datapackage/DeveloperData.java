package com.nexlabs.comnexlabsnanowidget.datapackage;

import com.google.gson.annotations.SerializedName;

public class DeveloperData {
    @SerializedName("forks")
    int forks;
    @SerializedName("stars")
    int stars;
    @SerializedName("subscribers")
    int subscribers;
    @SerializedName("total_issues")
    int totalIssues;
    @SerializedName("closed_issues")
    int closedIssues;
    @SerializedName("pull_requests_merged")
    int pullRequestsMerged;
    @SerializedName("pull_request_contributors")
    int getPullRequestsContributors;
    @SerializedName("commit_count_4_weeks")
    int commitCount4weeks;

    private DeveloperData() {
    }

    public DeveloperData(int forks, int stars, int subscribers, int totalIssues, int closedIssues, int pullRequestsMerged, int getPullRequestsContributors, int commitCount4weeks) {
        this.forks = forks;
        this.stars = stars;
        this.subscribers = subscribers;
        this.totalIssues = totalIssues;
        this.closedIssues = closedIssues;
        this.pullRequestsMerged = pullRequestsMerged;
        this.getPullRequestsContributors = getPullRequestsContributors;
        this.commitCount4weeks = commitCount4weeks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setSubscribers(int subscribers) {
        this.subscribers = subscribers;
    }

    public void setTotalIssues(int totalIssues) {
        this.totalIssues = totalIssues;
    }

    public void setClosedIssues(int closedIssues) {
        this.closedIssues = closedIssues;
    }

    public void setPullRequestsMerged(int pullRequestsMerged) {
        this.pullRequestsMerged = pullRequestsMerged;
    }

    public void setGetPullRequestsContributors(int getPullRequestsContributors) {
        this.getPullRequestsContributors = getPullRequestsContributors;
    }

    public void setCommitCount4weeks(int commitCount4weeks) {
        this.commitCount4weeks = commitCount4weeks;
    }


    public int getForks() {
        return forks;
    }

    public int getStars() {
        return stars;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public int getTotalIssues() {
        return totalIssues;
    }

    public int getClosedIssues() {
        return closedIssues;
    }

    public int getPullRequestsMerged() {
        return pullRequestsMerged;
    }

    public int getGetPullRequestsContributors() {
        return getPullRequestsContributors;
    }

    public int getCommitCount4weeks() {
        return commitCount4weeks;
    }
}
