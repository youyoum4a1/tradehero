package com.tradehero.common.billing.amazon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.ProductType;
import com.tradehero.common.billing.amazon.exception.AmazonException;

abstract public class BaseAmazonPurchaseConsumer<
            AmazonSKUType extends AmazonSKU,
            AmazonOrderIdType extends AmazonOrderId,
            AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
        extends BaseAmazonActor
    implements AmazonPurchaseConsumer<
        AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType,
        AmazonException>
{
    private int requestCode;
    private boolean consuming = false;
    protected AmazonPurchaseType purchase;
    @Nullable private OnAmazonConsumptionFinishedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonException> consumptionFinishedListener;

    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaseConsumer(
            @NonNull Context appContext,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(appContext, purchasingService);
    }
    //</editor-fold>

    @Override public int getRequestCode()
    {
        return requestCode;
    }

    @Override public void onDestroy()
    {
        setConsumptionFinishedListener(null);
        super.onDestroy();
    }

    @NonNull abstract protected AmazonPurchaseCacheRx<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType> getPurchaseCache();

    public boolean isConsuming()
    {
        return consuming;
    }

    private void checkNotConsuming()
    {
        if (consuming)
        {
            throw new IllegalStateException("BaseAmazonPurchaseConsumer is already consuming so it cannot be launched again");
        }
    }

    @Override @Nullable public OnAmazonConsumptionFinishedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonException> getConsumptionFinishedListener()
    {
        return consumptionFinishedListener;
    }

    @Override public void setConsumptionFinishedListener(@Nullable OnAmazonConsumptionFinishedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonException> consumptionFinishedListener)
    {
        this.consumptionFinishedListener = consumptionFinishedListener;
    }

    @Override public void consume(int requestCode, AmazonPurchaseType purchase)
    {
        checkNotConsuming();
        this.requestCode = requestCode;

        if (purchase == null)
        {
            throw new IllegalArgumentException("Purchase cannot be null");
        }

        if (!purchase.getOrderId().receipt.getProductType().equals(ProductType.CONSUMABLE))
        {
            handleConsumeSkippedInternal(purchase);
        }
        else
        {
            this.purchase = purchase;
            consuming = true;
            consumeEffectively();
        }
    }

    private void handleConsumeFinishedInternal(AmazonPurchaseType purchase)
    {
        consuming = false;
        getPurchaseCache().invalidate(purchase.getOrderId());
        // TODO invalidate incomplete purchases pref?
        notifyListenerConsumeFinished(purchase);
    }

    private void handleConsumeSkippedInternal(AmazonPurchaseType purchase)
    {
        consuming = false;
        notifyListenerConsumeFinished(purchase);
    }

    private void notifyListenerConsumeFinished(AmazonPurchaseType purchase)
    {
        OnAmazonConsumptionFinishedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonException> listener = getConsumptionFinishedListener();
        if (listener != null)
        {
            listener.onPurchaseConsumed(requestCode, purchase);
        }
    }

    private void consumeEffectively()
    {
        purchasingService.notifyFulfillment(purchase.getOrderId().receipt.getReceiptId(), FulfillmentResult.FULFILLED);
        handleConsumeFinishedInternal(purchase);
    }
}
