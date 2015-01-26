package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.googleplay.THIABProductDetail;

public class THIABSKUDetailAdapter
        extends ProductDetailAdapter<
            IABSKU,
            THIABProductDetail,
            THIABStoreProductDetailView>
{
    //<editor-fold desc="Constructors">
    public THIABSKUDetailAdapter(@NonNull Context context,
            ProductIdentifierDomain skuDomain)
    {
        super(context, R.layout.store_sku_detail_iab, skuDomain);
    }

    public THIABSKUDetailAdapter(@NonNull Context context, @LayoutRes int layoutResourceId,
            ProductIdentifierDomain skuDomain)
    {
        super(context, layoutResourceId, skuDomain);
    }
    //</editor-fold>
}
