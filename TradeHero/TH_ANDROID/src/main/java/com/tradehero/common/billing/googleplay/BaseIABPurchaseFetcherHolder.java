package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BaseBillingPurchaseFetcherHolder;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseIABPurchaseFetcherHolder<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABPurchaseFetcherType extends IABPurchaseFetcher<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType>,
        IABPurchaseFetchedListenerType extends BillingPurchaseFetcher.OnPurchaseFetchedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>>
    extends BaseBillingPurchaseFetcherHolder<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType,
        IABPurchaseFetchedListenerType,
        IABException>
{
    protected Map<Integer /*requestCode*/, IABPurchaseFetcherType> purchaseFetchers;

    public BaseIABPurchaseFetcherHolder()
    {
        super();
        purchaseFetchers = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
            !purchaseFetchers.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        IABPurchaseFetcherType purchaseFetcher = purchaseFetchers.get(requestCode);
        if (purchaseFetcher != null)
        {
            purchaseFetcher.setListener(null);
            purchaseFetcher.setPurchaseFetchedListener(null);
        }
        purchaseFetchers.remove(requestCode);
    }

    @Override public void launchFetchPurchaseSequence(int requestCode)
    {
        BillingPurchaseFetcher.OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> purchaseFetchedListener = new BillingPurchaseFetcher.OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException>()
        {
            @Override public void onFetchPurchasesFailed(int requestCode, IABException exception)
            {
                notifyPurchaseFetchedFailed(requestCode, exception);
            }

            @Override public void onFetchedPurchases(int requestCode, Map<IABSKUType, IABPurchaseType> purchases)
            {
                notifyPurchaseFetchedSuccess(requestCode, purchases);
            }
        };
        purchaseFetchedListeners.put(requestCode, purchaseFetchedListener);
        IABPurchaseFetcherType purchaseFetcher = createPurchaseFetcher();
        purchaseFetcher.setPurchaseFetchedListener(purchaseFetchedListener);
        purchaseFetchers.put(requestCode, purchaseFetcher);
        purchaseFetcher.fetchPurchases(requestCode);
    }

    @Override public void onDestroy()
    {
        for (IABPurchaseFetcherType purchaseFetcher : purchaseFetchers.values())
        {
            if (purchaseFetcher != null)
            {
                purchaseFetcher.setListener(null);
                purchaseFetcher.setPurchaseFetchedListener(null);
                purchaseFetcher.onDestroy();
            }
        }
        purchaseFetchers.clear();
        super.onDestroy();
    }

    abstract protected IABPurchaseFetcherType createPurchaseFetcher();
}
