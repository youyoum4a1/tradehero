package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.fragments.timeline.TimelineItemView;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/11/14 Time: 11:48 AM Copyright (c) TradeHero
 */
public class TimelineDiscussionFragment extends AbstractDiscussionFragment
{
    private TimelineItemView timelineItemView;
    private TimelineItemDTOKey timelineItemDTOKey;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_discussion, container, false);
        timelineItemView = (TimelineItemView) inflater.inflate(R.layout.timeline_item_view, null);

        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        if (discussionKey instanceof TimelineItemDTOKey)
        {
            linkWith((TimelineItemDTOKey) discussionKey, true);
        }

        super.linkWith(discussionKey, andDisplay);
    }

    protected void linkWith(TimelineItemDTOKey timelineItemDTOKey, boolean andDisplay)
    {
        this.timelineItemDTOKey = timelineItemDTOKey;
        if (andDisplay)
        {
            timelineItemView.display(timelineItemDTOKey);
        }
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
