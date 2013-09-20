package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.loaders.TimelinePagedItemListLoader;
import com.tradehero.th.widget.timeline.TimelineItemView;

public class TimelineAdapter extends DTOAdapter<TimelineItem, TimelineItemView>
        implements PullToRefreshListView.OnRefreshListener<ListView>, AbsListView.OnScrollListener, PullToRefreshBase.OnLastItemVisibleListener
{
    private TimelinePagedItemListLoader loader;
    private int currentScrollState;

    public TimelineAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    public void setLoader(TimelinePagedItemListLoader loader)
    {
        this.loader = loader;
        setItems(loader.getItems());
    }

    public TimelinePagedItemListLoader getLoader()
    {
        return loader;
    }

    @Override protected View getView(int position, TimelineItemView convertView)
    {
        return convertView;
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
            loader.setFirstVisibleItem((TimelineItem) getItem(firstVisibleItem));
            loader.setLastVisibleItem((TimelineItem) getItem(lastItemId));
        }
    }

    @Override public void onLastItemVisible()
    {
        // TODO not to refresh too frequent
        loader.loadPreviousPage();
    }
}
