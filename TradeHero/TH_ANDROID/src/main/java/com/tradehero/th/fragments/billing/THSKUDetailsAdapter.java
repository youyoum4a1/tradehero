package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.billing.googleplay.THIABProductDetails;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 4:14 PM To change this template use File | Settings | File Templates. */
public class THSKUDetailsAdapter extends SKUDetailsAdapter<THIABProductDetails, StoreSKUDetailView>
{
    public static final String TAG = THSKUDetailsAdapter.class.getSimpleName();

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

    @Override protected View getHeaderView(int position, View convertView, ViewGroup viewGroup)
    {
        SKUQuickDescriptionView quickDescription = convertView instanceof SKUQuickDescriptionView ?
                (SKUQuickDescriptionView) convertView :
                (SKUQuickDescriptionView) inflater.inflate(R.layout.store_quick_message, viewGroup, false);
        quickDescription.linkWithSkuDomain(skuDomain, true);
        return quickDescription;
    }

    @Override protected void fineTune(int position, THIABProductDetails dto, StoreSKUDetailView dtoView)
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
