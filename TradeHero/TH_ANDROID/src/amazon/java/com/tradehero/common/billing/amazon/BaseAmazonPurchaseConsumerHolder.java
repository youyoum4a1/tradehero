package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.amazon.exception.AmazonException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

abstract public class BaseAmazonPurchaseConsumerHolder<
        AmazonSKUType extends AmazonSKU,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonPurchaseConsumerType extends AmazonPurchaseConsumer<
                AmazonSKUType,
                AmazonOrderIdType,
                AmazonPurchaseType,
                AmazonException>>
        implements AmazonPurchaseConsumerHolder<
        AmazonSKUType,
        AmazonOrderIdType,
        AmazonPurchaseType,
        AmazonException>
{
    @NotNull protected Map<Integer /*requestCode*/, AmazonPurchaseConsumerType> amazonPurchaseConsumers;
    @NotNull protected Map<Integer /*requestCode*/, AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
            AmazonSKUType,
            AmazonOrderIdType,
            AmazonPurchaseType,
            AmazonException>> parentConsumeFinishedHandlers;

    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaseConsumerHolder()
    {
        super();
        amazonPurchaseConsumers = new HashMap<>();
        parentConsumeFinishedHandlers = new HashMap<>();
    }
    //</editor-fold>

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return !amazonPurchaseConsumers.containsKey(requestCode) &&
                !parentConsumeFinishedHandlers.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        parentConsumeFinishedHandlers.remove(requestCode);
        AmazonPurchaseConsumerType purchaseConsumer = amazonPurchaseConsumers.get(requestCode);
        if (purchaseConsumer != null)
        {
            purchaseConsumer.setConsumptionFinishedListener(null);
        }
        amazonPurchaseConsumers.remove(requestCode);
    }

    @Override @Nullable public AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
            AmazonSKUType,
            AmazonOrderIdType,
            AmazonPurchaseType,
            AmazonException> getConsumptionFinishedListener(int requestCode)
    {
        return parentConsumeFinishedHandlers.get(requestCode);
    }

    @Override public void registerConsumptionFinishedListener(
            int requestCode,
            @Nullable AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
                    AmazonSKUType,
                    AmazonOrderIdType,
                    AmazonPurchaseType,
                    AmazonException> purchaseConsumeHandler)
    {
        parentConsumeFinishedHandlers.put(requestCode, purchaseConsumeHandler);
    }

    @Override public void launchConsumeSequence(int requestCode, AmazonPurchaseType purchase)
    {
        AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonException> consumeListener = createConsumptionFinishedListener();
        AmazonPurchaseConsumerType iabPurchaseConsumer = createPurchaseConsumer();
        iabPurchaseConsumer.setConsumptionFinishedListener(consumeListener);
        amazonPurchaseConsumers.put(requestCode, iabPurchaseConsumer);
        iabPurchaseConsumer.consume(requestCode, purchase);
    }

    @NotNull protected AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonException>
    createConsumptionFinishedListener()
    {
        return new AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<AmazonSKUType, AmazonOrderIdType, AmazonPurchaseType, AmazonException>()
        {
            @Override public void onPurchaseConsumed(int requestCode, AmazonPurchaseType purchase)
            {
                notifyPurchaseConsumeSuccess(requestCode, purchase);
            }

            @Override public void onPurchaseConsumeFailed(int requestCode, AmazonPurchaseType purchase, AmazonException exception)
            {
                notifyPurchaseConsumeFail(requestCode, purchase, exception);
            }
        };
    }

    protected void notifyPurchaseConsumeSuccess(int requestCode, AmazonPurchaseType purchase)
    {
        Timber.d("notifyPurchaseConsumeSuccess Purchase info " + purchase);
        AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
                AmazonSKUType,
                AmazonOrderIdType,
                AmazonPurchaseType,
                AmazonException> handler = getConsumptionFinishedListener(requestCode);
        if (handler != null)
        {
            Timber.d("notifyPurchaseConsumeSuccess passing on the purchase for requestCode " + requestCode);
            handler.onPurchaseConsumed(requestCode, purchase);
        }
        else
        {
            Timber.d("notifyPurchaseConsumeSuccess No THAmazonPurchaseHandler for requestCode " + requestCode);
        }
    }

    protected void notifyPurchaseConsumeFail(int requestCode, AmazonPurchaseType purchase, AmazonException exception)
    {
        Timber.e("notifyPurchaseConsumeFail There was an exception during the consumption", exception);
        AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
                AmazonSKUType,
                AmazonOrderIdType,
                AmazonPurchaseType,
                AmazonException> handler = getConsumptionFinishedListener(requestCode);
        if (handler != null)
        {
            Timber.d("notifyPurchaseConsumeFail passing on the exception for requestCode " + requestCode);
            handler.onPurchaseConsumeFailed(requestCode, purchase, exception);
        }
        else
        {
            Timber.d("notifyPurchaseConsumeFail No THAmazonPurchaseHandler for requestCode " + requestCode);
        }
    }

    @Override public void onDestroy()
    {
        for (AmazonPurchaseConsumerType amazonPurchaseConsumer: amazonPurchaseConsumers.values())
        {
            if (amazonPurchaseConsumer != null)
            {
                amazonPurchaseConsumer.onDestroy();
            }
        }
        amazonPurchaseConsumers.clear();
        parentConsumeFinishedHandlers.clear();
    }

    abstract protected AmazonPurchaseConsumerType createPurchaseConsumer();
}
