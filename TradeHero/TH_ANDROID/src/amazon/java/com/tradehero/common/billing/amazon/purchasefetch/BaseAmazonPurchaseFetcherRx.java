package com.tradehero.common.billing.amazon.purchasefetch;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserData;
import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonPurchaseUpdateFailedException;
import com.tradehero.common.billing.amazon.exception.AmazonPurchaseUpdateUnsupportedException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingServicePurchaseUpdatesOperator;
import com.tradehero.common.billing.purchasefetch.BaseBillingPurchaseFetcherRx;
import com.tradehero.common.billing.purchasefetch.PurchaseFetchResult;
import rx.Observable;

abstract public class BaseAmazonPurchaseFetcherRx<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonPurchaseIncompleteType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
        extends BaseBillingPurchaseFetcherRx<AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType>
        implements AmazonPurchaseFetcherRx<
        AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType>
{
    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaseFetcherRx(
            int request,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(request);
        fetchPurchases(purchasingService);
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
    }

    protected void fetchPurchases(@NonNull AmazonPurchasingService purchasingService)
    {
        fetchIncompletePurchases(purchasingService)
                .flatMap(incomplete -> {
                    try
                    {
                        return Observable.just(complete(incomplete));
                    } catch (Throwable e)
                    {
                        return Observable.error(e);
                    }
                })
                .map(this::createResult)
                .subscribe(subject);
    }

    protected Observable<AmazonPurchaseIncompleteType> fetchIncompletePurchases(@NonNull AmazonPurchasingService purchasingService)
    {
        return Observable.create(new AmazonPurchasingServicePurchaseUpdatesOperator(purchasingService))
                .flatMap(this::getIncompletePurchases);
    }

    @NonNull protected Observable<AmazonPurchaseIncompleteType> getIncompletePurchases(@NonNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
        return getReceipts(purchaseUpdatesResponse)
                .map(receipt -> createIncompletePurchase(receipt, purchaseUpdatesResponse.getUserData()));
    }

    @NonNull public Observable<Receipt> getReceipts(@NonNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
        switch (purchaseUpdatesResponse.getRequestStatus())
        {
            case NOT_SUPPORTED:
                return Observable.error(new AmazonPurchaseUpdateUnsupportedException("Fetch purchases unsupported", purchaseUpdatesResponse));

            case FAILED:
                return Observable.error(new AmazonPurchaseUpdateFailedException("Failed to fetch purchases", purchaseUpdatesResponse));

            case SUCCESSFUL:
                return Observable.from(purchaseUpdatesResponse.getReceipts());

            default:
                return Observable.error(
                        new IllegalStateException("Unhandled PurchaseUpdatesResponse.RequestStatus." + purchaseUpdatesResponse.getRequestStatus()));
        }
    }

    @NonNull protected abstract AmazonPurchaseIncompleteType createIncompletePurchase(@NonNull Receipt receipt, @NonNull UserData userData);

    @NonNull protected abstract AmazonPurchaseType complete(@NonNull AmazonPurchaseIncompleteType incompleteFetchedPurchase);

    @NonNull protected PurchaseFetchResult<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType> createResult(
            @NonNull AmazonPurchaseType purchase)
    {
        return new PurchaseFetchResult<>(getRequestCode(), purchase);
    }
}
