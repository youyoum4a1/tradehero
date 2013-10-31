package com.tradehero.th.widget.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.list.ExpandingListViewListener;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 3:09 PM Copyright (c) TradeHero */
public class LeaderboardRankingListView extends PullToRefreshListView
{
    @Inject
    protected ExpandingListViewListener defaultExpandingListViewListener;

    //<editor-fold desc="Constructors">
    public LeaderboardRankingListView(Context context)
    {
        super(context);
        init();
    }

    public LeaderboardRankingListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public LeaderboardRankingListView(Context context, Mode mode)
    {
        super(context, mode);
        init();
    }

    public LeaderboardRankingListView(Context context, Mode mode, AnimationStyle style)
    {
        super(context, mode, style);
        init();
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

    @Override public void setOnItemClickListener(AdapterView.OnItemClickListener listener)
    {
        throw new IllegalArgumentException("You are trying to override the default listener");
    }
}
