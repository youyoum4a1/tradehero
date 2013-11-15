package com.tradehero.th.fragments.billing.management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.HeroPayoutDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.widget.list.BaseListHeaderView;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class AlertListItemAdapter extends BaseAdapter
{
    public static final String TAG = AlertListItemAdapter.class.getName();

    public static final int VIEW_TYPE_HEADER_ACTIVE = 0;
    public static final int VIEW_TYPE_HEADER_INACTIVE = 1;
    public static final int VIEW_TYPE_ITEM_ALERT = 2;

    protected final Context context;
    protected final LayoutInflater inflater;
    protected final int headerResId;
    protected final int payoutResId;
    protected final int payoutNoneResId;
    protected final int followerResId;
    protected final int followerNoneResId;
    protected FollowerSummaryDTO followerSummaryDTO;

    public AlertListItemAdapter(Context context, LayoutInflater inflater, int headerResId, int payoutResId, int payoutNoneResId, int followerResId,
            int followerNoneResId)
    {
        super();
        this.context = context;
        this.inflater = inflater;
        this.headerResId = headerResId;
        this.payoutResId = payoutResId;
        this.payoutNoneResId = payoutNoneResId;
        this.followerResId = followerResId;
        this.followerNoneResId = followerNoneResId;
    }

    public void setFollowerSummaryDTO(FollowerSummaryDTO followerSummaryDTO)
    {
        this.followerSummaryDTO = followerSummaryDTO;
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public int getViewTypeCount()
    {
        return 3;
    }

    public int getPayoutRealCount()
    {
        return (followerSummaryDTO == null || followerSummaryDTO.payoutSummary == null || followerSummaryDTO.payoutSummary.payouts == null) ?
                0 :
                followerSummaryDTO.payoutSummary.payouts.size();
    }

    public int getPayoutVisibleCount()
    {
        return Math.max (1, getPayoutRealCount());
    }

    public int getFollowerRealCount()
    {
        return (followerSummaryDTO == null || followerSummaryDTO.userFollowers == null) ?
                0 :
                followerSummaryDTO.userFollowers.size();
    }

    public int getFollowerVisibleCount()
    {
        return Math.max (1, getFollowerRealCount());
    }

    @Override public int getItemViewType(int position)
    {
        if (position <= 0)
        {
            return VIEW_TYPE_HEADER_ACTIVE;
        }
        else if (position <= getPayoutVisibleCount())
        {
            return VIEW_TYPE_ITEM_ALERT;
        }
        else if (position == getPayoutVisibleCount() + 1)
        {
            return VIEW_TYPE_HEADER_INACTIVE;
        }
        else
        {
            return VIEW_TYPE_ITEM_ALERT;
        }
    }

    @Override public int getCount()
    {
        return 2 + getPayoutVisibleCount() + getFollowerVisibleCount();
    }

    @Override public long getItemId(int position)
    {
        Object item = getItem(position);
        return item == null ? 0 : item.hashCode();
    }

    @Override public Object getItem(int position)
    {
        switch(getItemViewType(position))
        {
            case VIEW_TYPE_ITEM_ALERT:
                return getPayoutForPosition(position);
            case VIEW_TYPE_HEADER_ACTIVE:
                return "active";
            case VIEW_TYPE_HEADER_INACTIVE:
                return "inactive";

            default:
                throw new IllegalStateException(getItemViewType(position) + " is not a known view type");
        }
    }

    public int getPositionToPayoutIndex(int position)
    {
        return position - 1;
    }

    public HeroPayoutDTO getPayoutForPosition(int position)
    {
        return (followerSummaryDTO == null || followerSummaryDTO.payoutSummary == null || followerSummaryDTO.payoutSummary.payouts == null) ?
                null :
                followerSummaryDTO.payoutSummary.payouts.get(getPositionToPayoutIndex(position));
    }

    public int getPositionToFollowerIndex(int position)
    {
        return position - 2 - getPayoutVisibleCount();
    }

    public UserFollowerDTO getFollowerForPosition(int position)
    {
        return (followerSummaryDTO == null || followerSummaryDTO.userFollowers == null) ?
                null :
                followerSummaryDTO.userFollowers.get(getPositionToFollowerIndex(position));
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_ITEM_ALERT:
                if (!(convertView instanceof HeroPayoutListItemView))
                {
                    convertView = inflater.inflate(payoutResId, parent, false);
                }
                ((HeroPayoutListItemView) convertView).display((HeroPayoutDTO) getItem(position));
                break;

            case VIEW_TYPE_HEADER_ACTIVE:
                convertView = inflater.inflate(headerResId, parent, false);

                int stringId = position == 0 ? R.string.manage_followers_payout_list_header : R.string.manage_followers_list_header;
                int count = position == 0 ? getPayoutRealCount() : getFollowerRealCount();
                ((BaseListHeaderView) convertView).setHeaderTextContent(String.format(context.getString(stringId), count));
                break;

            case VIEW_TYPE_HEADER_INACTIVE:
                if (!(convertView instanceof FollowerListItemView))
                {
                    convertView = inflater.inflate(followerResId, parent, false);
                }
                ((FollowerListItemView) convertView).display((UserFollowerDTO) getItem(position));
                break;

            default:
                throw new IllegalStateException("Unhandled view type " + getItemViewType(position));
        }
        return convertView;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItemViewType(position) == VIEW_TYPE_ITEM_ALERT;
    }
}
