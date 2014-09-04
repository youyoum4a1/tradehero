package com.tradehero.common.billing.amazon;

import android.content.Context;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.ProductType;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            @NotNull Context appContext,
            @NotNull AmazonPurchasingService purchasingService)
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

    @NotNull abstract protected AmazonPurchaseCache<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType> getPurchaseCache();

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

    private void notifyListenerConsumeFailed(AmazonException exception)
    {
        OnAmazonConsumptionFinishedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonException> listener = getConsumptionFinishedListener();
        if (listener != null)
        {
            listener.onPurchaseConsumeFailed(requestCode, purchase, exception);
        }
    }

    private void handleConsumeFinishedInternal(AmazonPurchaseType purchase)
    {
        consuming = false;
        getPurchaseCache().invalidate(purchase.getOrderId());
        // TODO invalidate incomplete purchases pref?
        handleConsumeFinished(purchase);
        notifyListenerConsumeFinished(purchase);
    }

    protected void handleConsumeFinished(AmazonPurchaseType purchase)
    {
        // Just for children classes
    }

    private void handleConsumeSkippedInternal(AmazonPurchaseType purchase)
    {
        consuming = false;
        handleConsumeSkipped(purchase);
        notifyListenerConsumeFinished(purchase);
    }

    protected void handleConsumeSkipped(AmazonPurchaseType purchase)
    {
        // Just for children classes
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
