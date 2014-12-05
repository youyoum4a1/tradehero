package com.tradehero.common.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseProductIdentifierFetcherHolder;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import java.util.HashMap;
import java.util.Map;

abstract public class BaseAmazonProductIdentifierFetcherHolder<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>,
        AmazonProductIdentifierFetcherType extends AmazonProductIdentifierFetcher<
                AmazonSKUListKeyType,
                AmazonSKUType,
                AmazonSKUListType,
                AmazonExceptionType>,
        AmazonExceptionType extends AmazonException>
    extends BaseProductIdentifierFetcherHolder<
        AmazonSKUListKeyType,
        AmazonSKUType,
        AmazonSKUListType,
        AmazonExceptionType>
    implements AmazonProductIdentifierFetcherHolder<
            AmazonSKUListKeyType,
            AmazonSKUType,
            AmazonSKUListType,
            AmazonExceptionType>
{
    @NonNull protected final Map<Integer /*requestCode*/, AmazonProductIdentifierFetcherType> skuFetchers;

    //<editor-fold desc="Constructors">
    public BaseAmazonProductIdentifierFetcherHolder()
    {
        super();
        skuFetchers = new HashMap<>();
    }
    //</editor-fold>

    @Override public void launchProductIdentifierFetchSequence(int requestCode)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<AmazonSKUListKeyType, AmazonSKUType, AmazonSKUListType, AmazonExceptionType> skuFetchedListener = createProductIdentifierFetchedListener();
        AmazonProductIdentifierFetcherType skuFetcher = createSkuFetcher(requestCode);
        skuFetcher.setProductIdentifierListener(skuFetchedListener);
        skuFetchers.put(requestCode, skuFetcher);
        skuFetcher.fetchProductIdentifiers();
    }

    @NonNull protected abstract AmazonProductIdentifierFetcherType createSkuFetcher(int requestCode);

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
                !skuFetchers.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        AmazonProductIdentifierFetcherType inventoryFetcher = skuFetchers.get(requestCode);
        if (inventoryFetcher != null)
        {
            inventoryFetcher.setProductIdentifierListener(null);
        }
        skuFetchers.remove(requestCode);
    }

    @Override public void onDestroy()
    {
        for (AmazonProductIdentifierFetcherType inventoryFetcher : skuFetchers.values())
        {
            if (inventoryFetcher != null)
            {
                inventoryFetcher.onDestroy();
            }
        }
        skuFetchers.clear();

        super.onDestroy();
    }
}
