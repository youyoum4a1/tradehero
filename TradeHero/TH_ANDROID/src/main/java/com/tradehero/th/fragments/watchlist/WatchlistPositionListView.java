package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class WatchlistPositionListView extends PullToRefreshListView
{
    //<editor-fold desc="Constructors">
    public WatchlistPositionListView(Context context)
    {
        super(context);
    }

    public WatchlistPositionListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WatchlistPositionListView(Context context, Mode mode)
    {
        super(context, mode);
    }

    public WatchlistPositionListView(Context context, Mode mode, AnimationStyle style)
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
        FrameLayout wrapper = getRefreshableViewWrapper();
        wrapper.removeAllViews();
        wrapper.addView(watchlistListView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
