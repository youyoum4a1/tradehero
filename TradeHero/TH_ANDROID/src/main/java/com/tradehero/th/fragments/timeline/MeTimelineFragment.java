package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.localytics.android.LocalyticsSession;
import com.special.ResideMenu.ResideMenu;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.settings.SettingsProfileFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;


public class MeTimelineFragment extends TimelineFragment
    implements WithTutorial
{
    @Inject protected CurrentUserId currentUserId;
    @Inject LocalyticsSession localyticsSession;
    @Inject Lazy<ResideMenu> resideMenuLazy;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("MeTimelineFragment onCreate");
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.TabBar_Me);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        if (currentUserId != null)
        {
            getArguments().putInt(BUNDLE_KEY_SHOW_USER_ID, currentUserId.get());
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.timeline_menu, menu);
        actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(
                (ActionBar.DISPLAY_USE_LOGO)
                        | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setHomeButtonEnabled(true);
        displayActionBarTitle();
        actionBar.setLogo(R.drawable.icon_menu);
        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_edit:
                Bundle bundle = new Bundle();
                bundle.putBoolean(SettingsProfileFragment.BUNDLE_KEY_SHOW_BUTTON_BACK, true);
                getNavigator().pushFragment(SettingsProfileFragment.class, bundle);
                return true;
            case android.R.id.home:
                resideMenuLazy.get().openMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void createUserProfileRetrievedMilestone()
    {
        userProfileRetrievedMilestone = new UserProfileRetrievedMilestone(currentUserId.toUserBaseKey());
    }

    @Override protected void createPortfolioCompactListRetrievedMilestone()
    {
        portfolioCompactListRetrievedMilestone = new PortfolioCompactListRetrievedMilestone(currentUserId.toUserBaseKey());
    }

    @Override public boolean isTabBarVisible()
    {
        return true;
    }


    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_timeline;
    }
}
