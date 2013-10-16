package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.widget.timeline.TimelineItemView;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 2:49 PM Copyright (c) TradeHero */
public class LeaderboardRankingAdapter extends DTOAdapter<LeaderboardDefDTO, RankingItemView>
        implements PullToRefreshListView.OnRefreshListener<ListView>, AbsListView.OnScrollListener, PullToRefreshBase.OnLastItemVisibleListener
{
    public LeaderboardRankingAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    public Loader<List<LeaderboardDefDTO>> getLoader()
    {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    @Override protected View getView(int position, RankingItemView convertView)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void onLastItemVisible()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void onScrollStateChanged(AbsListView absListView, int i)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void onScroll(AbsListView absListView, int i, int i2, int i3)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
