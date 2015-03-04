package com.tradehero.th.fragments.timeline;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.pagination.RangeDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.portfolio.SimpleOwnPortfolioListItemAdapter;
import java.util.List;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MainTimelineAdapter extends ArrayAdapter
    implements StickyListHeadersAdapter
{
    public static final int EMPTY_ITEM_TYPE = 0;
    public static final int PORTFOLIO_ITEM_TYPE_OFFSET = 1;

    @Nullable private TimelineProfileClickListener profileClickListener;
    @NonNull private TimelineFragment.TabType currentTabType = TimelineFragment.TabType.PORTFOLIO_LIST;

    private final SimpleOwnPortfolioListItemAdapter portfolioListAdapter;
    private final int portfolioAdapterViewTypeOffset;

    private final SubTimelineAdapterNew subTimelineAdapter;
    private final int timelineAdapterViewTypeOffset;

    @LayoutRes private final int statResId;
    private final int statViewTypeOffset;

    private UserProfileDTO userProfileDTO;

    //<editor-fold desc="Constructors">
    public MainTimelineAdapter(@NonNull Activity context,
            @LayoutRes int timelineItemViewResId,
            @LayoutRes int portfolioItemViewResId,
            @LayoutRes int statResId)
    {
        super(context, 0);
        portfolioListAdapter = new SimpleOwnPortfolioListItemAdapter(context, portfolioItemViewResId);
        portfolioAdapterViewTypeOffset = PORTFOLIO_ITEM_TYPE_OFFSET;

        subTimelineAdapter = new SubTimelineAdapterNew(context, timelineItemViewResId);
        timelineAdapterViewTypeOffset = portfolioAdapterViewTypeOffset + portfolioListAdapter.getViewTypeCount();

        this.statResId = statResId;
        statViewTypeOffset = timelineAdapterViewTypeOffset + subTimelineAdapter.getViewTypeCount();
    }
    //</editor-fold>

    public void setCurrentTabType(@NonNull TimelineFragment.TabType currentTabType)
    {
        this.currentTabType = currentTabType;
        notifyDataSetChanged();
    }

    public void setProfileClickListener(@Nullable TimelineProfileClickListener profileClickListener)
    {
        this.profileClickListener = profileClickListener;
    }

    protected void notifyProfileClickListener(@NonNull TimelineFragment.TabType tabType)
    {
        TimelineProfileClickListener listenerCopy = profileClickListener;
        if (listenerCopy != null)
        {
            listenerCopy.onBtnClicked(tabType);
        }
    }

    //<editor-fold desc="StickyListHeadersAdapter">
    @Override public long getHeaderId(int position)
    {
        return 0;
    }

    @Override public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_profile_detail_bottom_buttons, parent, false);
        }
        TimelineHeaderButtonView castedView = (TimelineHeaderButtonView) convertView;
        castedView.changeButtonLook(currentTabType);
        castedView.setTimelineProfileClickListener(new TimelineProfileClickListener()
        {
            @Override public void onBtnClicked(@NonNull TimelineFragment.TabType tabType)
            {
                MainTimelineAdapter.this.notifyProfileClickListener(tabType);
            }
        });

        return convertView;
    }
    //</editor-fold>

    //////////////////////
    // Timeline elements
    //////////////////////

    public void appendHeadTimeline(@Nullable List<? extends TimelineItemDTO> newOnes)
    {
        subTimelineAdapter.appendHead(newOnes);
        subTimelineAdapter.notifyDataSetChanged();
        notifyDataSetChanged();
    }

    public void appendTailTimeline(@Nullable List<? extends TimelineItemDTO> newOnes)
    {
        subTimelineAdapter.appendTail(newOnes);
        subTimelineAdapter.notifyDataSetChanged();
        notifyDataSetChanged();
    }

    public RangeDTO getLatestTimelineRange()
    {
        return subTimelineAdapter.getLatestRange();
    }

    public RangeDTO getOlderTimelineRange()
    {
        return subTimelineAdapter.getOlderRange();
    }

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
            convertView = LayoutInflater.from(getContext()).inflate(statResId, viewGroup, false);
        }
        return convertView;
    }

    //<editor-fold desc="BaseAdapter">
    @Override public int getViewTypeCount()
    {
        return statViewTypeOffset + 1;
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
                case PORTFOLIO_LIST:
                    viewType = portfolioAdapterViewTypeOffset + portfolioListAdapter.getItemViewType(position);
                    break;

                case TIMELINE:
                    viewType = timelineAdapterViewTypeOffset + subTimelineAdapter.getItemViewType(position);
                    break;

                case STATS:
                    viewType = statViewTypeOffset;
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
}
