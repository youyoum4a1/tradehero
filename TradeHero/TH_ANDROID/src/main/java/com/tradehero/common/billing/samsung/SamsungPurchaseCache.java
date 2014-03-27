package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.ProductPurchaseCache;

/**
 * Created by xavier on 2/11/14.
 */
public class SamsungPurchaseCache<
            SamsungSKUType extends SamsungSKU,
            SamsungOrderIdType extends SamsungOrderId,
            SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>>
        extends ProductPurchaseCache<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType>
{
    public static final String TAG = SamsungPurchaseCache.class.getSimpleName();

    public SamsungPurchaseCache(int maxSize)
    {
        super(maxSize);
    }

    @Override protected SamsungPurchaseType fetch(SamsungOrderIdType key) throws Throwable
    {
        throw new IllegalStateException("You cannot fetch on this cache");
    }
}
