package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.timeline.TimelineItemDTOEnhanced;
import com.tradehero.th.loaders.TimelineListLoader;

public class TimelineAdapter extends LoaderDTOAdapter<TimelineItemDTOEnhanced, TimelineItemView, TimelineListLoader>
        implements
            PullToRefreshListView.OnRefreshListener<ListView>,
            AbsListView.OnScrollListener,
            PullToRefreshBase.OnLastItemVisibleListener
{
    private int currentScrollState;

    public TimelineAdapter(Context context, LayoutInflater inflater, int timelineLoaderId, int layoutResourceId)
    {
        super(context, inflater, timelineLoaderId, layoutResourceId);
    }

    @Override protected void fineTune(int position, TimelineItemDTOEnhanced dto, TimelineItemView dtoView)
    {
    }

    @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
    {
        switch (refreshView.getCurrentMode())
        {
            case PULL_FROM_START:
                getLoader().loadNext();
                break;
            case PULL_FROM_END:
                getLoader().loadPrevious();
                break;
        }
    }

    @Override public void onScrollStateChanged(final AbsListView absListView, int scrollState)
    {
        currentScrollState = scrollState;
    }

    @Override public void onScroll(final AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if (getCount() == 0)
        {
            return;
        }
        // update loader last & first visible item
        if (getLoader() != null)
        {
            int lastItemId = firstVisibleItem + visibleItemCount > getCount() ? getCount() - 1 : firstVisibleItem + visibleItemCount - 1;
            //strange behavior of onScroll, sometime firstVisibleItem >= getCount(), which is logically wrong, that's why I have to do this check
            int firstItemId = Math.min(firstVisibleItem, getCount() - 1);
            //getLoader().setFirstVisibleItem((TimelineItem) getItem(firstItemId));
            //getLoader().setLastVisibleItem((TimelineItem) getItem(lastItemId));
        }
    }

    @Override public void onLastItemVisible()
    {
        getLoader().loadPrevious();
    }

    @Override public Object getItem(int position)
    {
        return super.getItem(position);
    }

    /**
     * force to render header of the listview
     * somewhat hacKy :v
     * @return
     */
    @Override public boolean isEmpty()
    {
        return false;
    }
}
