package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.support.annotation.LayoutRes;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;
import android.support.annotation.NonNull;

public class THSamsungSKUDetailAdapter
        extends ProductDetailAdapter<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungStoreProductDetailView>
{
    //<editor-fold desc="Constructors">
    public THSamsungSKUDetailAdapter(@NonNull Context context,
            ProductIdentifierDomain skuDomain)
    {
        super(context, R.layout.store_sku_detail_samsung, skuDomain);
    }

    public THSamsungSKUDetailAdapter(@NonNull Context context, @LayoutRes int layoutResourceId,
            ProductIdentifierDomain skuDomain)
    {
        super(context, layoutResourceId, skuDomain);
    }
    //</editor-fold>

    @Override protected void fineTune(int position, THSamsungProductDetail dto, THSamsungStoreProductDetailView dtoView)
    {
    }
}
