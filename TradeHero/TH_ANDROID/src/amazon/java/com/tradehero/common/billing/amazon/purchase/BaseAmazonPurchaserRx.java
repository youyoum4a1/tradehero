package com.tradehero.common.billing.amazon.purchase;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.PurchaseResponse;
import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonPurchaseOrder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonPurchaseException;
import com.tradehero.common.billing.amazon.exception.AmazonPurchaseReceiptCancelledException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingServicePurchaseOperator;
import com.tradehero.common.billing.purchase.BaseBillingPurchaserRx;
import com.tradehero.common.billing.purchase.PurchaseResult;
import rx.Observable;

abstract public class BaseAmazonPurchaserRx<
        AmazonSKUType extends AmazonSKU,
        AmazonPurchaseOrderType extends AmazonPurchaseOrder<AmazonSKUType>,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
        extends BaseBillingPurchaserRx<
        AmazonSKUType,
        AmazonPurchaseOrderType,
        AmazonOrderIdType,
        AmazonPurchaseType>
        implements AmazonPurchaserRx<
        AmazonSKUType,
        AmazonPurchaseOrderType,
        AmazonOrderIdType,
        AmazonPurchaseType>
{
    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaserRx(
            int request,
            @NonNull AmazonPurchaseOrderType purchaseOrder,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(request, purchaseOrder);
        purchase(purchasingService);
    }
    //</editor-fold>

    protected void purchase(@NonNull AmazonPurchasingService purchasingService)
    {
        Observable.create(new AmazonPurchasingServicePurchaseOperator(
                purchasingService,
                getPurchaseOrder().getProductIdentifier().skuId))
                .flatMap(this::createResultObservable)
                .subscribe(subject);
    }

    @NonNull protected Observable<PurchaseResult<AmazonSKUType, AmazonPurchaseOrderType, AmazonOrderIdType, AmazonPurchaseType>>
    createResultObservable(@NonNull PurchaseResponse purchaseResponse)
    {
        switch (purchaseResponse.getRequestStatus())
        {
            case SUCCESSFUL:
                if (purchaseResponse.getReceipt().isCanceled())
                {
                    return Observable.error(new AmazonPurchaseReceiptCancelledException(
                            "Receipt cancelled for " + getPurchaseOrder(),
                            getPurchaseOrder().getProductIdentifier().skuId,
                            purchaseResponse));
                }
                else
                {
                    return Observable.just(new PurchaseResult<>(
                            getRequestCode(),
                            getPurchaseOrder(),
                            createPurchase(purchaseResponse)));
                }
            case FAILED:
            case NOT_SUPPORTED:
            case ALREADY_PURCHASED:
            case INVALID_SKU:
                return Observable.error(new AmazonPurchaseException(
                        "Purchase status is " + purchaseResponse.getRequestStatus(),
                        getPurchaseOrder().getProductIdentifier().skuId,
                        purchaseResponse));

            default:
                return Observable.error(new IllegalStateException(
                        "Unhandled PurchaseResponse.RequestStatus."
                                + purchaseResponse.getRequestStatus()));
        }
    }

    @NonNull abstract protected AmazonPurchaseType createPurchase(@NonNull PurchaseResponse purchaseResponse);
}
