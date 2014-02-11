package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.IABPurchaseCache;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 4:45 PM To change this template use File | Settings | File Templates. */
public class THIABPurchaseConsumer extends IABPurchaseConsumer<IABSKU, THIABOrderId, THIABPurchase>
{
    public static final String TAG = THIABPurchaseConsumer.class.getSimpleName();

    @Inject protected THIABPurchaseCache thiabPurchaseCache;

    //<editor-fold desc="Constructors">
    public THIABPurchaseConsumer(Activity activity)
    {
        super(activity);
        DaggerUtils.inject(this);
    }
    //</editor-fold>

    @Override protected IABPurchaseCache<IABSKU, THIABOrderId, THIABPurchase> getPurchaseCache()
    {
        return thiabPurchaseCache;
    }
}
