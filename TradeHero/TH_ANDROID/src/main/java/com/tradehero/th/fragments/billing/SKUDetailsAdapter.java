package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.billing.googleplay.BaseIABProductDetails;
import com.tradehero.th.adapters.DTOAdapter;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 4:14 PM To change this template use File | Settings | File Templates. */
abstract public class SKUDetailsAdapter<SKUDetailsType extends BaseIABProductDetails,
                                        SKUDetailViewType extends SKUDetailView<SKUDetailsType>>
        extends DTOAdapter<SKUDetailsType, SKUDetailViewType>
{
    public static final String TAG = SKUDetailsAdapter.class.getSimpleName();

    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_VALUE = 1;

    public SKUDetailsAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        switch (position)
        {
            case 0:
                return ITEM_TYPE_HEADER;
            default:
                return ITEM_TYPE_VALUE;
        }
    }

    @Override public int getCount()
    {
        return super.getCount() + 1;
    }

    @Override public Object getItem(int i)
    {
        return getItemViewType(i) == ITEM_TYPE_HEADER ? null : super.getItem(i - 1);
    }

    abstract protected View getHeaderView(int position, View convertView, ViewGroup viewGroup);

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        return getItemViewType(position) == ITEM_TYPE_HEADER ?
                getHeaderView(position, convertView, viewGroup) :
                super.getView(position, convertView instanceof SKUQuickDescriptionView ? null : convertView, viewGroup);
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItemViewType(position) != ITEM_TYPE_HEADER;
    }
}
