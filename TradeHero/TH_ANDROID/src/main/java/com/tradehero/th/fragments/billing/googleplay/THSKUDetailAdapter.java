package com.tradehero.th.fragments.billing.googleplay;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.thm.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.fragments.billing.ProductDetailAdapter;

public class THSKUDetailAdapter
        extends ProductDetailAdapter<
            IABSKU,
            THIABProductDetail,
        THIABStoreProductDetailView>
{
    //<editor-fold desc="Constructors">
    public THSKUDetailAdapter(Context context, LayoutInflater inflater,
            ProductIdentifierDomain skuDomain)
    {
        super(context, inflater, R.layout.store_sku_detail, skuDomain);
    }

    public THSKUDetailAdapter(Context context, LayoutInflater inflater, int layoutResourceId,
            ProductIdentifierDomain skuDomain)
    {
        super(context, inflater, layoutResourceId, skuDomain);
    }
    //</editor-fold>

    @Override protected void fineTune(int position, THIABProductDetail dto, THIABStoreProductDetailView dtoView)
    {
    }
}
