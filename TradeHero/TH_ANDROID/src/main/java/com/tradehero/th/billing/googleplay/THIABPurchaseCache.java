package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaseCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 2/11/14.
 */
@Singleton public class THIABPurchaseCache extends IABPurchaseCache<
            IABSKU,
            THIABOrderId,
            THIABPurchase>
{
    public static final String TAG = THIABPurchaseCache.class.getSimpleName();

    public static final int DEFAULT_MAX_SIZE = 200;

    @Inject public THIABPurchaseCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
}
