package com.tradehero.th.fragments.position;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class PositionListView extends PullToRefreshListView
{
    //<editor-fold desc="Constructors">
    public PositionListView(Context context)
    {
        super(context);
    }

    public PositionListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionListView(Context context, Mode mode)
    {
        super(context, mode);
    }

    public PositionListView(Context context, Mode mode, AnimationStyle style)
    {
        super(context, mode, style);
    }
    //</editor-fold>

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    public void setRefreshableView(ListView watchlistListView)
    {
        getRefreshableViewWrapper().addView(watchlistListView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
