package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.timeline.TimelineItemDTOKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.timeline.TimelineItemView;
import com.tradehero.th.persistence.timeline.TimelineCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/11/14 Time: 11:48 AM Copyright (c) TradeHero
 */
public class TimelineDiscussion extends DashboardFragment
{
    @InjectView(android.R.id.list) AbsListView commentList;

    @Inject TimelineCache timelineCache;
    private TimelineItemView timelineView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_discussion, container, false);
        timelineView = (TimelineItemView) inflater.inflate(R.layout.timeline_item_view, null);

        ButterKnife.inject(this, view);

        initView(view);
        return view;
    }

    private void initView(View view)
    {
    }

    @Override public void onResume()
    {
        super.onResume();

        TimelineItemDTOKey timelineItemDTOKey = new TimelineItemDTOKey(getArguments());
        //timelineView

        Timber.d("Timeline item id: %d", timelineItemDTOKey.key);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.discussion);
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
