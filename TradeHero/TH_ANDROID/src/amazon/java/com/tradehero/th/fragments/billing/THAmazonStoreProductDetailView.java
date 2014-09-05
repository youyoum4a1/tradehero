package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.billing.amazon.THAmazonProductDetail;

public class THAmazonStoreProductDetailView extends StoreProductDetailView<
        AmazonSKU,
        THAmazonProductDetail>
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") public THAmazonStoreProductDetailView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration") public THAmazonStoreProductDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration") public THAmazonStoreProductDetailView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>
}
