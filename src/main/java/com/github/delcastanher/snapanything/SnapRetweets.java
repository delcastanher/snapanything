package com.github.delcastanher.snapanything;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;

public class SnapRetweets extends SnapTwitter {

    public SnapRetweets() throws TwitterException {
        List<Status> statusesRetweetsOfMe = getRetweetsOfMe();
        int retweetsTotalSum = getRetweetsCount(statusesRetweetsOfMe);
        int retweetAverage = (int) Math.floor(retweetsTotalSum / statusesRetweetsOfMe.size());
        myTwitter.setKeepRetweetsOfMeAboveAverage(retweetAverage);
        for (Status status : statusesRetweetsOfMe) {
            if (myTwitter.isMyTweetEligibleToDelete(status)) {
                deletedStatuses.add(status.getText());
                myTwitter.getTwitter().destroyStatus(status.getId());
            }
        }
    }

    private List<Status> getRetweetsOfMe() throws TwitterException {
        List<Status> statusesRetweetsOfMine = new ArrayList<Status>();
        List<Status> statuses;
        int page = 1;
        do {
            Paging paging = new Paging(page++);
            statuses = myTwitter.getTwitter().getRetweetsOfMe(paging);
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

}
