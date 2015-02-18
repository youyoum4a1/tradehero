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
import rx.functions.Func1;

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
    @NonNull protected final AmazonPurchasingService purchasingService;

    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaseFetcherRx(
            int request,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(request);
        this.purchasingService = purchasingService;
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseFetchResult<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType>> get()
    {
        return fetchIncompletePurchases(purchasingService)
                .flatMap(new Func1<AmazonPurchaseIncompleteType, Observable<? extends AmazonPurchaseType>>()
                {
                    @Override public Observable<? extends AmazonPurchaseType> call(AmazonPurchaseIncompleteType incomplete)
                    {
                        try
                        {
                            return Observable.just(BaseAmazonPurchaseFetcherRx.this.complete(incomplete));
                        } catch (Throwable e)
                        {
                            return Observable.error(e);
                        }
                    }
                })
                .map(new Func1<AmazonPurchaseType, PurchaseFetchResult<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType>>()
                {
                    @Override public PurchaseFetchResult<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType> call(AmazonPurchaseType purchase)
                    {
                        return BaseAmazonPurchaseFetcherRx.this.createResult(purchase);
                    }
                });
    }

    protected Observable<AmazonPurchaseIncompleteType> fetchIncompletePurchases(@NonNull AmazonPurchasingService purchasingService)
    {
        return Observable.create(new AmazonPurchasingServicePurchaseUpdatesOperator(purchasingService))
                .flatMap(new Func1<PurchaseUpdatesResponse, Observable<? extends AmazonPurchaseIncompleteType>>()
                {
                    @Override public Observable<? extends AmazonPurchaseIncompleteType> call(PurchaseUpdatesResponse response)
                    {
                        return BaseAmazonPurchaseFetcherRx.this.getIncompletePurchases(response);
                    }
                });
    }

    @NonNull protected Observable<AmazonPurchaseIncompleteType> getIncompletePurchases(@NonNull final PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
        return getReceipts(purchaseUpdatesResponse)
                .map(new Func1<Receipt, AmazonPurchaseIncompleteType>()
                {
                    @Override public AmazonPurchaseIncompleteType call(Receipt receipt)
                    {
                        return BaseAmazonPurchaseFetcherRx.this.createIncompletePurchase(receipt, purchaseUpdatesResponse.getUserData());
                    }
                });
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