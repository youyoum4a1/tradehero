package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.handmark.pulltorefresh.library.PullToRefreshStickyListHeadersListView;

public class TimelineListView extends PullToRefreshStickyListHeadersListView
{
    //<editor-fold desc="Constructors">
    public TimelineListView(Context context)
    {
        super(context);
    }

    public TimelineListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TimelineListView(Context context, Mode mode)
    {
        super(context, mode);
    }

    public TimelineListView(Context context, Mode mode, AnimationStyle animStyle)
    {
        super(context, mode, animStyle);
    }
    //</editor-fold>

    public void addHeaderView(View view)
    {
        getRefreshableView().addHeaderView(view);
    }

    public void addFooterView(View view)
    {
        getRefreshableView().addFooterView(view);
    }

}
