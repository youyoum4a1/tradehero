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
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.portfolio.SimpleOwnPortfolioListItemAdapter;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.utils.Constants;
import java.util.List;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class MainTimelineAdapter extends ArrayAdapter
    implements StickyListHeadersAdapter,
        AbsListView.OnScrollListener,
        PullToRefreshListView.OnRefreshListener<StickyListHeadersListView>
{
    public static final int TIMELINE_ITEM_TYPE = 0;
    public static final int PORTFOLIO_ITEM_TYPE = 1;
    public static final int STATS_ITEM_TYPE = 2;
    public static final int EMPTY_ITEM_TYPE = 3;

    protected final LayoutInflater inflater;
    private TimelineProfileClickListener profileClickListener;
    private OnLoadFinishedListener onLoadFinishedListener;
    private TimelineFragment.TabType currentTabType = TimelineFragment.TabType.TIMELINE;

    private SubTimelineAdapter subTimelineAdapter;

    private SimpleOwnPortfolioListItemAdapter portfolioListAdapter;
    private final int statResId;
    private UserProfileDTO userProfileDTO;

    public MainTimelineAdapter(Activity context,
            LayoutInflater inflater,
            UserBaseKey shownUserBaseKey,
            int timelineItemViewResId,
            int portfolioItemViewResId,
            int statResId)
    {
        super(context, 0);
        this.inflater = inflater;

        subTimelineAdapter = new SubTimelineAdapter(context, inflater, shownUserBaseKey.key, timelineItemViewResId);
        subTimelineAdapter.setDTOLoaderCallback(createTimelineLoaderCallback(context, shownUserBaseKey));

        portfolioListAdapter = new SimpleOwnPortfolioListItemAdapter(context, inflater, portfolioItemViewResId);

        this.statResId = statResId;
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

    protected void notifyBeginRefresh(TimelineFragment.TabType tabType)
    {
        if (this.onLoadFinishedListener != null)
        {
            this.onLoadFinishedListener.onBeginRefresh(tabType);
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
        }
        TimelineHeaderButtonView castedView = (TimelineHeaderButtonView) convertView;
        castedView.changeButtonLook(currentTabType);
        castedView.setTimelineProfileClickListener(new TimelineProfileClickListener()
        {
            @Override public void onBtnClicked(TimelineFragment.TabType tabType)
            {
                notifyProfileClickListener(tabType);
            }
        });

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
                notifyBeginRefresh(currentTabType);
                break;

            case STATS:
                notifyBeginRefresh(currentTabType);
                break;

            default:
                throw new IllegalArgumentException("Unhandled tabType " + currentTabType);
        }
    }
    //</editor-fold>

    //////////////////////
    // Timeline elements
    //////////////////////

    //<editor-fold desc="Timeline Adapter">
    public int getTimelineLoaderId()
    {
        return subTimelineAdapter.getLoaderId();
    }

    public LoaderManager.LoaderCallbacks<List<TimelineItemDTOKey>> getLoaderTimelineCallback()
    {
        return subTimelineAdapter.getLoaderCallback();
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

    public LoaderDTOAdapter.ListLoaderCallback<TimelineItemDTOKey> createTimelineLoaderCallback(final Context context, final UserBaseKey shownUserBaseKey)
    {
        return new LoaderDTOAdapter.ListLoaderCallback<TimelineItemDTOKey>()
        {
            @Override public void onLoadFinished(ListLoader<TimelineItemDTOKey> loader, List<TimelineItemDTOKey> data)
            {
                notifyDataSetChanged();
                notifyLoadFinished();
            }

            @Override public ListLoader<TimelineItemDTOKey> onCreateLoader(Bundle args)
            {
                return createTimelineLoader(context, shownUserBaseKey);
            }
        };
    }

    private ListLoader<TimelineItemDTOKey> createTimelineLoader(Context context, UserBaseKey shownUserBaseKey)
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

    //////////////////////
    // Stat elements
    //////////////////////

    public void setUserProfileDTO(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
        if (currentTabType == TimelineFragment.TabType.STATS)
        {
            notifyDataSetChanged();
        }
    }

    protected View getStatView(View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(statResId, viewGroup, false);
        }
        return convertView;
    }

    //<editor-fold desc="BaseAdapter">
    @Override public int getViewTypeCount()
    {
        return 4;
    }

    @Override public int getItemViewType(int position)
    {
        int viewType;
        if (getRealCount() == 0)
        {
            viewType = EMPTY_ITEM_TYPE;
        }
        else
        {
            switch (currentTabType)
            {
                case TIMELINE:
                    viewType = subTimelineAdapter.getItemViewType(position);
                    break;

                case PORTFOLIO_LIST:
                    viewType = portfolioListAdapter.getItemViewType(position);
                    break;

                case STATS:
                    viewType = STATS_ITEM_TYPE;
                    break;

                default:
                    throw new IllegalArgumentException("Unhandled tabType " + currentTabType);
            }
        }
        return viewType;
    }

    public int getRealCount()
    {
        int count;
        switch (currentTabType)
        {
            case TIMELINE:
                count = subTimelineAdapter.getCount();
                break;

            case PORTFOLIO_LIST:
            default:
                count = portfolioListAdapter.getCount();
                break;

            case STATS:
                count = 1;
                break;
        }
        return count;
    }

    @Override public int getCount()
    {
        // We want at least 1, so that the sticky header is visible
        return Math.max(1, getRealCount());
    }

    @Override public Object getItem(int i)
    {
        Object item;
        if (getRealCount() == 0)
        {
            item = null;
        }
        else
        {
            switch (currentTabType)
            {
                case TIMELINE:
                    item = subTimelineAdapter.getItem(i);
                    break;

                case PORTFOLIO_LIST:
                    item = portfolioListAdapter.getItem(i);
                    break;

                case STATS:
                    item = userProfileDTO;
                    break;

                default:
                    throw new IllegalArgumentException("Unhandled tabType " + currentTabType);
            }
        }
        return item;
    }

    @Override public boolean hasStableIds()
    {
        return false;
    }

    @Override public long getItemId(int i)
    {
        long itemId;
        if (getRealCount() == 0)
        {
            itemId = 0;
        }
        else
        {
            switch (currentTabType)
            {
                case TIMELINE:
                    itemId = subTimelineAdapter.getItemId(i);
                    break;

                case PORTFOLIO_LIST:
                    itemId = portfolioListAdapter.getItemId(i);
                    break;

                case STATS:
                    itemId = 432;
                    break;

                default:
                    throw new IllegalArgumentException("Unhandled tabType " + currentTabType);
            }
        }
        return itemId;
    }

    @Override public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (getRealCount() == 0)
        {
            view = new TextView(getContext());
        }
        else
        {
            switch (currentTabType)
            {
                case TIMELINE:
                    view = subTimelineAdapter.getView(i, view, viewGroup);
                    break;

                case PORTFOLIO_LIST:
                    view = portfolioListAdapter.getView(i, view, viewGroup);
                    break;

                case STATS:
                    view = getStatView(view, viewGroup);
                    ((UserProfileDetailView) view).display(userProfileDTO);
                    break;

                default:
                    throw new IllegalArgumentException("Unhandled tabType: " + currentTabType);
            }
        }
        return view;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        boolean enabled;
        if (getRealCount() == 0)
        {
            enabled = false;
        }
        else
        {
            switch (currentTabType)
            {
                case TIMELINE:
                    enabled = subTimelineAdapter.isEnabled(position);
                    break;

                case PORTFOLIO_LIST:
                    enabled = portfolioListAdapter.isEnabled(position);
                    break;

                case STATS:
                    enabled = false;
                    break;

                default:
                    throw new IllegalArgumentException("Unhandled tabType " + currentTabType);
            }
        }
        return enabled;
    }
    //</editor-fold>

    public static interface OnLoadFinishedListener
    {
        void onLoadFinished();

        void onBeginRefresh(TimelineFragment.TabType tabType);
    }
}
