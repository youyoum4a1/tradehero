package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.googleplay.exception.IABException;
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
                IABPurchaseType>>
    implements IABPurchaseConsumerHolder<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType,
        IABException>
{
    protected Map<Integer /*requestCode*/, IABPurchaseConsumerType> iabPurchaseConsumers;
    protected Map<Integer /*requestCode*/, IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException>>
            consumptionFinishedListeners;
    protected Map<Integer /*requestCode*/, IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABException>> parentConsumeFinishedHandlers;

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

    @Override public void forgetRequestCode(int requestCode)
    {
        consumptionFinishedListeners.remove(requestCode);
        parentConsumeFinishedHandlers.remove(requestCode);
        IABPurchaseConsumerType purchaseConsumer = iabPurchaseConsumers.get(requestCode);
        if (purchaseConsumer != null)
        {
            purchaseConsumer.setListener(null);
            purchaseConsumer.setConsumptionFinishedListener(null);
        }
        iabPurchaseConsumers.remove(requestCode);
    }

    @Override public IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABException> getConsumptionFinishedListener(int requestCode)
    {
        return parentConsumeFinishedHandlers.get(requestCode);
    }

    @Override public void registerConsumptionFinishedListener(int requestCode, IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABException> purchaseConsumeHandler)
    {
        parentConsumeFinishedHandlers.put(requestCode, purchaseConsumeHandler);
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
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException> handler = getConsumptionFinishedListener(requestCode);
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
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException> handler = getConsumptionFinishedListener(requestCode);
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
                iabPurchaseConsumer.onDestroy();
            }
        }
        iabPurchaseConsumers.clear();
        consumptionFinishedListeners.clear();
        parentConsumeFinishedHandlers.clear();
    }

    abstract protected IABPurchaseConsumerType createPurchaseConsumer();
}
