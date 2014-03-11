package com.tradehero.th.fragments.timeline;

import android.app.Activity;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.portfolio.SimpleOwnPortfolioListItemAdapter;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.utils.Constants;
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
    private OnLoadFinishedListener onLoadFinishedListener;
    private TimelineFragment.TabType currentTabType = TimelineFragment.TabType.TIMELINE;

    private TimelineAdapter timelineAdapter;
    private SimpleOwnPortfolioListItemAdapter portfolioListAdapter;

    public MainTimelineAdapter(Activity context,
            LayoutInflater inflater,
            UserBaseKey shownUserBaseKey,
            int timelineItemViewResId,
            int portfolioItemViewResId)
    {
        super(context, 0);
        this.inflater = inflater;

        timelineAdapter = new TimelineAdapter(context, inflater, shownUserBaseKey.key, timelineItemViewResId);
        timelineAdapter.setDTOLoaderCallback(createTimelineLoaderCallback(context, shownUserBaseKey));
        portfolioListAdapter = new SimpleOwnPortfolioListItemAdapter(context, inflater, portfolioItemViewResId);
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

    public void setOnLoadFinishedListener(OnLoadFinishedListener onLoadFinishedListener)
    {
        this.onLoadFinishedListener = onLoadFinishedListener;
    }

    protected void notifyLoadFinished()
    {
        if (this.onLoadFinishedListener != null)
        {
            this.onLoadFinishedListener.onLoadFinished();
        }
    }

    //<editor-fold desc="AbsListView.OnScrollListener">
    private int currentScrollState;

    @Override public void onScrollStateChanged(final AbsListView absListView, int scrollState)
    {
        currentScrollState = scrollState;
    }

    @Override public void onScroll(final AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        switch (currentTabType)
        {
            case TIMELINE:
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
                break;


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
        switch(currentTabType)
        {
            case TIMELINE:
                switch (refreshView.getCurrentMode())
                {
                    case PULL_FROM_START:
                        getTimelineLoader().loadNext();
                        break;
                    case PULL_FROM_END:
                        getTimelineLoader().loadPrevious();
                        break;
                }
                break;

            case PORTFOLIO_LIST:
                break;

            case STATS:
                break;
        }
    }
    //</editor-fold>

    //////////////////////
    // Timeline elements
    //////////////////////

    //<editor-fold desc="Timeline Adapter">
    public int getTimelineLoaderId()
    {
        return timelineAdapter.getLoaderId();
    }

    public LoaderManager.LoaderCallbacks<List<TimelineItem>> getLoaderTimelineCallback()
    {
        return timelineAdapter.getLoaderCallback();
    }

    public TimelineListLoader getTimelineLoader()
    {
        if (getContext() instanceof FragmentActivity)
        {
            Loader loader = (Loader) ((FragmentActivity) getContext()).getSupportLoaderManager().getLoader(getTimelineLoaderId());
            return (TimelineListLoader) loader;
        }
        throw new IllegalArgumentException("Context has to be FragmentActivity");
    }

    public LoaderDTOAdapter.ListLoaderCallback<TimelineItem> createTimelineLoaderCallback(final Context context, final UserBaseKey shownUserBaseKey)
    {
        return new LoaderDTOAdapter.ListLoaderCallback<TimelineItem>()
        {
            @Override public void onLoadFinished(ListLoader<TimelineItem> loader, List<TimelineItem> data)
            {
                notifyDataSetChanged();
                notifyLoadFinished();
            }

            @Override public ListLoader<TimelineItem> onCreateLoader(Bundle args)
            {
                return createTimelineLoader(context, shownUserBaseKey);
            }
        };
    }

    private ListLoader<TimelineItem> createTimelineLoader(Context context, UserBaseKey shownUserBaseKey)
    {
        TimelineListLoader timelineLoader = new TimelineListLoader(context, shownUserBaseKey);
        timelineLoader.setPerPage(Constants.TIMELINE_ITEM_PER_PAGE);
        return timelineLoader;
    }
    //</editor-fold>

    //////////////////////
    // Portfolio elements
    //////////////////////

    public void setDisplayablePortfolioItems(List<DisplayablePortfolioDTO> items)
    {
        this.portfolioListAdapter.setItems(items);
        this.portfolioListAdapter.notifyDataSetChanged();
        if (currentTabType == TimelineFragment.TabType.PORTFOLIO_LIST)
        {
            notifyDataSetChanged();
        }
    }

    //<editor-fold desc="BaseAdapter">
    @Override public int getCount()
    {
        int count;
        switch (currentTabType)
        {
            case TIMELINE:
                count = timelineAdapter.getCount();
                break;

            case PORTFOLIO_LIST:
            default:
                count = portfolioListAdapter.getCount();
        }
        return count;
    }

    @Override public Object getItem(int i)
    {
        Object item;
        switch (currentTabType)
        {
            case TIMELINE:
                item = timelineAdapter.getItem(i);
                break;

            case PORTFOLIO_LIST:
            default:
                item = portfolioListAdapter.getItem(i);
        }
        return item;
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

    public static interface OnLoadFinishedListener
    {
        void onLoadFinished();
    }
}
