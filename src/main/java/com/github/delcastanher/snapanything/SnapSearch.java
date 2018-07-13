package com.github.delcastanher.snapanything;

import com.google.appengine.api.datastore.*;
import me.jhenrique.manager.TweetManager;
import me.jhenrique.manager.TwitterCriteria;
import me.jhenrique.model.Tweet;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class SnapSearch extends SnapTwitter {

    private DatastoreService datastore;

    public SnapSearch() throws TwitterException {
        datastore = DatastoreServiceFactory.getDatastoreService();
        Tweet lastReadedTweet = new Tweet();
        List<Tweet> tweets = TweetManager.getTweets(createSearchCriteria());
        for (Tweet tweet: tweets) {
            lastReadedTweet = tweet;
            try {
                Status status = myTwitter.getTwitter().showStatus(Long.parseLong(tweet.getId()));
                if (myTwitter.isMyTweetEligibleToDelete(status)) {
                    deletedStatuses.add(status.getText());
                    myTwitter.getTwitter().destroyStatus(status.getId());
                }
            } catch (TwitterException e) {
                if (acceptExceptionForThisTwitterErrorCode(e.getErrorCode())) {
                    saveTweetOnDatastore(tweet);
                    throw e;
                }
            }
        }
        saveTweetOnDatastore(lastReadedTweet);
    }

    private boolean acceptExceptionForThisTwitterErrorCode(int errorCode){
        switch (errorCode){
            //[INFO] GCLOUD: TwitterException{exceptionCode=[168a95bc-0f0b152c], statusCode=403, message=You may not delete another user's status., code=183, retryAfter=-1, rateLimitStatus=null, version=4.0.6}
            case 183: return false;
            //[INFO] GCLOUD: TwitterException{exceptionCode=[e8914fbf-0ebc47ae], statusCode=401, message=You have been blocked from the author of this tweet., code=136, retryAfter=-1, rateLimitStatus=RateLimitStatusJSONImpl{remaining=677, limit=900, resetTimeInSeconds=1531484259, secondsUntilReset=415}, version=4.0.6}
            case 136: return false;

            default: return true;
        }
    }

    private TwitterCriteria createSearchCriteria() throws TwitterException {
        String authenticatedUserScreenName = myTwitter.getTwitter().verifyCredentials().getScreenName();
        String searchUntilDate = generateSearchUntilDate();
        return TwitterCriteria.create().setQuerySearch("@" + authenticatedUserScreenName).setUntil(searchUntilDate);
    }

    public String generateSearchUntilDate() {
        String untilDate;
        PreparedQuery pq = datastore.prepare(new Query("LastReadedTweet"));
        Entity lastReadedTweet = pq.asSingleEntity();
        if (lastReadedTweet != null && lastReadedTweet.hasProperty("date")) {
            datastore.delete(lastReadedTweet.getKey());
            untilDate = lastReadedTweet.getProperty("date").toString();
        } else {
            untilDate = LocalDate.now().toString();
        }
        return untilDate;
    }

    private void saveTweetOnDatastore(Tweet lastReadedTweet) {
        if (lastReadedTweet.getDate() != null) {
            Entity entity = new Entity("LastReadedTweet");
            entity.setProperty("date", convertDateToIsoLocalDate(lastReadedTweet.getDate()));
            datastore.put(entity);
        }
    }

    private String convertDateToIsoLocalDate(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

}