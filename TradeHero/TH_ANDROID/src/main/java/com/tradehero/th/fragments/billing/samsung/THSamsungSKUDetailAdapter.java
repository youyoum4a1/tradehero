package com.tradehero.th.fragments.billing.samsung;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;
import com.tradehero.th.fragments.billing.ProductDetailAdapter;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 4:14 PM To change this template use File | Settings | File Templates. */
public class THSamsungSKUDetailAdapter
        extends ProductDetailAdapter<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungStoreProductDetailView>
{
    //<editor-fold desc="Constructors">
    public THSamsungSKUDetailAdapter(Context context, LayoutInflater inflater,
            ProductIdentifierDomain skuDomain)
    {
        super(context, inflater, R.layout.store_sku_detail_samsung, skuDomain);
    }

    public THSamsungSKUDetailAdapter(Context context, LayoutInflater inflater, int layoutResourceId,
            ProductIdentifierDomain skuDomain)
    {
        super(context, inflater, layoutResourceId, skuDomain);
    }
    //</editor-fold>

    @Override protected void fineTune(int position, THSamsungProductDetail dto, THSamsungStoreProductDetailView dtoView)
    {
    }
}
