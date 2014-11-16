package com.tradehero.common.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseBillingInventoryFetcherHolder;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class BaseIABInventoryFetcherHolder<
        IABSKUType extends IABSKU,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABInventoryFetcherType extends IABInventoryFetcher<
                                IABSKUType,
                                IABProductDetailType,
                                IABException>>
    extends BaseBillingInventoryFetcherHolder<
        IABSKUType,
        IABProductDetailType,
        IABException>
    implements IABInventoryFetcherHolder<
        IABSKUType,
        IABProductDetailType,
        IABException>
{
    @NonNull protected Map<Integer /*requestCode*/, IABInventoryFetcherType> iabInventoryFetchers;
    protected Exception latestInventoryFetcherException; // TODO here too

    //<editor-fold desc="Constructors">
    public BaseIABInventoryFetcherHolder()
    {
        super();
        iabInventoryFetchers = new HashMap<>();
    }
    //</editor-fold>

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
        inventoryFetcher.fetchInventory();
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
