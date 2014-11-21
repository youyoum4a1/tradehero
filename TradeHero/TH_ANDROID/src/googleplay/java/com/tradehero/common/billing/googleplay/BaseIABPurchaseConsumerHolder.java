package com.tradehero.common.billing.googleplay;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

abstract public class BaseIABPurchaseConsumerHolder<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABPurchaseConsumerType extends IABPurchaseConsumer<
                        IABSKUType,
                        IABOrderIdType,
                        IABPurchaseType,
                        IABException>>
    implements IABPurchaseConsumerHolder<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType,
        IABException>
{
    @NonNull protected Map<Integer /*requestCode*/, IABPurchaseConsumerType> iabPurchaseConsumers;
    @NonNull protected Map<Integer /*requestCode*/, IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABException>> parentConsumeFinishedHandlers;

    //<editor-fold desc="Constructors">
    public BaseIABPurchaseConsumerHolder()
    {
        super();
        iabPurchaseConsumers = new HashMap<>();
        parentConsumeFinishedHandlers = new HashMap<>();
    }
    //</editor-fold>

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return !iabPurchaseConsumers.containsKey(requestCode) &&
                !parentConsumeFinishedHandlers.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        parentConsumeFinishedHandlers.remove(requestCode);
        IABPurchaseConsumerType purchaseConsumer = iabPurchaseConsumers.get(requestCode);
        if (purchaseConsumer != null)
        {
            purchaseConsumer.setListener(null);
            purchaseConsumer.setConsumptionFinishedListener(null);
        }
        iabPurchaseConsumers.remove(requestCode);
    }

    @Override @Nullable public IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABException> getConsumptionFinishedListener(int requestCode)
    {
        return parentConsumeFinishedHandlers.get(requestCode);
    }

    @Override public void registerConsumptionFinishedListener(
            int requestCode,
            @Nullable IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                    IABSKUType,
                    IABOrderIdType,
                    IABPurchaseType,
                    IABException> purchaseConsumeHandler)
    {
        parentConsumeFinishedHandlers.put(requestCode, purchaseConsumeHandler);
    }

    @Override public void launchConsumeSequence(int requestCode, IABPurchaseType purchase)
    {
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> consumeListener = createConsumptionFinishedListener();
        IABPurchaseConsumerType iabPurchaseConsumer = createPurchaseConsumer();
        iabPurchaseConsumer.setConsumptionFinishedListener(consumeListener);
        iabPurchaseConsumers.put(requestCode, iabPurchaseConsumer);
        iabPurchaseConsumer.consume(requestCode, purchase);
    }

    @NonNull protected IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException>
            createConsumptionFinishedListener()
    {
        return new IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException>()
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
        parentConsumeFinishedHandlers.clear();
    }

    abstract protected IABPurchaseConsumerType createPurchaseConsumer();
}
