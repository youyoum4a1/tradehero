package com.tradehero.common.billing.amazon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserData;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

abstract public class BaseAmazonPurchaseFetcher<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonPurchaseIncompleteType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonExceptionType extends AmazonException>
    extends BaseAmazonActor
    implements AmazonPurchaseFetcher<
            AmazonSKUType,
            AmazonOrderIdType,
            AmazonPurchaseType,
            AmazonExceptionType>
{
    protected boolean fetching;
    @NonNull protected final List<AmazonPurchaseIncompleteType> fetchedIncompletePurchases;
    @NonNull protected final List<AmazonPurchaseIncompleteType> fetchedCanceledPurchases;
    @NonNull protected final List<AmazonPurchaseType> purchases;
    @Nullable protected OnPurchaseFetchedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> fetchListener;

    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaseFetcher(
            int request,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(request, purchasingService);
        fetchedIncompletePurchases = new ArrayList<>();
        fetchedCanceledPurchases = new ArrayList<>();
        purchases = new ArrayList<>();
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        setPurchaseFetchedListener(null);
        super.onDestroy();
    }

    @Override public void fetchPurchases()
    {
        checkNotFetching();
        this.fetching = true;
        purchasingService.getPurchaseUpdates(true, this);
    }

    protected void checkNotFetching()
    {
        if (fetching)
        {
            throw new IllegalStateException("Already fetching");
        }
    }

    @Override public void onPurchaseUpdatesResponse(@NonNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
        super.onPurchaseUpdatesResponse(purchaseUpdatesResponse);
        switch (purchaseUpdatesResponse.getRequestStatus())
        {
            case SUCCESSFUL:
                handleReceived(purchaseUpdatesResponse.getReceipts(), purchaseUpdatesResponse.getUserData());
                notifyListenerFetched();
                break;
            case FAILED:
            case NOT_SUPPORTED:
                notifyListenerFetchFailed(createException(purchaseUpdatesResponse.getRequestStatus()));
                break;
        }
    }

    protected void handleReceived(@NonNull List<Receipt> receipts, @NonNull UserData userData)
    {
        for (Receipt receipt : receipts)
        {
            if (receipt.isCanceled())
            {
                fetchedCanceledPurchases.add(createIncompletePurchase(receipt, userData));
                // TODO do something with those?
            }
            else
            {
                fetchedIncompletePurchases.add(createIncompletePurchase(receipt, userData));
            }
        }
    }

    @NonNull protected abstract AmazonPurchaseIncompleteType createIncompletePurchase(@NonNull Receipt receipt, @NonNull UserData userData);

    abstract protected AmazonExceptionType createException(@NonNull PurchaseUpdatesResponse.RequestStatus requestStatus);

    @Override @Nullable public OnPurchaseFetchedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> getFetchListener()
    {
        return fetchListener;
    }

    @Override public void setPurchaseFetchedListener(@Nullable OnPurchaseFetchedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> fetchListener)
    {
        this.fetchListener = fetchListener;
    }

    protected void notifyListenerFetched()
    {
        OnPurchaseFetchedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> listenerCopy = getFetchListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchedPurchases(getRequestCode(), this.purchases);
        }
    }

    protected void notifyListenerFetchFailed(AmazonExceptionType exception)
    {
        Timber.e(exception, "");
        OnPurchaseFetchedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> listenerCopy = getFetchListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchPurchasesFailed(getRequestCode(), exception);
        }
    }
}
