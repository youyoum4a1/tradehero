package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.googleplay.THIABProductDetail;

public class THIABStoreProductDetailView extends StoreProductDetailView<
        IABSKU,
        THIABProductDetail>
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") public THIABStoreProductDetailView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration") public THIABStoreProductDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration") public THIABStoreProductDetailView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>
}
