package com.github.delcastanher.snapanything;

import twitter4j.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SnapTwitter {

    // START OF CUSTOMIZABLE VARIABLES
    final private int DAYS_TO_KEEP = 7;
    final private boolean KEEP_FAVORITES = true;
    final private boolean KEEP_RETWEETS_OF_ME = true;
    // END OF CUSTOMIZABLE VARIABLES

    private Twitter twitter;
    private List<String> deletedStatuses = new ArrayList<String>();
    Date now = new Date();

    public SnapTwitter(){
        twitter = TwitterFactory.getSingleton();
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

    private boolean isEligibleToDelete(Status status, boolean keepRetweetsOfMe) {

        if (KEEP_FAVORITES && status.isFavorited()) {
            return false;
        }

        if (keepRetweetsOfMe && isRetweetOfMe(status)) {
            return false;
        }

        long dateDiff = TimeUnit.DAYS.convert(now.getTime() - status.getCreatedAt().getTime(), TimeUnit.MILLISECONDS);
        if (dateDiff <= DAYS_TO_KEEP) {
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
}
