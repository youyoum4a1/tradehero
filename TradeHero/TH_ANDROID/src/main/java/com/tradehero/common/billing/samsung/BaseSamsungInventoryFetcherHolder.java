package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BaseBillingInventoryFetcherHolder;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseSamsungInventoryFetcherHolder<
        SamsungSKUType extends SamsungSKU,
        SamsungProductDetailType extends SamsungProductDetail<SamsungSKUType>,
        SamsungInventoryFetcherType extends SamsungInventoryFetcher<
                SamsungSKUType,
                SamsungProductDetailType,
                SamsungExceptionType>,
        SamsungExceptionType extends SamsungException>
    extends BaseBillingInventoryFetcherHolder<
        SamsungSKUType,
        SamsungProductDetailType,
        SamsungExceptionType>
    implements SamsungInventoryFetcherHolder<
        SamsungSKUType,
        SamsungProductDetailType,
        SamsungExceptionType>
{
    protected Map<Integer /*requestCode*/, SamsungInventoryFetcherType> inventoryFetchers;

    public BaseSamsungInventoryFetcherHolder()
    {
        super();
        inventoryFetchers = new HashMap<>();
    }

    @Override public void launchInventoryFetchSequence(int requestCode, List<SamsungSKUType> allIds)
    {
        BillingInventoryFetcher.OnInventoryFetchedListener<SamsungSKUType, SamsungProductDetailType, SamsungExceptionType> skuFetchedListener = createInventoryFetchedListener();
        SamsungInventoryFetcherType inventoryFetcher = createProductIdentifierFetcher();
        inventoryFetcher.setInventoryFetchedListener(skuFetchedListener);
        inventoryFetchers.put(requestCode, inventoryFetcher);
        inventoryFetcher.fetchInventory(requestCode);
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
                !inventoryFetchers.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        SamsungInventoryFetcherType inventoryFetcher = inventoryFetchers.get(requestCode);
        if (inventoryFetcher != null)
        {
            inventoryFetcher.setInventoryFetchedListener(null);
        }
        inventoryFetchers.remove(requestCode);
    }

    @Override public void onDestroy()
    {
        for (SamsungInventoryFetcherType inventoryFetcher : inventoryFetchers.values())
        {
            if (inventoryFetcher != null)
            {
                inventoryFetcher.setInventoryFetchedListener(null);
            }
        }
        inventoryFetchers.clear();

        super.onDestroy();
    }

    abstract protected SamsungInventoryFetcherType createProductIdentifierFetcher();
}
