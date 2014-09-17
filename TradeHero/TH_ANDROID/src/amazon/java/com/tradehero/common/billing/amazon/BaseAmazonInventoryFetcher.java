package com.tradehero.common.billing.amazon;

import android.content.Context;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.ProductDataResponse;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

/**
 * Product Identifier Fetcher and Inventory Fetcher are essentially making the same calls.
 */
abstract public class BaseAmazonInventoryFetcher<
        AmazonSKUType extends AmazonSKU,
        AmazonProductDetailType extends AmazonProductDetail<AmazonSKUType>,
        AmazonExceptionType extends AmazonException>
    extends BaseAmazonActor
    implements AmazonInventoryFetcher<
            AmazonSKUType,
            AmazonProductDetailType,
            AmazonExceptionType>
{
    protected boolean fetching;
    @NotNull protected List<AmazonSKUType> productIdentifiers;
    @NotNull protected Map<AmazonSKUType, AmazonProductDetailType> inventory;
    @Nullable private OnInventoryFetchedListener<AmazonSKUType, AmazonProductDetailType, AmazonExceptionType> inventoryFetchedListener;

    //<editor-fold desc="Constructors">
    public BaseAmazonInventoryFetcher(
            @NotNull Context context,
            @NotNull AmazonPurchasingService purchasingService)
    {
        super(context, purchasingService);
        inventory = new HashMap<>();
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        setInventoryFetchedListener(null);
        super.onDestroy();
    }

    @Override @Nullable public OnInventoryFetchedListener<AmazonSKUType, AmazonProductDetailType, AmazonExceptionType> getInventoryFetchedListener()
    {
        return inventoryFetchedListener;
    }

    @Override public void setInventoryFetchedListener(@Nullable OnInventoryFetchedListener<AmazonSKUType, AmazonProductDetailType, AmazonExceptionType> onInventoryFetchedListener)
    {
        this.inventoryFetchedListener = onInventoryFetchedListener;
    }

    @Override @NotNull public List<AmazonSKUType> getProductIdentifiers()
    {
        return productIdentifiers;
    }

    @Override public void setProductIdentifiers(@NotNull List<AmazonSKUType> productIdentifiers)
    {
        this.productIdentifiers = productIdentifiers;
    }

    @Override public void fetchInventory(int requestCode)
    {
        Timber.d("Fetching inventory");
        checkNotFetching();
        this.fetching = true;
        setRequestCode(requestCode);
        prepareAndCallService();
    }

    protected void checkNotFetching()
    {
        if (fetching)
        {
            throw new IllegalStateException("Already fetching");
        }
    }

    protected void prepareAndCallService()
    {
        Set<String> skuIds = new HashSet<>();
        for (AmazonSKUType amazonSKU : productIdentifiers)
        {
            skuIds.add(amazonSKU.skuId);
        }
        purchasingService.getProductData(skuIds, this);
    }

    @Override public void onProductDataResponse(@NotNull ProductDataResponse productDataResponse)
    {
        super.onProductDataResponse(productDataResponse);
        switch(productDataResponse.getRequestStatus())
        {
            case SUCCESSFUL:
                populateProducts(productDataResponse.getProductData());
                notifyListenerFetched();
                break;
            case FAILED:
            case NOT_SUPPORTED:
                notifyListenerFetchFailed(createException(productDataResponse.getRequestStatus()));
                break;
        }
    }

    protected void populateProducts(Map<String, Product> productMap)
    {
        AmazonSKUType key;
        for (Map.Entry<String, Product> pair : productMap.entrySet())
        {
            key = createAmazonSku(pair.getKey());
            inventory.put(key, createAmazonProductDetail(key, pair.getValue()));
        }
    }

    abstract protected AmazonExceptionType createException(ProductDataResponse.RequestStatus requestStatus);

    abstract protected AmazonSKUType createAmazonSku(String skuId);
    abstract protected AmazonProductDetailType createAmazonProductDetail(AmazonSKUType amazonSKU, Product product);

    protected void notifyListenerFetched()
    {
        OnInventoryFetchedListener<AmazonSKUType, AmazonProductDetailType, AmazonExceptionType> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            Timber.d("Notify listener");
            listenerCopy.onInventoryFetchSuccess(
                    getRequestCode(),
                    new ArrayList<>(inventory.keySet()),
                    inventory);
        }
        else
        {
            Timber.d("Listener null");
        }
    }

    protected void notifyListenerFetchFailed(AmazonExceptionType exception)
    {
        Timber.e(exception, "");
        OnInventoryFetchedListener<AmazonSKUType, AmazonProductDetailType, AmazonExceptionType> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchFail(getRequestCode(), null, exception);
        }
    }
}
