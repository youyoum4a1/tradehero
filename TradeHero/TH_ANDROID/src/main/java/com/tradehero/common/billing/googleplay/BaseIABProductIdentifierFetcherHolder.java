package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BaseProductIdentifierFetcherHolder;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.HashMap;
import java.util.Map;


abstract public class BaseIABProductIdentifierFetcherHolder<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseIABSKUList<IABSKUType>,
        IABProductIdentifierFetcherType extends IABProductIdentifierFetcher<
                IABSKUListKeyType,
                IABSKUType,
                IABSKUListType,
                IABExceptionType>,
        IABExceptionType extends IABException>
    extends BaseProductIdentifierFetcherHolder<
        IABSKUListKeyType,
        IABSKUType,
        IABSKUListType,
        IABExceptionType>
    implements IABProductIdentifierFetcherHolder<
        IABSKUListKeyType,
        IABSKUType,
        IABSKUListType,
        IABExceptionType>
{
    protected Map<Integer /*requestCode*/, IABProductIdentifierFetcherType> skuFetchers;

    public BaseIABProductIdentifierFetcherHolder()
    {
        super();
        skuFetchers = new HashMap<>();
    }

    @Override public void launchProductIdentifierFetchSequence(int requestCode)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKUListKeyType, IABSKUType, IABSKUListType, IABExceptionType> skuFetchedListener = createProductIdentifierFetchedListener();
        IABProductIdentifierFetcherType skuFetcher = createProductIdentifierFetcher();
        skuFetcher.setProductIdentifierListener(skuFetchedListener);
        skuFetchers.put(requestCode, skuFetcher);
        skuFetcher.fetchProductIdentifiers(requestCode);
    }

    @Override public void onDestroy()
    {
        for (IABProductIdentifierFetcherType inventoryFetcher : skuFetchers.values())
        {
            if (inventoryFetcher != null)
            {
                inventoryFetcher.setProductIdentifierListener(null);
            }
        }
        skuFetchers.clear();

        super.onDestroy();
    }

    abstract protected IABProductIdentifierFetcherType createProductIdentifierFetcher();
}
