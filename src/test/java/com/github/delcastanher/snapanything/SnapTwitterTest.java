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

public class SnapTwitterTest {

    private SnapTwitter snapTwitter;
    private long DAYS_TO_KEEP;
    private boolean KEEP_FAVORITES;
    Date dateToDeleteBefore;

    @Mock
    private Status status;

    @Before
    public void init() {
        snapTwitter = new SnapTwitter();
        DAYS_TO_KEEP = snapTwitter.getDaysToKeep();
        KEEP_FAVORITES = snapTwitter.getKeepFavorites();
        MockitoAnnotations.initMocks(this);
        dateToDeleteBefore = Date.from(LocalDateTime.now().minusDays(DAYS_TO_KEEP + 1).atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    public void testIsEligibleToDeleteWhenisExactlyDaysToKeepOld() {
        Date dateToDelete = Date.from(LocalDateTime.now().minusDays(DAYS_TO_KEEP).atZone(ZoneId.systemDefault()).toInstant());
        snapTwitter = new SnapTwitter(); // Need to inicialize here again to have the same seconds
        this.mockStatus(false, false, 0, dateToDelete);
        Assert.assertTrue(snapTwitter.isEligibleToDelete(status, false));
        Assert.assertTrue(snapTwitter.isEligibleToDelete(status, true));
    }

    @Test
    public void testIsEligibleToDeleteWhenOlderThanDaysToKeep() {
        this.mockStatus(false, false, 0, dateToDeleteBefore);
        Assert.assertTrue(snapTwitter.isEligibleToDelete(status, false));
        Assert.assertTrue(snapTwitter.isEligibleToDelete(status, true));
    }

    @Test
    public void testIsEligibleToDeleteWhenNewerThanDaysToKeep() {
        Date dateToDelete = Date.from(LocalDateTime.now().minusDays(DAYS_TO_KEEP - 1).atZone(ZoneId.systemDefault()).toInstant());

        this.mockStatus(false, false, 0, dateToDelete);
        Assert.assertFalse(snapTwitter.isEligibleToDelete(status, false));
        Assert.assertFalse(snapTwitter.isEligibleToDelete(status, true));

        this.mockStatus(true, false, 0, dateToDelete);
        Assert.assertFalse(snapTwitter.isEligibleToDelete(status, false));
        Assert.assertFalse(snapTwitter.isEligibleToDelete(status, true));

        this.mockStatus(false, true, 0, dateToDelete);
        Assert.assertFalse(snapTwitter.isEligibleToDelete(status, false));
        Assert.assertFalse(snapTwitter.isEligibleToDelete(status, true));

        this.mockStatus(false, true, 99, dateToDelete);
        Assert.assertFalse(snapTwitter.isEligibleToDelete(status, false));
        Assert.assertFalse(snapTwitter.isEligibleToDelete(status, true));

        this.mockStatus(false, false, 99, dateToDelete);
        Assert.assertFalse(snapTwitter.isEligibleToDelete(status, false));
        Assert.assertFalse(snapTwitter.isEligibleToDelete(status, true));
    }

    @Test
    public void testIsEligibleToDeleteWhenFavorited() {
        this.mockStatus(true, false, 0, dateToDeleteBefore);
        Assert.assertEquals(snapTwitter.isEligibleToDelete(status, false), !KEEP_FAVORITES);
        Assert.assertEquals(snapTwitter.isEligibleToDelete(status, true), !KEEP_FAVORITES);
    }

    @Test
    public void testIsEligibleToDeleteWhenIsNotMyTweet() {
        this.mockStatus(false, true, 0, dateToDeleteBefore);
        Assert.assertTrue(snapTwitter.isEligibleToDelete(status, false));
        Assert.assertTrue(snapTwitter.isEligibleToDelete(status, true));

        this.mockStatus(false, true, 99, dateToDeleteBefore);
        Assert.assertTrue(snapTwitter.isEligibleToDelete(status, false));
        Assert.assertTrue(snapTwitter.isEligibleToDelete(status, true));
    }

    @Test
    public void testIsEligibleToDeleteWhenHasRetweets() {
        this.mockStatus(false, false, 99, dateToDeleteBefore);
        Assert.assertTrue(snapTwitter.isEligibleToDelete(status, false));
        Assert.assertFalse(snapTwitter.isEligibleToDelete(status, true));
    }

    private void mockStatus(boolean isFavorited, boolean isRetweet, int retweetCount, Date createdAt) {
        when(status.isFavorited()).thenReturn(isFavorited);
        when(status.isRetweet()).thenReturn(isRetweet);
        when(status.getRetweetCount()).thenReturn(retweetCount);
        when(status.getCreatedAt()).thenReturn(createdAt);
    }
}
