package com.tradehero.th.fragments.social;

import android.content.Context;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class PeopleSearchFragmentTest
{
    @Inject Context context;
    private PeopleSearchFragment peopleSearchFragment;
    private DashboardNavigator dashboardNavigator;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();
    }

    @After public void tearDown()
    {
        peopleSearchFragment = null;
    }

    //<editor-fold desc="Hint">
    @Ignore("This test will fail because the setup mocks the action bar, which then returns a null view")
    @Test public void testHintIsCorrect()
    {
        peopleSearchFragment = dashboardNavigator.pushFragment(PeopleSearchFragment.class);

        assertThat(peopleSearchFragment.mSearchTextField.getHint())
                .isEqualTo(context.getString(R.string.search_social_friend_hint));
    }
    //</editor-fold>
}
