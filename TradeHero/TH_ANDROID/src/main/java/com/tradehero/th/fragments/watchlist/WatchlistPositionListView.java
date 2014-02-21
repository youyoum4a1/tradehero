package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/16/14 Time: 5:58 PM Copyright (c) TradeHero
 */
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
        getRefreshableViewWrapper().addView(watchlistListView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
