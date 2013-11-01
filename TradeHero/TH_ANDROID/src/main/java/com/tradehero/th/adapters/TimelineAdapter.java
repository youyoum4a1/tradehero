package com.tradehero.th.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.loaders.TimelinePagedItemListLoader;
import com.tradehero.th.widget.timeline.TimelineItemView;

public class TimelineAdapter extends DTOAdapter<TimelineItem, TimelineItemView>
        implements PullToRefreshListView.OnRefreshListener<ListView>, AbsListView.OnScrollListener, PullToRefreshBase.OnLastItemVisibleListener
{
    private static final String TAG = TimelineAdapter.class.getName();
    private TimelinePagedItemListLoader loader;
    private int currentScrollState;
    private int onScreenMiddleItemPosition;

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

    @Override protected void fineTune(int position, TimelineItem dto, TimelineItemView dtoView)
    {
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

    @Override public void onScrollStateChanged(final AbsListView absListView, int scrollState)
    {
        currentScrollState = scrollState;

        if (currentScrollState != SCROLL_STATE_FLING)
        {
            absListView.post(new Runnable()
            {
                @Override public void run()
                {
                    absListView.setItemChecked(onScreenMiddleItemPosition, true);
                }
            });
        }
    }

    @Override public void onScroll(final AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if (getCount() == 0)
        {
            return;
        }
        // update loader last & first visible item
        if (loader != null)
        {
            int lastItemId = firstVisibleItem + visibleItemCount > getCount() ? getCount() - 1 : firstVisibleItem + visibleItemCount - 1;
            // strange behavior of onScroll, sometime firstVisibleItem >= getCount(), which is logically wrong, that's why I have to do this check
            int firstItemId = Math.min(firstVisibleItem, getCount() - 1);
            loader.setFirstVisibleItem((TimelineItem) getItem(firstItemId));
            loader.setLastVisibleItem((TimelineItem) getItem(lastItemId));
        }

        onScreenMiddleItemPosition = firstVisibleItem + (visibleItemCount-1) / 2;

        // TODO when scrolling speed is low, display button bar
        //if (currentScrollState == SCROLL_STATE_TOUCH_SCROLL)
        //{
        //    absListView.post(new Runnable()
        //    {
        //        @Override public void run()
        //        {
        //            absListView.setItemChecked(onScreenMiddleItemPosition, true);
        //        }
        //    });
        //}
    }

    @Override public void onLastItemVisible()
    {
        loader.loadPreviousPage();
    }
}
