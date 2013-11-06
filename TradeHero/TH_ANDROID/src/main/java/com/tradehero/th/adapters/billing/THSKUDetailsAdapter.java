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

    private String skuDomain;

    public THSKUDetailsAdapter(Context context, LayoutInflater inflater)
    {
        super(context, inflater, R.layout.store_sku_detail);
        this.skuDomain = THSKUDetails.DOMAIN_VIRTUAL_DOLLAR;
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

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        if (position == 0)
        {
            SKUQuickDescriptionView quickDescription = (SKUQuickDescriptionView) inflater.inflate(R.layout.store_quick_message, viewGroup, false);
            quickDescription.linkWithSkuDomain(skuDomain, true);
            return quickDescription;
        }
        return super.getView(position, convertView instanceof SKUQuickDescriptionView ? null : convertView, viewGroup);
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
