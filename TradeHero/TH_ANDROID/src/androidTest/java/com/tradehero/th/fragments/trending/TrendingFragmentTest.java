package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import com.tradehero.THRobolectric;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.DashboardNavigator;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(THRobolectricTestRunner.class)
public class TrendingFragmentTest
{
    @Inject CurrentUserId currentUserId;
    @Inject DashboardNavigator dashboardNavigator;
    private OpenTrendingFragment trendingFragment;
    private OwnedPortfolioId applicablePortfolioId;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);
        currentUserId.set(23);
        applicablePortfolioId = new OwnedPortfolioId(23, 7);
    }

    @After public void tearDown()
    {
        dashboardNavigator.popFragment();
        trendingFragment = null;
    }

    @Test public void uiRequestDollarPassedWhenCallExtraCash() throws InterruptedException
    {
        Bundle args = new Bundle();
        TrendingFragment.putApplicablePortfolioId(args, applicablePortfolioId);
        trendingFragment = dashboardNavigator.pushFragment(
                OpenTrendingFragment.class,
                args);
        THRobolectric.runBgUiTasks(3);
        THBillingInteractor dummyInteractor = mock(THBillingInteractor.class);
        trendingFragment.set(dummyInteractor);
        final THUIBillingRequestContainer requestContainer = new THUIBillingRequestContainer();
        //noinspection unchecked
        when(dummyInteractor.run(any(THUIBillingRequest.class))).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                requestContainer.passedRequest = (THUIBillingRequest) invocation.getArguments()[0];
                return 1;
            }
        });

        trendingFragment.handleExtraCashItemOnClick();
        Thread.sleep(50);

        //noinspection unchecked
        verify(dummyInteractor, times(1)).run(requestContainer.passedRequest);
        assertThat(requestContainer.passedRequest).isNotNull();
        assertThat(requestContainer.passedRequest.getDomainToPresent()).isNotNull();
        assertThat(requestContainer.passedRequest.getDomainToPresent()).isEqualTo(ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR);
    }
}
