package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.special.ResideMenu.ResideMenu;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.home.HomeFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;

@Routable({
        "user/me", "profiles/me"
})
public class MeTimelineFragment extends TimelineFragment
    implements WithTutorial, View.OnClickListener
{
    @Inject protected CurrentUserId currentUserId;
    @Inject Analytics analytics;
    @Inject Lazy<ResideMenu> resideMenuLazy;

    private TextView updateCenterCountTextView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.save(getArguments(), currentUserId.toUserBaseKey());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.timeline_menu, menu);
        displayActionBarTitle();

        MenuItem updateCenterItem = menu.findItem(R.id.action_bar_update_center_icon);
        View updateCenterIcon = updateCenterItem.getActionView();
        if (updateCenterIcon != null)
        {
            updateCenterIcon.setOnClickListener(this);
            updateCenterCountTextView = (TextView)updateCenterIcon.findViewById(R.id.action_bar_message_count);
        }
        updateView();
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Me));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_bar_home_icon:
                getDashboardNavigator().pushFragment(HomeFragment.class);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void updateView()
    {
        super.updateView();
        if (updateCenterCountTextView != null && shownProfile != null)
        {
            updateCenterCountTextView.setText(String.valueOf(shownProfile.unreadMessageThreadsCount));
        }
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_timeline;
    }

    @Override public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.action_bar_update_center_icon:
                getDashboardNavigator().pushFragment(UpdateCenterFragment.class);
                break;
        }
    }
}
