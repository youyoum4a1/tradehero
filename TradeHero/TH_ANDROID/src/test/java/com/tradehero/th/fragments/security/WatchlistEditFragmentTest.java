package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(RobolectricMavenTestRunner.class)
public class WatchlistEditFragmentTest
{
    private static final String GOOGLE_NAME = "Google Inc";
    private static final String GOOGLE_STOCK_WATCHING_PRICE = "554.51";
    private static final String GOOGLE_STOCK_WATCHING_QUANTITY = "1";
    private static final String GOOGLE_LOGO_URL =
            "http://portalvhdskgrrf4wksb8vq.blob.core.windows.net/tradeherocompanypictures/162075.pass%2399.1.Google%20Inc.9918673.jpg.THCROPSIZED.jpg";

    private WatchlistEditFragment watchlistFragment;
    private SecurityId googleSecurityId;

    @Before public void setUp()
    {
        ActivityController<DashboardActivity> activityController = Robolectric.buildActivity(DashboardActivity.class).create().start();
        DashboardActivity activity = activityController.get();

        googleSecurityId = new SecurityId("NASDAQ", "GOOGL");

        // TODO should we do like following, or should we get the object by deserialize json data from test resource?
        SecurityCompactDTO googleSecurityCompactDTO = new SecurityCompactDTO();
        googleSecurityCompactDTO.name = GOOGLE_NAME;
        googleSecurityCompactDTO.imageBlobUrl = GOOGLE_LOGO_URL;
        googleSecurityCompactDTO.lastPrice = 554.51D;

        SecurityCompactCache securityCompactCache = spy(new SecurityCompactCache());
        when(securityCompactCache.get(any(SecurityId.class))).thenReturn(googleSecurityCompactDTO);

        Bundle args = new Bundle();
        WatchlistEditFragment.putSecurityId(args, googleSecurityId);
        watchlistFragment = activity.getDashboardNavigator().pushFragment(WatchlistEditFragment.class, args);
        watchlistFragment.securityCompactCache = securityCompactCache;

        activityController.resume();
    }

    @Test public void shouldNotBeNull()
    {
        assertThat(watchlistFragment).isNotNull();
        assertThat(watchlistFragment.getView()).isNotNull();
    }

    @Test public void shouldDisplaySecurityInfoCorrectly()
    {
        WatchlistViewHolder holder = new WatchlistViewHolder();
        ButterKnife.inject(holder, watchlistFragment.getView());

        assertThat(holder.securityName.getText()).isEqualTo(SecurityUtils.getDisplayableSecurityName(googleSecurityId));
        assertThat(holder.securityDesc.getText()).isEqualTo(GOOGLE_NAME);
        assertThat(holder.securityPrice.getText().toString()).isEqualTo(GOOGLE_STOCK_WATCHING_PRICE);
        assertThat(holder.securityQuantity.getText().toString()).isEqualTo(GOOGLE_STOCK_WATCHING_QUANTITY);
    }

    public static class WatchlistViewHolder
    {
        @InjectView(R.id.edit_watchlist_item_security_name) TextView securityName;
        @InjectView(R.id.edit_watchlist_item_security_desc) TextView securityDesc;
        @InjectView(R.id.edit_watchlist_item_security_price) EditText securityPrice;
        @InjectView(R.id.edit_watchlist_item_security_quantity) EditText securityQuantity;
    }
}
