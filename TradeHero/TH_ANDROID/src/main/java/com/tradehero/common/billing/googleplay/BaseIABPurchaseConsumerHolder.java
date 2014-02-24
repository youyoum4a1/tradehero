package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.googleplay.exception.IABException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseIABPurchaseConsumerHolder<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABPurchaseConsumerType extends IABPurchaseConsumer<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType>,
        IABConsumeFinishedListenerType extends IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>>
    implements IABPurchaseConsumerHolder<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType,
        IABConsumeFinishedListenerType,
        IABException>
{
    protected Map<Integer /*requestCode*/, IABPurchaseConsumerType> iabPurchaseConsumers;
    protected Map<Integer /*requestCode*/, IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException>>
            consumptionFinishedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<IABConsumeFinishedListenerType>> parentConsumeFinishedHandlers;

    public BaseIABPurchaseConsumerHolder()
    {
        super();
        iabPurchaseConsumers = new HashMap<>();
        consumptionFinishedListeners = new HashMap<>();
        parentConsumeFinishedHandlers = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return !iabPurchaseConsumers.containsKey(requestCode) &&
                !consumptionFinishedListeners.containsKey(requestCode) &&
                !parentConsumeFinishedHandlers.containsKey(requestCode);
    }

    @Override public IABConsumeFinishedListenerType getConsumeFinishedListener(int requestCode)
    {
        WeakReference<IABConsumeFinishedListenerType> weakHandler = parentConsumeFinishedHandlers.get(requestCode);
        if (weakHandler != null)
        {
            return weakHandler.get();
        }
        return null;
    }

    @Override public void registerConsumeFinishedListener(int requestCode, IABConsumeFinishedListenerType purchaseConsumeHandler)
    {
        parentConsumeFinishedHandlers.put(requestCode, new WeakReference<>(purchaseConsumeHandler));
    }

    @Override public void unregisterConsumeFinishedListener(int requestCode)
    {
        iabPurchaseConsumers.remove(requestCode);
        consumptionFinishedListeners.remove(requestCode);
        parentConsumeFinishedHandlers.remove(requestCode);
    }

    @Override public void launchConsumeSequence(int requestCode, IABPurchaseType purchase)
    {
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> consumeListener =  new IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException>()
        {
            @Override public void onPurchaseConsumed(int requestCode, IABPurchaseType purchase)
            {
                notifyPurchaseConsumeSuccess(requestCode, purchase);
            }

            @Override public void onPurchaseConsumeFailed(int requestCode, IABPurchaseType purchase, IABException exception)
            {
                notifyPurchaseConsumeFail(requestCode, purchase, exception);
            }
        };
        consumptionFinishedListeners.put(requestCode, consumeListener);
        IABPurchaseConsumerType iabPurchaseConsumer = createPurchaseConsumer();
        iabPurchaseConsumer.setConsumptionFinishedListener(consumeListener);
        iabPurchaseConsumers.put(requestCode, iabPurchaseConsumer);
        iabPurchaseConsumer.consume(requestCode, purchase);
    }
    protected void notifyPurchaseConsumeSuccess(int requestCode, IABPurchaseType purchase)
    {
        Timber.d("notifyPurchaseConsumeSuccess Purchase info " + purchase);
        IABConsumeFinishedListenerType handler = getConsumeFinishedListener(requestCode);
        if (handler != null)
        {
            Timber.d("notifyPurchaseConsumeSuccess passing on the purchase for requestCode " + requestCode);
            handler.onPurchaseConsumed(requestCode, purchase);
        }
        else
        {
            Timber.d("notifyPurchaseConsumeSuccess No THIABPurchaseHandler for requestCode " + requestCode);
        }
    }

    protected void notifyPurchaseConsumeFail(int requestCode, IABPurchaseType purchase, IABException exception)
    {
        Timber.e("notifyPurchaseConsumeFail There was an exception during the consumption", exception);
        IABConsumeFinishedListenerType handler = getConsumeFinishedListener(requestCode);
        if (handler != null)
        {
            Timber.d("notifyPurchaseConsumeFail passing on the exception for requestCode " + requestCode);
            handler.onPurchaseConsumeFailed(requestCode, purchase, exception);
        }
        else
        {
            Timber.d("notifyPurchaseConsumeFail No THIABPurchaseHandler for requestCode " + requestCode);
        }
    }

    @Override public void onDestroy()
    {
        for (IABPurchaseConsumerType iabPurchaseConsumer: iabPurchaseConsumers.values())
        {
            if (iabPurchaseConsumer != null)
            {
                iabPurchaseConsumer.setListener(null);
                iabPurchaseConsumer.setConsumptionFinishedListener(null);
                iabPurchaseConsumer.onDestroy();
            }
        }
        iabPurchaseConsumers.clear();
        consumptionFinishedListeners.clear();
        parentConsumeFinishedHandlers.clear();
    }

    abstract protected IABPurchaseConsumerType createPurchaseConsumer();
}
