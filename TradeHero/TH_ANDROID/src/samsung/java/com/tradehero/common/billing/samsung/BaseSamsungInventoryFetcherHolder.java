package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BaseBillingInventoryFetcherHolder;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Provider;
import android.support.annotation.NonNull;
import timber.log.Timber;

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
    @NonNull protected final Provider<SamsungInventoryFetcherType> samsungInventoryFetcherTypeProvider;
    @NonNull protected final Map<Integer /*requestCode*/, SamsungInventoryFetcherType> inventoryFetchers;

    //<editor-fold desc="Constructors">
    public BaseSamsungInventoryFetcherHolder(
            @NonNull Provider<SamsungInventoryFetcherType> samsungInventoryFetcherTypeProvider)
    {
        super();
        this.samsungInventoryFetcherTypeProvider = samsungInventoryFetcherTypeProvider;
        inventoryFetchers = new HashMap<>();
    }
    //</editor-fold>

    @Override public void launchInventoryFetchSequence(int requestCode, List<SamsungSKUType> allIds)
    {
        Timber.d("Launching fetch sequence");
        BillingInventoryFetcher.OnInventoryFetchedListener<SamsungSKUType, SamsungProductDetailType, SamsungExceptionType> skuFetchedListener = createInventoryFetchedListener();
        SamsungInventoryFetcherType inventoryFetcher = samsungInventoryFetcherTypeProvider.get();
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
}
