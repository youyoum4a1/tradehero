package com.tradehero.common.billing.googleplay;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
abstract public class BaseIABActor<
                    IABSKUType extends IABSKU,
                    IABProductDetailsType extends IABProductDetails<IABSKUType>,
                    IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
                    IABOrderIdType extends IABOrderId,
                    IABPurchaseType extends IABPurchase<IABOrderIdType, IABSKUType>,
                    IABPurchaserType extends IABPurchaser<IABSKUType, IABProductDetailsType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType>,
                    IABPurchaseHandlerType extends IABPurchaseHandler<IABSKUType, IABOrderIdType, IABPurchaseType, IABException>,
                    IABPurchaseConsumerType extends IABPurchaseConsumer<IABSKUType, IABOrderIdType, IABPurchaseType>,
                    IABPurchaseConsumeHandlerType extends IABPurchaseConsumeHandler<IABSKUType, IABOrderIdType, IABPurchaseType, IABException>>
    implements IABActor<
                    IABSKUType,
                    IABProductDetailsType,
                    IABPurchaseOrderType,
                    IABOrderIdType,
                    IABPurchaseType,
                    IABPurchaseHandlerType,
                    IABPurchaseConsumeHandlerType,
                    IABException>
{
    public static final String TAG = BaseIABActor.class.getSimpleName();
    public static final int MAX_RANDOM_RETRIES = 50;

    protected WeakReference<Activity> weakActivity = new WeakReference<>(null);

    protected Map<Integer /*requestCode*/, IABPurchaserType> iabPurchasers;
    protected Map<Integer /*requestCode*/, IABPurchaser.OnIABPurchaseFinishedListener> purchaseFinishedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<IABPurchaseHandlerType>> purchaseHandlers;

    protected Map<Integer /*requestCode*/, IABPurchaseConsumerType> iabPurchaseConsumers;
    protected Map<Integer /*requestCode*/, IABPurchaseConsumer.OnIABConsumptionFinishedListener> purchaseConsumptionFinishedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<IABPurchaseConsumeHandlerType>> purchaseConsumeHandlers;


    public BaseIABActor(Activity activity)
    {
        super();
        setActivity(activity);
        iabPurchasers = new HashMap<>();
        purchaseFinishedListeners = new HashMap<>();
        purchaseHandlers = new HashMap<>();

        iabPurchaseConsumers = new HashMap<>();
        purchaseConsumptionFinishedListeners = new HashMap<>();
        purchaseConsumeHandlers = new HashMap<>();
    }

    public void onDestroy()
    {
        for (IABPurchaserType iabPurchaser: iabPurchasers.values())
        {
            if (iabPurchaser != null)
            {
                iabPurchaser.setListener(null);
                iabPurchaser.setPurchaseFinishedListener(null);
                iabPurchaser.dispose();
            }
        }
        iabPurchasers.clear();
        purchaseFinishedListeners.clear();
        purchaseHandlers.clear();

        for (IABPurchaseConsumerType iabPurchaseConsumer: iabPurchaseConsumers.values())
        {
            if (iabPurchaseConsumer != null)
            {
                iabPurchaseConsumer.setListener(null);
                iabPurchaseConsumer.setConsumptionFinishedListener(null);
                iabPurchaseConsumer.dispose();
            }
        }
        iabPurchaseConsumers.clear();
        purchaseConsumptionFinishedListeners.clear();
        purchaseConsumeHandlers.clear();
    }

    public Activity getActivity()
    {
        return weakActivity.get();
    }

    public void setActivity(Activity context)
    {
        this.weakActivity = new WeakReference<>(context);
    }

    public int getUnusedRequestCode()
    {
        int retries = MAX_RANDOM_RETRIES;
        int randomNumber;
        while (retries-- > 0)
        {
            randomNumber = (int) (Math.random() * Integer.MAX_VALUE);
            if (isUnusedRequestCode(randomNumber))
            {
                return randomNumber;
            }
        }
        throw new IllegalStateException("Could not find an unused requestCode after " + MAX_RANDOM_RETRIES + " trials");
    }

    protected boolean isUnusedRequestCode(int randomNumber)
    {
        return !iabPurchasers.containsKey(randomNumber) &&
                !purchaseFinishedListeners.containsKey(randomNumber) &&
                !purchaseHandlers.containsKey(randomNumber) &&
                !iabPurchaseConsumers.containsKey(randomNumber) &&
                !purchaseConsumptionFinishedListeners.containsKey(randomNumber) &&
                !purchaseConsumeHandlers.containsKey(randomNumber);
    }

    public void forgetRequestCode(int requestCode)
    {
        iabPurchasers.remove(requestCode);
        purchaseFinishedListeners.remove(requestCode);
        purchaseHandlers.remove(requestCode);

        iabPurchaseConsumers.remove(requestCode);
        purchaseConsumptionFinishedListeners.remove(requestCode);
        purchaseConsumeHandlers.remove(requestCode);
    }

    /**
     * The purchaseHandler should be strongly referenced elsewhere.
     * @param purchaseHandler
     * @return
     */
    protected void registerPurchaseHandler(int requestCode, IABPurchaseHandlerType purchaseHandler)
    {
        purchaseHandlers.put(requestCode, new WeakReference<>(purchaseHandler));
    }

    /**
     * The purchaseConsumeHandler should be strongly referenced elsewhere
     * @param requestCode
     * @param purchaseConsumeHandler
     */
    protected void registerPurchaseConsumeHandler(int requestCode, IABPurchaseConsumeHandlerType purchaseConsumeHandler)
    {
        purchaseConsumeHandlers.put(requestCode, new WeakReference<>(purchaseConsumeHandler));
    }

    //<editor-fold desc="IABActor">
    @Override public int launchPurchaseSequence(IABPurchaseHandlerType purchaseHandler, IABPurchaseOrderType purchaseOrder)
    {
        int requestCode = getUnusedRequestCode();
        registerPurchaseHandler(requestCode, purchaseHandler);
        createAndRegisterPurchaseFinishedListener(requestCode);

        IABPurchaserType iabPurchaser = createPurchaser(requestCode);
        iabPurchasers.put(requestCode, iabPurchaser);
        iabPurchaser.purchase(purchaseOrder, requestCode);
        return requestCode;
    }

    @Override public int launchConsumeSequence(IABPurchaseConsumeHandlerType purchaseConsumeHandler, IABPurchaseType purchase)
    {
        int requestCode = getUnusedRequestCode();
        registerPurchaseConsumeHandler(requestCode, purchaseConsumeHandler);
        createAndRegisterPurchaseConsumeFinishedListener(requestCode);

        IABPurchaseConsumerType iabPurchaseConsumer = createPurchaseConsumer(requestCode);
        iabPurchaseConsumers.put(requestCode, iabPurchaseConsumer);
        iabPurchaseConsumer.consume(purchase);
        return requestCode;
    }
    //</editor-fold>

    protected void createAndRegisterPurchaseFinishedListener(final int requestCode)
    {
        purchaseFinishedListeners.put(requestCode, createPurchaseFinishedListener(requestCode));
    }

    protected IABPurchaser.OnIABPurchaseFinishedListener createPurchaseFinishedListener(final int requestCode)
    {
        return new IABPurchaser.OnIABPurchaseFinishedListener<IABPurchaseType, IABException>()
        {
            private IABPurchaseHandlerType getPurchaseHandler()
            {
                WeakReference<IABPurchaseHandlerType> weakHandler = purchaseHandlers.get(requestCode);
                if (weakHandler != null)
                {
                    return weakHandler.get();
                }
                return null;
            }

            @Override public void onIABPurchaseFinished(IABPurchaser purchaser, IABPurchaseType info)
            {
                THToast.show("OnIABPurchaseFinishedListener.onIABPurchaseFinished Purchase went through ok");
                THLog.d(TAG, "OnIABPurchaseFinishedListener.onIABPurchaseFinished Purchase info " + info);
                IABPurchaseHandlerType handler = getPurchaseHandler();
                if (handler != null)
                {
                    THLog.d(TAG, "OnIABPurchaseFinishedListener.onIABPurchaseFinished passing on the purchase for requestCode " + requestCode);
                    handler.handlePurchaseReceived(requestCode, info);
                }
                else
                {
                    THLog.d(TAG, "OnIABPurchaseFinishedListener.onIABPurchaseFinished No THIABPurchaseHandler for requestCode " + requestCode);
                }
                finish();
            }

            @Override public void onIABPurchaseFailed(IABPurchaser purchaser, IABException exception)
            {
                THLog.e(TAG, "OnIABPurchaseFinishedListener.onIABPurchaseFailed There was an exception during the purchase", exception);
                IABPurchaseHandlerType handler = getPurchaseHandler();
                if (handler != null)
                {
                    THLog.d(TAG, "OnIABPurchaseFinishedListener.onIABPurchaseFailed passing on the exception for requestCode " + requestCode);
                    handler.handlePurchaseException(requestCode, exception);
                }
                else
                {
                    THLog.d(TAG, "OnIABPurchaseFinishedListener.onIABPurchaseFailed No THIABPurchaseHandler for requestCode " + requestCode);
                }
                finish();
            }

            private void finish()
            {
                forgetRequestCode(requestCode);
            }
        };
    }

    protected void createAndRegisterPurchaseConsumeFinishedListener(final int requestCode)
    {
        purchaseConsumptionFinishedListeners.put(requestCode, createPurchaseConsumeFinishedListener(requestCode));
    }

    protected IABPurchaseConsumer.OnIABConsumptionFinishedListener createPurchaseConsumeFinishedListener(final int requestCode)
    {
        return new IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABPurchaseType, IABException>()
        {
            private IABPurchaseConsumeHandlerType getPurchaseConsumeHandler()
            {
                WeakReference<IABPurchaseConsumeHandlerType> weakHandler = purchaseConsumeHandlers.get(requestCode);
                if (weakHandler != null)
                {
                    return weakHandler.get();
                }
                return null;
            }

            @Override public void onIABConsumptionFinished(IABPurchaseConsumer purchaseConsumer, IABPurchaseType info)
            {
                THToast.show("OnIABConsumptionFinishedListener.onIABPurchaseFinished Purchase went through ok");
                THLog.d(TAG, "OnIABConsumptionFinishedListener.onIABPurchaseFinished Purchase info " + info);
                IABPurchaseConsumeHandlerType handler = getPurchaseConsumeHandler();
                if (handler != null)
                {
                    THLog.d(TAG, "OnIABConsumptionFinishedListener.onIABPurchaseFinished passing on the purchase for requestCode " + requestCode);
                    handler.handlePurchaseConsumed(requestCode, info);
                }
                else
                {
                    THLog.d(TAG, "OnIABConsumptionFinishedListener.onIABPurchaseFinished No THIABPurchaseHandler for requestCode " + requestCode);
                }
                finish();
            }

            @Override public void onIABConsumptionFailed(IABPurchaseConsumer purchaseConsumer, IABException exception)
            {
                THLog.e(TAG, "OnIABConsumptionFinishedListener.onIABConsumptionFailed There was an exception during the consumption", exception);
                IABPurchaseConsumeHandlerType handler = getPurchaseConsumeHandler();
                if (handler != null)
                {
                    THLog.d(TAG, "OnIABConsumptionFinishedListener.onIABConsumptionFailed passing on the exception for requestCode " + requestCode);
                    handler.handlePurchaseConsumeException(requestCode, exception);
                }
                else
                {
                    THLog.d(TAG, "OnIABConsumptionFinishedListener.onIABConsumptionFailed No THIABPurchaseHandler for requestCode " + requestCode);
                }
                finish();
            }

            private void finish()
            {
                forgetRequestCode(requestCode);
            }
        };
    }

    abstract protected /*<PurchaserType extends IABPurchaser<IABSKUType, IABProductDetailsType, IABOrderIdType, IABPurchaseType>>*/
        IABPurchaserType createPurchaser(final int requestCode);

    abstract protected IABPurchaseConsumerType createPurchaseConsumer(final int requestCode);

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IABPurchaser iabPurchaser = iabPurchasers.get(requestCode);
        if (iabPurchaser != null)
        {
            iabPurchaser.handleActivityResult(requestCode, resultCode, data);
        }
    }
}
