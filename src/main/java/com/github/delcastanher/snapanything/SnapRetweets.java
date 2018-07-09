package com.github.delcastanher.snapanything;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;

public class SnapRetweets extends SnapTwitter {

    public SnapRetweets() throws TwitterException {
        List<Status> statusesRetweetsOfMe = getRetweetsOfMe();
        int retweetAverage = (int) statusesRetweetsOfMe.stream().mapToDouble(Status::getRetweetCount).average().orElse(0.0);
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
        List<Status> statusesPerPage;
        int page = 1;
        do {
            Paging paging = new Paging(page++);
            statusesPerPage = myTwitter.getTwitter().getRetweetsOfMe(paging);
            statusesRetweetsOfMine.addAll(statusesPerPage);
        } while (statusesPerPage.size() > 0);
        return statusesRetweetsOfMine;
    }
}
