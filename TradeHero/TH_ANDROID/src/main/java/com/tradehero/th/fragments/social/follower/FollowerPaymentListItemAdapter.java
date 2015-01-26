package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.social.FollowerTransactionDTO;
import com.tradehero.th.widget.list.BaseListHeaderView;

public class FollowerPaymentListItemAdapter extends ArrayDTOAdapter<FollowerTransactionDTO, FollowerPaymentListItemView>
{
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    public static final int POSITION_HEADER = 0;
    public static final int SUPER_POSITION_OFFSET = 1;

    @LayoutRes private final int headerResId;

    //<editor-fold desc="Constructors">
    public FollowerPaymentListItemAdapter(@NonNull Context context,
            @LayoutRes int followerPaymentLayoutResourceId,
            @LayoutRes int headerResId)
    {
        super(context, followerPaymentLayoutResourceId);
        this.headerResId = headerResId;
    }
    //</editor-fold>

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

    private int getHeaderCount()
    {   //
        return 1;
        //return 0;
    }
    @Override public int getCount()
    {
        return super.getCount() + getHeaderCount();
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
                convertView = getInflater().inflate(headerResId, parent, false);
            }
            ((BaseListHeaderView) convertView).setHeaderTextContent(getContext().getString(R.string.manage_follower_payment_transaction_list_header));
        }
        else
        {
            if (!(convertView instanceof FollowerPaymentListItemView))
            {
                convertView = conditionalInflate(position, convertView, parent);
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
