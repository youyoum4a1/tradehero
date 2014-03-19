package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BaseProductIdentifierFetcherHolder;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseIABProductIdentifierFetcherHolder<
        IABSKUType extends IABSKU,
        ProductIdentifierFetcherType extends ProductIdentifierFetcher<
                IABSKUType,
                IABExceptionType>,
        IABExceptionType extends IABException>
    extends BaseProductIdentifierFetcherHolder<
        IABSKUType,
        IABExceptionType>
{
    protected Map<Integer /*requestCode*/, ProductIdentifierFetcherType> skuFetchers;

    public BaseIABProductIdentifierFetcherHolder()
    {
        super();
        skuFetchers = new HashMap<>();
    }

    @Override public void launchProductIdentifierFetchSequence(int requestCode)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKUType, IABExceptionType> skuFetchedListener =
                new ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKUType, IABExceptionType>()
                {
                    @Override public void onFetchedProductIdentifiers(int requestCode,
                            Map<String, List<IABSKUType>> availableSkus)
                    {
                        notifyProductIdentifierFetchedSuccess(requestCode, availableSkus);
                    }

                    @Override public void onFetchProductIdentifiersFailed(int requestCode,
                            IABExceptionType exception)
                    {
                        notifyProductIdentifierFetchedFailed(requestCode, exception);
                    }
                };
        productIdentifierFetchedListeners.put(requestCode, skuFetchedListener);
        ProductIdentifierFetcherType skuFetcher = createProductIdentifierFetcher();
        skuFetcher.setProductIdentifierListener(skuFetchedListener);
        skuFetchers.put(requestCode, skuFetcher);
        skuFetcher.fetchProductIdentifiers(requestCode);
    }

    @Override public void onDestroy()
    {
        for (ProductIdentifierFetcherType inventoryFetcher : skuFetchers.values())
        {
            if (inventoryFetcher != null)
            {
                inventoryFetcher.setProductIdentifierListener(null);
            }
        }
        skuFetchers.clear();

        super.onDestroy();
    }

    abstract protected ProductIdentifierFetcherType createProductIdentifierFetcher();
}
