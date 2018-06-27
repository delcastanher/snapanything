package com.github.delcastanher.snapanything;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.List;

public class SnapTimeline extends SnapTwitter {
    public SnapTimeline() throws TwitterException {
        List<Status> statuses;
        int page = 1;
        do {
            Paging paging = new Paging(page++);
            statuses = myTwitter.getTwitter().getUserTimeline(paging);
            for (Status status : statuses) {
                if (myTwitter.isMyTweetEligibleToDelete(status)) {
                    deletedStatuses.add(status.getText());
                    myTwitter.getTwitter().destroyStatus(status.getId());
                }
            }
        } while (statuses.size() > 0);
    }
}