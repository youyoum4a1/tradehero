package com.tradehero.common.billing.googleplay;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.billing.googleplay.exceptions.IABBillingUnavailableException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.billing.googleplay.SKUDetailsPurchaser;
import com.tradehero.th.billing.googleplay.SKUFetcher;
import com.tradehero.th.billing.googleplay.THIABPurchaseHandler;
import com.tradehero.th.billing.googleplay.THInventoryFetcher;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
abstract public class BaseIABActor<
                    IABSKUType extends IABSKU,
                    IABProductDetailsType extends IABProductDetails<IABSKUType>,
                    IABOrderIdType extends IABOrderId,
                    IABPurchaseType extends IABPurchase<IABOrderIdType, IABSKUType>,
                    IABPurchaserType extends IABPurchaser<IABSKUType, IABProductDetailsType, IABOrderIdType, IABPurchaseType>,
                    IABPurchaseHandlerType extends IABPurchaseHandler<IABSKUType, IABOrderIdType, IABPurchaseType, IABException>>
    implements IABActor<
                    IABSKUType,
                    IABProductDetailsType,
                    IABOrderIdType,
                    IABPurchaseType,
                    IABPurchaseHandlerType,
                    IABException>
{
    public static final String TAG = BaseIABActor.class.getSimpleName();
    public static final int MAX_RANDOM_RETRIES = 50;

    protected WeakReference<Activity> weakActivity = new WeakReference<>(null);

    protected Map<Integer, IABPurchaserType> iabPurchasers;
    protected Map<Integer, IABPurchaser.OnIABPurchaseFinishedListener> purchaseFinishedListeners;
    protected Map<Integer, WeakReference<IABPurchaseHandlerType>> purchaseHandlers;

    public BaseIABActor(Activity activity)
    {
        super();
        setActivity(activity);
        iabPurchasers = new HashMap<>();
        purchaseFinishedListeners = new HashMap<>();
        purchaseHandlers = new HashMap<>();
    }

    public void onDestroy()
    {
        for (IABPurchaser iabPurchaser: iabPurchasers.values())
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
            if (!iabPurchasers.containsKey(randomNumber) &&
                    !purchaseFinishedListeners.containsKey(randomNumber) &&
                    !purchaseHandlers.containsKey(randomNumber))
            {
                return randomNumber;
            }
        }
        throw new IllegalStateException("Could not find an unused requestCode after " + MAX_RANDOM_RETRIES + " trials");
    }

    public void forgetRequestCode(int requestCode)
    {
        iabPurchasers.remove(requestCode);
        purchaseFinishedListeners.remove(requestCode);
        purchaseHandlers.remove(requestCode);
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

    //<editor-fold desc="IABActor">
    @Override public int launchPurchaseSequence(IABPurchaseHandlerType purchaseHandler, IABProductDetailsType skuDetails)
    {
        return launchPurchaseSequence(purchaseHandler, skuDetails, null);
    }

    @Override public int launchPurchaseSequence(IABPurchaseHandlerType purchaseHandler, IABProductDetailsType skuDetails, Object extraData)
    {
        if (!(extraData instanceof String))
        {
            throw new IllegalArgumentException("Extra data needs to be a String");
        }
        return launchPurchaseSequence(purchaseHandler, skuDetails, (String) extraData);
    }

    @Override public int launchPurchaseSequence(IABPurchaseHandlerType purchaseHandler, IABProductDetailsType skuDetails, String extraData)
    {
        int requestCode = getUnusedRequestCode();
        registerPurchaseHandler(requestCode, purchaseHandler);
        createAndRegisterPurchaseFinishedListener(requestCode);

        IABPurchaserType iabPurchaser = createPurchaser(requestCode);
        iabPurchasers.put(requestCode, iabPurchaser);
        iabPurchaser.purchase(skuDetails, extraData, requestCode);
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

    abstract protected <PurchaserType extends IABPurchaser<IABSKUType, IABProductDetailsType, IABOrderIdType, IABPurchaseType>>
        PurchaserType createPurchaser(final int requestCode);

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IABPurchaser iabPurchaser = iabPurchasers.get(requestCode);
        if (iabPurchaser != null)
        {
            iabPurchaser.handleActivityResult(requestCode, resultCode, data);
        }
    }
}
