package com.tradehero.th.adapters.billing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.widget.billing.SKUQuickDescriptionView;
import com.tradehero.th.widget.billing.StoreSKUDetailView;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 4:14 PM To change this template use File | Settings | File Templates. */
public class THSKUDetailsAdapter extends DTOAdapter<THSKUDetails, StoreSKUDetailView>
{
    public static final String TAG = THSKUDetailsAdapter.class.getSimpleName();

    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_VALUE = 1;

    private String skuDomain;

    public THSKUDetailsAdapter(Context context, LayoutInflater inflater, String skuDomain)
    {
        super(context, inflater, R.layout.store_sku_detail);
        this.skuDomain = skuDomain;
    }

    public THSKUDetailsAdapter(Context context, LayoutInflater inflater, int layoutResourceId, String skuDomain)
    {
        super(context, inflater, layoutResourceId);
        this.skuDomain = skuDomain;
    }

    @Override public int getCount()
    {
        return super.getCount() + 1;
    }

    @Override public Object getItem(int i)
    {
        return i == 0 ? null : super.getItem(i - 1);
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

    protected View getHeaderView(int position, View convertView, ViewGroup viewGroup)
    {
        SKUQuickDescriptionView quickDescription = convertView instanceof SKUQuickDescriptionView ?
                (SKUQuickDescriptionView) convertView :
                (SKUQuickDescriptionView) inflater.inflate(R.layout.store_quick_message, viewGroup, false);
        quickDescription.linkWithSkuDomain(skuDomain, true);
        return quickDescription;
    }

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

    @Override protected void fineTune(int position, THSKUDetails dto, StoreSKUDetailView dtoView)
    {
    }

    public String getSkuDomain()
    {
        return skuDomain;
    }

    public void setSkuDomain(String skuDomain)
    {
        this.skuDomain = skuDomain;
    }
}
