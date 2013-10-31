package com.tradehero.th.widget.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/** Created with IntelliJ IDEA. User: tho Date: 9/13/13 Time: 11:35 AM Copyright (c) TradeHero */
public class TimelineListView extends PullToRefreshListView
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

    public TimelineListView(Context context, Mode mode, AnimationStyle style)
    {
        super(context, mode, style);
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
