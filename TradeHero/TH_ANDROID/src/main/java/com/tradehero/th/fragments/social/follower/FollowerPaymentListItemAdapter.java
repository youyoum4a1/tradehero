package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.social.FollowerTransactionDTO;
import com.tradehero.th.widget.list.BaseListHeaderView;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class FollowerPaymentListItemAdapter extends DTOAdapter<FollowerTransactionDTO, FollowerPaymentListItemView>
{
    public static final String TAG = FollowerPaymentListItemAdapter.class.getSimpleName();

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    public static final int POSITION_HEADER = 0;
    public static final int SUPER_POSITION_OFFSET = 1;

    private final int headerResId;

    public FollowerPaymentListItemAdapter(Context context, LayoutInflater inflater, int followerPaymentLayoutResourceId, int headerResId)
    {
        super(context, inflater, followerPaymentLayoutResourceId);
        this.headerResId = headerResId;
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        return position == POSITION_HEADER ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override public int getCount()
    {
        return super.getCount() + 1;
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).hashCode();
    }

    @Override public Object getItem(int position)
    {
        return getItemViewType(position) == VIEW_TYPE_HEADER ? "header" : super.getItem(getAdjustedSuperPosition(position));
    }

    public int getAdjustedSuperPosition(int position)
    {
        return position - SUPER_POSITION_OFFSET;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (getItemViewType(position) == VIEW_TYPE_HEADER)
        {
            if (!(convertView instanceof BaseListHeaderView))
            {
                convertView = inflater.inflate(headerResId, parent, false);
            }
            ((BaseListHeaderView) convertView).setHeaderTextContent(context.getString(R.string.manage_follower_payment_transaction_list_header));
        }
        else
        {
            if (!(convertView instanceof FollowerPaymentListItemView))
            {
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }
            ((FollowerPaymentListItemView) convertView).display((FollowerTransactionDTO) getItem(position));
        }
        return convertView;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItemViewType(position) != VIEW_TYPE_HEADER;
    }

    @Override protected void fineTune(int position, FollowerTransactionDTO dto, FollowerPaymentListItemView dtoView)
    {
        // Nothing to do
    }
}
