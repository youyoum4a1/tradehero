package com.tradehero.th.fragments.billing;

import android.content.Context;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.amazon.THAmazonProductDetail;

public class THAmazonSKUDetailAdapter
        extends ProductDetailAdapter<
        AmazonSKU,
        THAmazonProductDetail,
        THAmazonStoreProductDetailView>
{
    //<editor-fold desc="Constructors">
    public THAmazonSKUDetailAdapter(Context context,
            ProductIdentifierDomain skuDomain)
    {
        super(context, R.layout.store_sku_detail_amazon, skuDomain);
    }

    public THAmazonSKUDetailAdapter(Context context, int layoutResourceId,
            ProductIdentifierDomain skuDomain)
    {
        super(context, layoutResourceId, skuDomain);
    }
    //</editor-fold>

    @Override protected void fineTune(int position, THAmazonProductDetail dto, THAmazonStoreProductDetailView dtoView)
    {
    }
}
