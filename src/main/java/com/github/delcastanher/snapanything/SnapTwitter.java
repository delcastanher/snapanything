package com.github.delcastanher.snapanything;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SnapTwitter {

    // START OF CUSTOMIZABLE VARIABLES
    final private String O_AUTH_CONSUMER_KEY = "*********************";
    final private String O_AUTH_CONSUMER_SECRET = "******************************************";
    final private String O_AUTH_ACCESS_TOKEN = "**************************************************";
    final private String O_AUTH_ACCESS_TOKEN_SECRET = "******************************************";
    final private long DAYS_TO_KEEP = 1L;
    final private boolean KEEP_FAVORITES = false;
    final private boolean KEEP_RETWEETS_OF_ME = false;
    // END OF CUSTOMIZABLE VARIABLES

    private Twitter twitter;
    private List<String> deletedStatuses = new ArrayList<String>();
    private Date dateToDeleteBefore;

    public SnapTwitter(){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(O_AUTH_CONSUMER_KEY)
                .setOAuthConsumerSecret(O_AUTH_CONSUMER_SECRET)
                .setOAuthAccessToken(O_AUTH_ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(O_AUTH_ACCESS_TOKEN_SECRET);
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
        dateToDeleteBefore = Date.from(LocalDateTime.now().minusDays(DAYS_TO_KEEP).atZone(ZoneId.systemDefault()).toInstant());
    }

    public void snapTimeline() throws TwitterException {
        List<Status> statuses;
        int page = 1;
        do {
            Paging paging = new Paging(page++);
            statuses = twitter.getUserTimeline(paging);
            for (Status status : statuses) {
                if (isEligibleToDelete(status, KEEP_RETWEETS_OF_ME)) {
                    deletedStatuses.add(status.getText());
                    twitter.destroyStatus(status.getId());
                }
            }
        } while (statuses.size() > 0);
    }

    public void snapRetweets() throws TwitterException {
        List<Status> statusesRetweetsOfMe = getRetweetsOfMe();
        int retweetsTotalSum = getRetweetsCount(statusesRetweetsOfMe);
        int retweetAverage = (int) Math.floor(retweetsTotalSum / statusesRetweetsOfMe.size());
        for (Status status : statusesRetweetsOfMe) {
            if (isEligibleToDelete(status, false) && status.getRetweetCount() < retweetAverage ) {
                deletedStatuses.add(status.getText());
                twitter.destroyStatus(status.getId());
            }
        }
    }

    private List<Status> getRetweetsOfMe() throws TwitterException {
        List<Status> statusesRetweetsOfMine = new ArrayList<Status>();
        List<Status> statuses;
        int page = 1;
        do {
            Paging paging = new Paging(page++);
            statuses = twitter.getRetweetsOfMe(paging);
            statusesRetweetsOfMine.addAll(statuses);
        } while (statuses.size() > 0);
        return statusesRetweetsOfMine;
    }

    private int getRetweetsCount(List<Status> statuses) {
        int retweetsTotalSum = 0;
        for (Status status: statuses) {
            retweetsTotalSum += status.getRetweetCount();
        }
        return retweetsTotalSum;
    }

    public boolean isEligibleToDelete(Status status, boolean keepRetweetsOfMe) {

        if (KEEP_FAVORITES && status.isFavorited()) {
            return false;
        }

        if (keepRetweetsOfMe && isRetweetOfMe(status)) {
            return false;
        }

        if (status.getCreatedAt().compareTo(dateToDeleteBefore) > 0) {
            return false;
        }

        return true;
    }

    private boolean isRetweetOfMe(Status status) {
        return !status.isRetweet() && status.getRetweetCount() > 0;
    }

    public List<String> getDeletedStatuses() {
        return deletedStatuses;
    }

    public long getDaysToKeep() { return DAYS_TO_KEEP; }

    public boolean getKeepFavorites() { return KEEP_FAVORITES; }
}
