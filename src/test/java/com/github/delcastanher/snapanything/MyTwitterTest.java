package com.github.delcastanher.snapanything;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import twitter4j.Status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.when;

public class MyTwitterTest {

    private MyTwitter myTwitter;
    private long DAYS_TO_KEEP;
    private boolean KEEP_FAVORITES;
    Date dateToDeleteBefore;

    @Mock
    private Status status;

    @Before
    public void init() {
        myTwitter = new MyTwitter();
        DAYS_TO_KEEP = myTwitter.getDaysToKeep();
        KEEP_FAVORITES = myTwitter.getKeepFavorites();
        MockitoAnnotations.initMocks(this);
        dateToDeleteBefore = Date.from(LocalDateTime.now().minusDays(DAYS_TO_KEEP + 1).atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    public void testIsEligibleToDeleteWhenisExactlyDaysToKeepOld() {
        Date dateToDelete = Date.from(LocalDateTime.now().minusDays(DAYS_TO_KEEP).atZone(ZoneId.systemDefault()).toInstant());
        myTwitter = new MyTwitter(); // Need to inicialize here again to have the same seconds
        this.mockStatus(false, false, 0, dateToDelete);
        myTwitter.setKeepRetweetsOfMeAboveAverage(0);
        Assert.assertTrue(myTwitter.isMyTweetEligibleToDelete(status));
        myTwitter.setKeepRetweetsOfMeAboveAverage(1);
        Assert.assertTrue(myTwitter.isMyTweetEligibleToDelete(status));
    }

    @Test
    public void testIsEligibleToDeleteWhenOlderThanDaysToKeep() {
        this.mockStatus(false, false, 0, dateToDeleteBefore);
        myTwitter.setKeepRetweetsOfMeAboveAverage(0);
        Assert.assertTrue(myTwitter.isMyTweetEligibleToDelete(status));
        myTwitter.setKeepRetweetsOfMeAboveAverage(1);
        Assert.assertTrue(myTwitter.isMyTweetEligibleToDelete(status));
    }

    @Test
    public void testIsEligibleToDeleteWhenNewerThanDaysToKeep() {
        Date dateToDelete = Date.from(LocalDateTime.now().minusDays(DAYS_TO_KEEP - 1).atZone(ZoneId.systemDefault()).toInstant());

        this.mockStatus(false, false, 0, dateToDelete);
        myTwitter.setKeepRetweetsOfMeAboveAverage(0);
        Assert.assertFalse(myTwitter.isMyTweetEligibleToDelete(status));
        myTwitter.setKeepRetweetsOfMeAboveAverage(1);
        Assert.assertFalse(myTwitter.isMyTweetEligibleToDelete(status));

        this.mockStatus(true, false, 0, dateToDelete);
        myTwitter.setKeepRetweetsOfMeAboveAverage(0);
        Assert.assertFalse(myTwitter.isMyTweetEligibleToDelete(status));
        myTwitter.setKeepRetweetsOfMeAboveAverage(1);
        Assert.assertFalse(myTwitter.isMyTweetEligibleToDelete(status));

        this.mockStatus(false, true, 0, dateToDelete);
        myTwitter.setKeepRetweetsOfMeAboveAverage(0);
        Assert.assertFalse(myTwitter.isMyTweetEligibleToDelete(status));
        myTwitter.setKeepRetweetsOfMeAboveAverage(1);
        Assert.assertFalse(myTwitter.isMyTweetEligibleToDelete(status));

        this.mockStatus(false, true, 99, dateToDelete);
        myTwitter.setKeepRetweetsOfMeAboveAverage(0);
        Assert.assertFalse(myTwitter.isMyTweetEligibleToDelete(status));
        myTwitter.setKeepRetweetsOfMeAboveAverage(1);
        Assert.assertFalse(myTwitter.isMyTweetEligibleToDelete(status));

        this.mockStatus(false, false, 99, dateToDelete);
        myTwitter.setKeepRetweetsOfMeAboveAverage(0);
        Assert.assertFalse(myTwitter.isMyTweetEligibleToDelete(status));
        myTwitter.setKeepRetweetsOfMeAboveAverage(1);
        Assert.assertFalse(myTwitter.isMyTweetEligibleToDelete(status));
    }

    @Test
    public void testIsEligibleToDeleteWhenFavorited() {
        this.mockStatus(true, false, 0, dateToDeleteBefore);
        myTwitter.setKeepRetweetsOfMeAboveAverage(0);
        Assert.assertEquals(myTwitter.isMyTweetEligibleToDelete(status), !KEEP_FAVORITES);
        myTwitter.setKeepRetweetsOfMeAboveAverage(1);
        Assert.assertEquals(myTwitter.isMyTweetEligibleToDelete(status), !KEEP_FAVORITES);
    }

    @Test
    public void testIsEligibleToDeleteWhenIsNotMyTweet() {
        this.mockStatus(false, true, 0, dateToDeleteBefore);
        myTwitter.setKeepRetweetsOfMeAboveAverage(0);
        Assert.assertTrue(myTwitter.isMyTweetEligibleToDelete(status));
        myTwitter.setKeepRetweetsOfMeAboveAverage(1);
        Assert.assertTrue(myTwitter.isMyTweetEligibleToDelete(status));

        this.mockStatus(false, true, 99, dateToDeleteBefore);
        myTwitter.setKeepRetweetsOfMeAboveAverage(0);
        Assert.assertTrue(myTwitter.isMyTweetEligibleToDelete(status));
        myTwitter.setKeepRetweetsOfMeAboveAverage(1);
        Assert.assertTrue(myTwitter.isMyTweetEligibleToDelete(status));
    }

    @Test
    public void testIsEligibleToDeleteWhenHasRetweets() {
        this.mockStatus(false, false, 99, dateToDeleteBefore);
        myTwitter.setKeepRetweetsOfMeAboveAverage(0);
        Assert.assertTrue(myTwitter.isMyTweetEligibleToDelete(status));
        myTwitter.setKeepRetweetsOfMeAboveAverage(1);
        Assert.assertFalse(myTwitter.isMyTweetEligibleToDelete(status));
        myTwitter.setKeepRetweetsOfMeAboveAverage(99);
        Assert.assertFalse(myTwitter.isMyTweetEligibleToDelete(status));
        myTwitter.setKeepRetweetsOfMeAboveAverage(100);
        Assert.assertTrue(myTwitter.isMyTweetEligibleToDelete(status));
    }

    private void mockStatus(boolean isFavorited, boolean isRetweet, int retweetCount, Date createdAt) {
        when(status.isFavorited()).thenReturn(isFavorited);
        when(status.isRetweet()).thenReturn(isRetweet);
        when(status.getRetweetCount()).thenReturn(retweetCount);
        when(status.getCreatedAt()).thenReturn(createdAt);
    }
}
