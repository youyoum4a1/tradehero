package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 4:45 PM To change this template use File | Settings | File Templates. */
public class THBaseIABPurchaseConsumer
        extends BaseIABPurchaseConsumer<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
    implements THIABPurchaseConsumer
{
    public static final String TAG = THBaseIABPurchaseConsumer.class.getSimpleName();

    @Inject protected THIABPurchaseCache thiabPurchaseCache;

    //<editor-fold desc="Constructors">
    public THBaseIABPurchaseConsumer()
    {
        super();
    }
    //</editor-fold>

    @Override protected IABPurchaseCache<IABSKU, THIABOrderId, THIABPurchase> getPurchaseCache()
    {
        return thiabPurchaseCache;
    }
}
