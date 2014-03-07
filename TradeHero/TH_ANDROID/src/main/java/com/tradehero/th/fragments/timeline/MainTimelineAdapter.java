package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sun.org.apache.regexp.internal.recompile;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.fragments.portfolio.PortfolioListItemForProfileAdapter;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import java.util.List;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by xavier on 3/6/14.
 */
public class MainTimelineAdapter extends ArrayAdapter
    implements StickyListHeadersAdapter,
        AbsListView.OnScrollListener,
        PullToRefreshListView.OnRefreshListener<StickyListHeadersListView>,
        PullToRefreshBase.OnLastItemVisibleListener
{
    public static final String TAG = MainTimelineAdapter.class.getSimpleName();

    protected final LayoutInflater inflater;
    private TimelineProfileClickListener profileClickListener;
    private int timelineItemViewResId;
    private int portfolioItemViewResId;
    private TimelineFragment.TabType currentTabType = TimelineFragment.TabType.TIMELINE;

    private TimelineAdapter timelineAdapter;
    private PortfolioListItemForProfileAdapter portfolioListAdapter;

    public MainTimelineAdapter(Context context, LayoutInflater inflater)
    {
        super(context, 0);
        this.inflater = inflater;
    }

    public TimelineFragment.TabType getCurrentTabType()
    {
        return currentTabType;
    }

    public void setCurrentTabType(TimelineFragment.TabType currentTabType)
    {
        this.currentTabType = currentTabType;
        notifyDataSetChanged();
    }

    public void setProfileClickListener(TimelineProfileClickListener profileClickListener)
    {
        this.profileClickListener = profileClickListener;
    }

    protected void notifyProfileClickListener(TimelineFragment.TabType tabType)
    {
        if (profileClickListener != null)
        {
            profileClickListener.onBtnClicked(tabType);
        }
    }

    public void setTimelineItemViewResId(int timelineItemViewResId)
    {
        this.timelineItemViewResId = timelineItemViewResId;
    }

    public void setPortfolioItemViewResId(int portfolioItemViewResId)
    {
        this.portfolioItemViewResId = portfolioItemViewResId;
    }

    //<editor-fold desc="AbsListView.OnScrollListener">
    private int currentScrollState;

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
        if (getTimelineLoader() != null)
        {
            int lastItemId = firstVisibleItem + visibleItemCount > getCount() ? getCount() - 1 : firstVisibleItem + visibleItemCount - 1;
            //strange behavior of onScroll, sometime firstVisibleItem >= getCount(), which is logically wrong, that's why I have to do this check
            int firstItemId = Math.min(firstVisibleItem, getCount() - 1);
            //getTimelineLoader().setFirstVisibleItem((TimelineItem) getItem(firstItemId));
            //getTimelineLoader().setLastVisibleItem((TimelineItem) getItem(lastItemId));
        }
    }
    //</editor-fold>

    //<editor-fold desc="PullToRefreshBase.OnLastItemVisibleListener">
    @Override public void onLastItemVisible()
    {
        getTimelineLoader().loadPrevious();
    }
    //</editor-fold>

    //<editor-fold desc="StickyListHeadersAdapter">
    @Override public long getHeaderId(int position)
    {
        return 0;
    }

    @Override public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.user_profile_detail_bottom_buttons_2_0, parent, false);
            ((TimelineHeaderButtonView) convertView).setTimelineProfileClickListener(new TimelineProfileClickListener()
            {
                @Override public void onBtnClicked(TimelineFragment.TabType tabType)
                {
                    notifyProfileClickListener(tabType);
                }
            });
        }

        return convertView;
    }
    //</editor-fold>

    //<editor-fold desc="PullToRefreshListView.OnRefreshListener<StickyListHeadersListView>">
    @Override public void onRefresh(PullToRefreshBase<StickyListHeadersListView> refreshView)
    {
        switch (refreshView.getCurrentMode())
        {
            case PULL_FROM_START:
                getTimelineLoader().loadNext();
                break;
            case PULL_FROM_END:
                getTimelineLoader().loadPrevious();
                break;
        }
    }
    //</editor-fold>

    //////////////////////
    // Timeline elements
    //////////////////////

    //<editor-fold desc="Timeline Adapter">
    private int loaderId;
    private LoaderDTOAdapter.ListLoaderCallback<TimelineItem> callback;

    public int getLoaderId()
    {
        return loaderId;
    }

    public LoaderManager.LoaderCallbacks<List<TimelineItem>> getLoaderTimelineCallback()
    {
        return new LoaderManager.LoaderCallbacks<List<TimelineItem>>()
        {
            @Override public Loader<List<TimelineItem>> onCreateLoader(int id, Bundle args)
            {
                //loaderId = id;
                return callback != null ? callback.onCreateLoader(id, args) : null;
            }

            @Override public void onLoadFinished(Loader<List<TimelineItem>> loader, List<TimelineItem> data)
            {
                notifyDataSetChanged();

                if (loader instanceof ListLoader && callback != null)
                {
                    callback.onLoadFinished((ListLoader<TimelineItem>) loader, data);
                }
            }

            @Override public void onLoaderReset(Loader<List<TimelineItem>> loader)
            {
                if (loader instanceof ListLoader && callback != null)
                {
                    callback.onLoaderReset((ListLoader<TimelineItem>)loader);
                }
            }
        };
    }

    public TimelineListLoader getTimelineLoader()
    {
        if (getContext() instanceof FragmentActivity)
        {
            Loader loader = (Loader) ((FragmentActivity) getContext()).getSupportLoaderManager().getLoader(getLoaderId());
            return (TimelineListLoader) loader;
        }
        throw new IllegalArgumentException("Context has to be FragmentActivity");
    }

    public void setTimelineLoaderCallback(LoaderDTOAdapter.ListLoaderCallback<TimelineItem> callback)
    {
        this.callback = callback;
    }
    //</editor-fold>

    //////////////////////
    // Portfolio elements
    //////////////////////

    //<editor-fold desc="BaseAdapter">
    @Override public int getCount()
    {
        switch (currentTabType)
        {
            case TIMELINE:
                return timelineAdapter.getCount();

            case PORTFOLIO_LIST:
            default:
                return portfolioListAdapter.getCount();
        }
    }

    @Override public Object getItem(int i)
    {
        switch (currentTabType)
        {
            case TIMELINE:
                return timelineAdapter.getItem(i);

            case PORTFOLIO_LIST:
            default:
                return portfolioListAdapter.getItem(i);
        }
    }

    @Override public long getItemId(int i)
    {
        switch (currentTabType)
        {
            case TIMELINE:
                return timelineAdapter.getItemId(i);

            case PORTFOLIO_LIST:
            default:
                return portfolioListAdapter.getItemId(i);
        }
    }

    @Override public View getView(int i, View view, ViewGroup viewGroup)
    {
        switch (currentTabType)
        {
            case TIMELINE:
                return timelineAdapter.getView(i, view, viewGroup);

            case PORTFOLIO_LIST:
            default:
                return portfolioListAdapter.getView(i, view, viewGroup);
        }
    }
    //</editor-fold>
}
