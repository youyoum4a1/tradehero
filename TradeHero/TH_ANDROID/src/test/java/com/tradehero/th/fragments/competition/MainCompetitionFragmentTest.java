package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.fragments.DashboardNavigator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class MainCompetitionFragmentTest
{
    private DashboardNavigator dashboardNavigator;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();
    }

    @Test public void shouldAbleToNavigateToMainCompetitionFragmentWithOutApplicablePortfolioId()
    {
        Bundle args = new Bundle();

        ProviderId providerId = new ProviderId(23);
        MainCompetitionFragment.putProviderId(args, providerId);

        MainCompetitionFragment mainCompetition = dashboardNavigator.pushFragment(MainCompetitionFragment.class, args);

        assertThat(dashboardNavigator.getCurrentFragment()).isInstanceOf(MainCompetitionFragment.class);
        assertThat(mainCompetition).isNotNull();
        assertThat(mainCompetition.listView).isNotNull();
    }
}