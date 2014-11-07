package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.home.HomeFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import javax.inject.Inject;

@Routable({
        "user/me", "profiles/me"
})
public class MeTimelineFragment extends TimelineFragment
        implements WithTutorial, View.OnClickListener
{
    @Inject protected CurrentUserId currentUserId;
    @Inject Analytics analytics;
    @Inject DTOCacheUtilImpl dtoCacheUtil;

    private TextView updateCenterCountTextView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.save(getArguments(), currentUserId.toUserBaseKey());
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Me));
        dtoCacheUtil.initialPrefetches();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.timeline_menu, menu);
        displayActionBarTitle();
        setActionBarSubtitle(null);

        MenuItem updateCenterItem = menu.findItem(R.id.action_bar_update_center_icon);
        View updateCenterIcon = updateCenterItem.getActionView();
        if (updateCenterIcon != null)
        {
            updateCenterIcon.setOnClickListener(this);
            updateCenterCountTextView = (TextView) updateCenterIcon.findViewById(R.id.action_bar_message_count);
        }
        updateView();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_bar_home_icon:
                navigator.get().pushFragment(HomeFragment.class);
                return true;
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
            int unreadCount = shownProfile.unreadMessageThreadsCount;
            if (unreadCount == 0)
            {
                updateCenterCountTextView.setText("");
                updateCenterCountTextView.setVisibility(View.GONE);
            }
            else
            {
                updateCenterCountTextView.setText(THSignedNumber.builder(unreadCount).build().toString());
                updateCenterCountTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_timeline;
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.action_bar_update_center_icon:
                navigator.get().pushFragment(UpdateCenterFragment.class);
                break;
        }
    }

    @Override protected void fetchMessageThreadHeader()
    {
        // Nothing to do
    }
}
