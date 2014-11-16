package com.tradehero.common.billing.amazon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.amazon.device.iap.model.PurchaseResponse;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import timber.log.Timber;

abstract public class BaseAmazonPurchaser<
        AmazonSKUType extends AmazonSKU,
        AmazonPurchaseOrderType extends AmazonPurchaseOrder<AmazonSKUType>,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonExceptionType extends AmazonException>
    extends BaseAmazonActor
    implements AmazonPurchaser<
            AmazonSKUType,
            AmazonPurchaseOrderType,
            AmazonOrderIdType,
            AmazonPurchaseType,
            AmazonExceptionType>
{
    protected AmazonPurchaseOrderType purchaseOrder;
    @Nullable private OnPurchaseFinishedListener<AmazonSKUType, AmazonPurchaseOrderType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> purchaseFinishedListener;

    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaser(
            int request,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(request, purchasingService);
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        setPurchaseFinishedListener(null);
        super.onDestroy();
    }

    @Override @Nullable public OnPurchaseFinishedListener<AmazonSKUType, AmazonPurchaseOrderType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> getPurchaseFinishedListener()
    {
        return this.purchaseFinishedListener;
    }

    @Override public void setPurchaseFinishedListener(@Nullable OnPurchaseFinishedListener<AmazonSKUType, AmazonPurchaseOrderType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> purchaseFinishedListener)
    {
        this.purchaseFinishedListener = purchaseFinishedListener;
    }

    @Override public void purchase(@NonNull AmazonPurchaseOrderType purchaseOrder)
    {
        this.purchaseOrder = purchaseOrder;
        purchasingService.purchase(purchaseOrder.sku.skuId, this);
    }

    @Override public void onPurchaseResponse(@NonNull PurchaseResponse purchaseResponse)
    {
        super.onPurchaseResponse(purchaseResponse);
        switch (purchaseResponse.getRequestStatus())
        {
            case SUCCESSFUL:
                if (purchaseResponse.getReceipt().isCanceled())
                {
                    notifyPurchaseFailed(createAmazonCanceledException());
                }
                else
                {
                    AmazonPurchaseType purchase = createAmazonPurchase(purchaseResponse);
                    handlePurchaseFinished(purchase);
                    notifyPurchaseFinished(purchase);
                }
                break;
            case FAILED:
            case NOT_SUPPORTED:
            case ALREADY_PURCHASED:
            case INVALID_SKU:
                notifyPurchaseFailed(createAmazonException(purchaseResponse.getRequestStatus()));
                break;
        }
    }

    abstract protected AmazonPurchaseType createAmazonPurchase(PurchaseResponse purchaseResponse);
    abstract protected AmazonExceptionType createAmazonException(PurchaseResponse.RequestStatus status);
    abstract protected AmazonExceptionType createAmazonCanceledException();

    protected void handlePurchaseFinished(AmazonPurchaseType purchase)
    {
        // Nothing to do here
    }

    protected void notifyPurchaseFinished(AmazonPurchaseType purchase)
    {
        OnPurchaseFinishedListener<AmazonSKUType, AmazonPurchaseOrderType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> listenerCopy = purchaseFinishedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onPurchaseFinished(getRequestCode(), purchaseOrder, purchase);
        }
    }

    protected void notifyPurchaseFailed(AmazonExceptionType exception)
    {
        Timber.e(exception, "");
        OnPurchaseFinishedListener<AmazonSKUType, AmazonPurchaseOrderType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> listenerCopy = purchaseFinishedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onPurchaseFailed(getRequestCode(), purchaseOrder, exception);
        }
    }
}
