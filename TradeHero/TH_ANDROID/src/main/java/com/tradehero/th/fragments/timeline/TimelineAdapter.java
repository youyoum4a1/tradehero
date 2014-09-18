package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.loaders.TimelineListLoader;

public class TimelineAdapter extends LoaderDTOAdapter<TimelineItemDTOKey, TimelineItemViewLinear, TimelineListLoader>
        implements
            PullToRefreshListView.OnRefreshListener<ListView>,
            PullToRefreshBase.OnLastItemVisibleListener
{
    public TimelineAdapter(Context context, LayoutInflater inflater, int timelineLoaderId, int layoutResourceId)
    {
        super(context, inflater, timelineLoaderId, layoutResourceId);
    }

    @Override protected void fineTune(int position, TimelineItemDTOKey dto, TimelineItemViewLinear dtoView)
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
