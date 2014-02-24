package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.list.SingleExpandingListViewListener;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 3:09 PM Copyright (c) TradeHero */
public class LeaderboardMarkUserListView extends PullToRefreshListView
{
    @Inject SingleExpandingListViewListener defaultExpandingListViewListener;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserListView(Context context)
    {
        super(context);
    }

    public LeaderboardMarkUserListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardMarkUserListView(Context context, Mode mode)
    {
        super(context, mode);
    }

    public LeaderboardMarkUserListView(Context context, Mode mode, AnimationStyle style)
    {
        super(context, mode, style);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        DaggerUtils.inject(this);
        super.setOnItemClickListener(defaultExpandingListViewListener);
    }

    @Override protected void onDetachedFromWindow()
    {
        super.setOnItemClickListener(null);
        super.onDetachedFromWindow();
    }

    @Override public final void setOnItemClickListener(AdapterView.OnItemClickListener listener)
    {
        throw new IllegalArgumentException("You are trying to override the default listener");
    }
}
