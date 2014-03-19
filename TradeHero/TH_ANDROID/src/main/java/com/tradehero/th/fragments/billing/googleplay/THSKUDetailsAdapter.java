package com.tradehero.th.fragments.billing.googleplay;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.R;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.fragments.billing.StoreSKUDetailView;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 4:14 PM To change this template use File | Settings | File Templates. */
public class THSKUDetailsAdapter extends SKUDetailsAdapter<THIABProductDetail, StoreSKUDetailView>
{
    public static final String TAG = THSKUDetailsAdapter.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public THSKUDetailsAdapter(Context context, LayoutInflater inflater, String skuDomain)
    {
        super(context, inflater, R.layout.store_sku_detail, skuDomain);
    }

    public THSKUDetailsAdapter(Context context, LayoutInflater inflater, int layoutResourceId, String skuDomain)
    {
        super(context, inflater, layoutResourceId, skuDomain);
    }
    //</editor-fold>

    @Override protected void fineTune(int position, THIABProductDetail dto, StoreSKUDetailView dtoView)
    {
    }
}
