package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BaseBillingInventoryFetcherHolder;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


abstract public class BaseIABInventoryFetcherHolder<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseIABSKUList<IABSKUType>,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABInventoryFetcherType extends IABBillingInventoryFetcher<
                IABSKUListKeyType,
                IABSKUType,
                IABSKUListType,
                IABProductDetailType>>
    extends BaseBillingInventoryFetcherHolder<
        IABSKUType,
        IABProductDetailType,
        IABException>
{
    protected Map<Integer /*requestCode*/, IABInventoryFetcherType> iabInventoryFetchers;
    protected boolean inventoryReady = false; // TODO this feels HACKy
    protected boolean errorLoadingInventory = false; // TODO here too
    protected Exception latestInventoryFetcherException; // TODO here too

    public BaseIABInventoryFetcherHolder()
    {
        super();
        iabInventoryFetchers = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int randomNumber)
    {
        return super.isUnusedRequestCode(randomNumber) &&
                !iabInventoryFetchers.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        IABInventoryFetcherType inventoryFetcher = iabInventoryFetchers.get(requestCode);
        if (inventoryFetcher != null)
        {
            inventoryFetcher.setListener(null);
            inventoryFetcher.setInventoryFetchedListener(null);
        }
        iabInventoryFetchers.remove(requestCode);
    }

    @Override public void launchInventoryFetchSequence(int requestCode, List<IABSKUType> allSkus)
    {
        latestInventoryFetcherException = null;
        BillingInventoryFetcher.OnInventoryFetchedListener<IABSKUType, IABProductDetailType, IABException>
                fetchedListener = createInventoryFetchedListener();

        IABInventoryFetcherType inventoryFetcher = createInventoryFetcher();
        iabInventoryFetchers.put(requestCode, inventoryFetcher);
        inventoryFetcher.setProductIdentifiers(allSkus);
        inventoryFetcher.setInventoryFetchedListener(fetchedListener);
        inventoryFetcher.fetchInventory(requestCode);
    }

    @Override public boolean hadErrorLoadingInventory()
    {
        return errorLoadingInventory;
    }

    @Override public boolean isInventoryReady()
    {
        return inventoryReady;
    }

    @Override public void onDestroy()
    {
        for (IABInventoryFetcherType inventoryFetcher : iabInventoryFetchers.values())
        {
            if (inventoryFetcher != null)
            {
                inventoryFetcher.onDestroy();
            }
        }
        iabInventoryFetchers.clear();

        super.onDestroy();
    }

    abstract protected IABInventoryFetcherType createInventoryFetcher();
}
