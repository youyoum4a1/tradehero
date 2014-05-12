package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BaseProductIdentifierFetcherHolder;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.HashMap;
import java.util.Map;


abstract public class BaseSamsungProductIdentifierFetcherHolder<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>,
        SamsungProductIdentifierFetcherType extends SamsungProductIdentifierFetcher<
                SamsungSKUListKeyType,
                SamsungSKUType,
                SamsungSKUListType,
                SamsungExceptionType>,
        SamsungExceptionType extends SamsungException>
    extends BaseProductIdentifierFetcherHolder<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType,
        SamsungExceptionType>
    implements SamsungProductIdentifierFetcherHolder<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType,
        SamsungExceptionType>
{
    protected Map<Integer /*requestCode*/, SamsungProductIdentifierFetcherType> skuFetchers;

    public BaseSamsungProductIdentifierFetcherHolder()
    {
        super();
        skuFetchers = new HashMap<>();
    }

    @Override public void launchProductIdentifierFetchSequence(int requestCode)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<SamsungSKUListKeyType, SamsungSKUType, SamsungSKUListType, SamsungExceptionType> skuFetchedListener = createProductIdentifierFetchedListener();
        SamsungProductIdentifierFetcherType skuFetcher = createProductIdentifierFetcher();
        skuFetcher.setProductIdentifierListener(skuFetchedListener);
        skuFetchers.put(requestCode, skuFetcher);
        skuFetcher.fetchProductIdentifiers(requestCode);
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
                !skuFetchers.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        SamsungProductIdentifierFetcherType inventoryFetcher = skuFetchers.get(requestCode);
        if (inventoryFetcher != null)
        {
            inventoryFetcher.setProductIdentifierListener(null);
        }
        skuFetchers.remove(requestCode);
    }

    @Override public void onDestroy()
    {
        for (SamsungProductIdentifierFetcherType inventoryFetcher : skuFetchers.values())
        {
            if (inventoryFetcher != null)
            {
                inventoryFetcher.setProductIdentifierListener(null);
            }
        }
        skuFetchers.clear();

        super.onDestroy();
    }

    abstract protected SamsungProductIdentifierFetcherType createProductIdentifierFetcher();
}
