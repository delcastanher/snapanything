package com.github.delcastanher.snapanything;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class MyTwitter {

    // START OF CUSTOMIZABLE VARIABLES
    private final String O_AUTH_CONSUMER_KEY = "*********************";
    private final String O_AUTH_CONSUMER_SECRET = "******************************************";
    private final String O_AUTH_ACCESS_TOKEN = "**************************************************";
    private final String O_AUTH_ACCESS_TOKEN_SECRET = "******************************************";
    private final long DAYS_TO_KEEP = 1L;
    private final boolean KEEP_FAVORITES = false;
    private int KEEP_RETWEETS_OF_ME_ABOVE_AVERAGE = 0;
    // END OF CUSTOMIZABLE VARIABLES

    private Twitter twitter;
    private Date dateToDeleteBefore;

    public MyTwitter() {
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

    public boolean isMyTweetEligibleToDelete(Status status) {

        if (KEEP_FAVORITES && status.isFavorited()) {
            return false;
        }

        if (KEEP_RETWEETS_OF_ME_ABOVE_AVERAGE > 0 && !status.isRetweet() && status.getRetweetCount() >= KEEP_RETWEETS_OF_ME_ABOVE_AVERAGE) {
            return false;
        }

        if (status.getCreatedAt().compareTo(dateToDeleteBefore) > 0) {
            return false;
        }

        return true;
    }

    public void setKeepRetweetsOfMeAboveAverage(int retweetAverage){
        this.KEEP_RETWEETS_OF_ME_ABOVE_AVERAGE = retweetAverage;
    }

    public long getDaysToKeep() { return DAYS_TO_KEEP; }

    public boolean getKeepFavorites() { return KEEP_FAVORITES; }

    public Twitter getTwitter() { return twitter; }
}
