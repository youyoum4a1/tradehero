package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BaseBillingPurchaseFetcherHolder;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.Map;

/**
 * Created by xavier on 3/27/14.
 */
abstract public class BaseSamsungPurchaseFetcherHolder<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungPurchaseFetcherType extends SamsungPurchaseFetcher<
                SamsungSKUType,
                SamsungOrderIdType,
                SamsungPurchaseType,
                SamsungExceptionType>,
        SamsungExceptionType extends SamsungException>
    extends BaseBillingPurchaseFetcherHolder<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungExceptionType>
    implements SamsungPurchaseFetcherHolder<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungExceptionType>
{
    protected Map<Integer /*requestCode*/, SamsungPurchaseFetcherType> purchaseFetchers;
    
    public BaseSamsungPurchaseFetcherHolder()
    {
        super();
        purchaseFetchers = null;
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
                !purchaseFetchers.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        SamsungPurchaseFetcherType purchaseFetcher = purchaseFetchers.get(requestCode);
        if (purchaseFetcher != null)
        {
            purchaseFetcher.setPurchaseFetchedListener(null);
        }
        purchaseFetchers.remove(requestCode);
    }

    @Override public void launchFetchPurchaseSequence(int requestCode)
    {
        BillingPurchaseFetcher.OnPurchaseFetchedListener<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> purchaseFetchedListener = createPurchaseFetchedListener();
        SamsungPurchaseFetcherType purchaseFetcher = createPurchaseFetcher();
        purchaseFetcher.setPurchaseFetchedListener(purchaseFetchedListener);
        purchaseFetchers.put(requestCode, purchaseFetcher);
        purchaseFetcher.fetchPurchases(requestCode);
    }

    @Override public void onDestroy()
    {
        for (SamsungPurchaseFetcherType purchaseFetcher : purchaseFetchers.values())
        {
            if (purchaseFetcher != null)
            {
                purchaseFetcher.setPurchaseFetchedListener(null);
            }
        }
        purchaseFetchers.clear();
        super.onDestroy();
    }

    abstract protected SamsungPurchaseFetcherType createPurchaseFetcher();
}
