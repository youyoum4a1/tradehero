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
    private LeaderboardDefPagedItemListLoader loader;
    private int currentScrollState;

    public LeaderboardRankingAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    public void setLoader(LeaderboardDefPagedItemListLoader loader)
    {
        this.loader = loader;
        setItems(loader.getItems());
    }

    public Loader<List<LeaderboardDefDTO>> getLoader()
    {
        return loader;
    }

    @Override protected View getView(int position, RankingItemView convertView)
    {
        return convertView;
    }

    @Override public void onLastItemVisible()
    {
        // TODO not to refresh too frequent
        loader.loadPreviousPage();
    }

    @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
    {
        switch (refreshView.getCurrentMode())
        {
            case PULL_FROM_START:
                loader.loadNextPage();
                break;
            case PULL_FROM_END:
                loader.loadPreviousPage();
                break;
        }
    }

    @Override public void onScrollStateChanged(AbsListView absListView, int scrollState)
    {
        currentScrollState = scrollState;
    }

    @Override public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if (getCount() > 0 && loader != null)
        {
            int lastItemId = firstVisibleItem + visibleItemCount > getCount() ? getCount() - 1 : firstVisibleItem + visibleItemCount - 1;
            loader.setFirstVisibleItem((LeaderboardDefDTO) getItem(firstVisibleItem));
            loader.setLastVisibleItem((LeaderboardDefDTO) getItem(lastItemId));
        }
    }
}
