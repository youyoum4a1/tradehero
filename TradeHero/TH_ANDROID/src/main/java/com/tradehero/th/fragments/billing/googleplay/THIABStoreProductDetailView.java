package com.tradehero.th.fragments.billing.googleplay;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.fragments.billing.StoreProductDetailView;

/**
 * Created by xavier on 3/26/14.
 */
public class THIABStoreProductDetailView extends StoreProductDetailView<
        IABSKU,
        THIABProductDetail>
{
    public THIABStoreProductDetailView(Context context)
    {
        super(context);
    }

    public THIABStoreProductDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public THIABStoreProductDetailView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
}
