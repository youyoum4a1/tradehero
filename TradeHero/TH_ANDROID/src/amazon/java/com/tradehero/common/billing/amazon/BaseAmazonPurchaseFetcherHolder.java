package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.BaseBillingPurchaseFetcherHolder;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

abstract public class BaseAmazonPurchaseFetcherHolder<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonPurchaseFetcherType extends AmazonPurchaseFetcher<
                        AmazonSKUType,
                        AmazonOrderIdType,
                        AmazonPurchaseType,
                        AmazonExceptionType>,
        AmazonExceptionType extends AmazonException>
    extends BaseBillingPurchaseFetcherHolder<
        AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType,
        AmazonExceptionType>
    implements AmazonPurchaseFetcherHolder<
            AmazonSKUType,
            AmazonOrderIdType,
            AmazonPurchaseType,
            AmazonExceptionType>
{
    @NotNull protected final Provider<AmazonPurchaseFetcherType> amazonPurchaseFetcherTypeProvider;
    @NotNull protected final Map<Integer /*requestCode*/, AmazonPurchaseFetcherType> purchaseFetchers;

    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaseFetcherHolder(
            @NotNull Provider<AmazonPurchaseFetcherType> amazonPurchaseFetcherTypeProvider)
    {
        super();
        this.amazonPurchaseFetcherTypeProvider = amazonPurchaseFetcherTypeProvider;
        purchaseFetchers = new HashMap<>();
    }
    //</editor-fold>

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
                !purchaseFetchers.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        AmazonPurchaseFetcherType purchaseFetcher = purchaseFetchers.get(requestCode);
        if (purchaseFetcher != null)
        {
            purchaseFetcher.setPurchaseFetchedListener(null);
        }
        purchaseFetchers.remove(requestCode);
    }

    @Override public void launchFetchPurchaseSequence(int requestCode)
    {
        BillingPurchaseFetcher.OnPurchaseFetchedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> purchaseFetchedListener = createPurchaseFetchedListener();
        AmazonPurchaseFetcherType purchaseFetcher = amazonPurchaseFetcherTypeProvider.get();
        purchaseFetcher.setPurchaseFetchedListener(purchaseFetchedListener);
        purchaseFetchers.put(requestCode, purchaseFetcher);
        purchaseFetcher.fetchPurchases(requestCode);
    }

    @Override public void onDestroy()
    {
        for (AmazonPurchaseFetcherType purchaseFetcher : purchaseFetchers.values())
        {
            if (purchaseFetcher != null)
            {
                purchaseFetcher.onDestroy();
            }
        }
        purchaseFetchers.clear();
        super.onDestroy();
    }
}
