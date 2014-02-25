package com.tradehero.th.fragments.billing.googleplay;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.common.billing.googleplay.BaseIABProductDetail;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.fragments.billing.ProductDetailView;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 2:49 PM To change this template use File | Settings | File Templates. */
public class SKUDetailView<SKUDetailsType extends BaseIABProductDetail>
        extends ProductDetailView<
            IABSKU,
            SKUDetailsType>
{
    public static final String TAG = SKUDetailView.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public SKUDetailView(Context context)
    {
        super(context);
    }

    public SKUDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SKUDetailView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    protected void displayPrice()
    {
        if (skuPrice != null)
        {
            if (skuDetails != null)
            {
                skuPrice.setText(skuDetails.price);
            }
        }
    }

    protected void displayDeliverableText()
    {
        if (deliverableText != null)
        {
            if (skuDetails != null)
            {
                deliverableText.setText(skuDetails.description);
            }
        }
    }
}
