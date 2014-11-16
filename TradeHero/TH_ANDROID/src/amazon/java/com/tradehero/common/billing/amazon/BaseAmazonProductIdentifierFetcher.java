package com.tradehero.common.billing.amazon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.amazon.device.iap.model.ProductType;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import timber.log.Timber;

abstract public class BaseAmazonProductIdentifierFetcher<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>,
        AmazonExceptionType extends AmazonException>
        extends BaseAmazonActor
        implements AmazonProductIdentifierFetcher<
                AmazonSKUListKeyType,
                AmazonSKUType,
                AmazonSKUListType,
                AmazonExceptionType>
{
    public static final int FIRST_ITEM_NUM = 1;

    protected boolean fetching;
    protected LinkedList<String> remainingGroupIds;
    protected String fetchingGroupId;
    protected Map<AmazonSKUListKeyType, AmazonSKUListType> amazonSKUs;
    @Nullable private OnProductIdentifierFetchedListener<AmazonSKUListKeyType, AmazonSKUType, AmazonSKUListType, AmazonExceptionType> fetchedListener;

    //<editor-fold desc="Constructors">
    public BaseAmazonProductIdentifierFetcher(
            int requestCode,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(requestCode, purchasingService);
        remainingGroupIds = new LinkedList<>();
        fetchingGroupId = null;
        amazonSKUs = new HashMap<>();
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        setProductIdentifierListener(null);
        super.onDestroy();
    }

    @Override @Nullable public OnProductIdentifierFetchedListener<AmazonSKUListKeyType, AmazonSKUType, AmazonSKUListType, AmazonExceptionType> getProductIdentifierListener()
    {
        return fetchedListener;
    }

    @Override public void setProductIdentifierListener(@Nullable OnProductIdentifierFetchedListener<AmazonSKUListKeyType, AmazonSKUType, AmazonSKUListType, AmazonExceptionType> listener)
    {
        this.fetchedListener = listener;
    }

    @Override public void fetchProductIdentifiers(int requestCode)
    {
        checkNotFetching();
        this.fetching = true;
        prepareKnownSkus();
    }

    protected void checkNotFetching()
    {
        if (fetching)
        {
            throw new IllegalStateException("Already fetching");
        }
    }

    protected void prepareKnownSkus()
    {
        for (ProductType productType : ProductType.values())
        {
            AmazonSKUListType list = createAmazonSKUList();
            populate(list, productType);
            amazonSKUs.put(createAmazonListKey(productType), list);
        }
        notifyListenerFetched();
    }

    abstract protected void populate(AmazonSKUListType list, ProductType productType);
    abstract protected AmazonSKUListKeyType createAmazonListKey(ProductType productType);
    abstract protected AmazonSKUListType createAmazonSKUList();

    protected void notifyListenerFetched()
    {
        OnProductIdentifierFetchedListener<AmazonSKUListKeyType, AmazonSKUType, AmazonSKUListType, AmazonExceptionType> listenerCopy = getProductIdentifierListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchedProductIdentifiers(getRequestCode(), this.amazonSKUs);
        }
    }

    protected void notifyListenerFetchFailed(AmazonExceptionType exception)
    {
        Timber.e(exception, "");
        OnProductIdentifierFetchedListener<AmazonSKUListKeyType, AmazonSKUType, AmazonSKUListType, AmazonExceptionType> listenerCopy = getProductIdentifierListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchProductIdentifiersFailed(getRequestCode(), exception);
        }
    }
}
