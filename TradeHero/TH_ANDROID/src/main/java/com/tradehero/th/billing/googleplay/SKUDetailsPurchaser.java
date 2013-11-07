package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.IABPurchaser;

/** Created with IntelliJ IDEA. User: xavier Date: 11/7/13 Time: 12:35 PM To change this template use File | Settings | File Templates. */
public class SKUDetailsPurchaser extends IABPurchaser<THSKUDetails>
{
    public static final String TAG = SKUDetailsPurchaser.class.getSimpleName();

    public SKUDetailsPurchaser(Activity activity)
    {
        super(activity);
    }
}
