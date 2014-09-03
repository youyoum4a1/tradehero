package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.BaseBillingInventoryFetcherHolder;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

abstract public class BaseAmazonInventoryFetcherHolder<
        AmazonSKUType extends AmazonSKU,
        AmazonProductDetailType extends AmazonProductDetail<AmazonSKUType>,
        AmazonInventoryFetcherType extends AmazonInventoryFetcher<
                AmazonSKUType,
                AmazonProductDetailType,
                AmazonExceptionType>,
        AmazonExceptionType extends AmazonException>
    extends BaseBillingInventoryFetcherHolder<
        AmazonSKUType,
        AmazonProductDetailType,
        AmazonExceptionType>
    implements AmazonInventoryFetcherHolder<
            AmazonSKUType,
            AmazonProductDetailType,
            AmazonExceptionType>
{
    @NotNull protected final Provider<AmazonInventoryFetcherType> amazonInventoryFetcherTypeProvider;
    @NotNull protected final Map<Integer /*requestCode*/, AmazonInventoryFetcherType> inventoryFetchers;

    //<editor-fold desc="Constructors">
    public BaseAmazonInventoryFetcherHolder(
            @NotNull Provider<AmazonInventoryFetcherType> amazonInventoryFetcherTypeProvider)
    {
        super();
        this.amazonInventoryFetcherTypeProvider = amazonInventoryFetcherTypeProvider;
        inventoryFetchers = new HashMap<>();
    }
    //</editor-fold>

    @Override public void launchInventoryFetchSequence(int requestCode, List<AmazonSKUType> allIds)
    {
        Timber.d("Launching fetch sequence");
        BillingInventoryFetcher.OnInventoryFetchedListener<AmazonSKUType, AmazonProductDetailType, AmazonExceptionType> skuFetchedListener = createInventoryFetchedListener();
        AmazonInventoryFetcherType inventoryFetcher = amazonInventoryFetcherTypeProvider.get();
        inventoryFetcher.setInventoryFetchedListener(skuFetchedListener);
        inventoryFetcher.setProductIdentifiers(allIds);
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
        AmazonInventoryFetcherType inventoryFetcher = inventoryFetchers.get(requestCode);
        if (inventoryFetcher != null)
        {
            inventoryFetcher.setInventoryFetchedListener(null);
        }
        inventoryFetchers.remove(requestCode);
    }

    @Override public void onDestroy()
    {
        for (AmazonInventoryFetcherType inventoryFetcher : inventoryFetchers.values())
        {
            if (inventoryFetcher != null)
            {
                inventoryFetcher.onDestroy();
            }
        }
        inventoryFetchers.clear();

        super.onDestroy();
    }
}
