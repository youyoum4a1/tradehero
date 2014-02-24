package com.tradehero.common.billing.googleplay;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.ProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
abstract public class BaseIABLogicHolder<
        IABSKUType extends IABSKU,
        IABProductIdentifierFetcherHolderType extends ProductIdentifierFetcherHolder<
                IABSKUType,
                IABProductIdentifierFetchedListenerType,
                IABException>,
        IABProductIdentifierFetchedListenerType extends ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                IABSKUType,
                IABException>,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABInventoryFetcherHolderType extends IABInventoryFetcherHolder<
                IABSKUType,
                IABProductDetailType,
                IABInventoryFetchedListenerType,
                IABException>,
        IABInventoryFetchedListenerType extends BillingInventoryFetcher.OnInventoryFetchedListener<
                IABSKUType,
                IABProductDetailType,
                IABException>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<
                IABSKUType,
                IABOrderIdType>,
        IABPurchaseFetcherHolderType extends IABPurchaseFetcherHolder<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABPurchaseFetchedListenerType,
                IABException>,
        IABPurchaseFetchedListenerType extends BillingPurchaseFetcher.OnPurchaseFetchedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>,
        IABPurchaserHolderType extends IABPurchaserHolder<
                IABSKUType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABPurchaseFinishedListenerType,
                IABException>,
        IABPurchaseFinishedListenerType extends BillingPurchaser.OnPurchaseFinishedListener<
                IABSKUType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>,
        IABPurchaseConsumerType extends IABPurchaseConsumer<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType>,
        IABConsumeFinishedListenerType extends IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>>
    implements IABLogicHolder<
            IABSKUType,
            IABProductDetailType,
            IABPurchaseOrderType,
            IABOrderIdType,
            IABPurchaseType,
            IABPurchaseFetchedListenerType,
            IABPurchaseFinishedListenerType,
            IABConsumeFinishedListenerType,
            IABException>
{
    public static final int MAX_RANDOM_RETRIES = 50;

    protected WeakReference<Activity> weakActivity = new WeakReference<>(null);

    protected IABProductIdentifierFetcherHolderType productIdentifierFetcherHolder;
    protected IABInventoryFetcherHolderType inventoryFetcherHolder;
    protected IABPurchaseFetcherHolderType purchaseFetcherHolder;
    protected IABPurchaserHolderType purchaserHolder;

    protected Map<Integer /*requestCode*/, IABPurchaseConsumerType> iabPurchaseConsumers;
    protected Map<Integer /*requestCode*/, IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException>> consumptionFinishedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<IABConsumeFinishedListenerType>> parentConsumeFinishedHandlers;

    public BaseIABLogicHolder(Activity activity)
    {
        super();
        setActivity(activity);

        productIdentifierFetcherHolder = createProductIdentifierFetcherHolder();
        inventoryFetcherHolder = createInventoryFetcherHolder();
        purchaseFetcherHolder = createPurchaseFetcherHolder();
        purchaserHolder = createPurchaserHolder();

        iabPurchaseConsumers = new HashMap<>();
        consumptionFinishedListeners = new HashMap<>();
        parentConsumeFinishedHandlers = new HashMap<>();
    }

    public void onDestroy()
    {
        if (productIdentifierFetcherHolder != null)
        {
            productIdentifierFetcherHolder.onDestroy();
        }
        if (inventoryFetcherHolder != null)
        {
            inventoryFetcherHolder.onDestroy();
        }

        if (purchaserHolder != null)
        {
            purchaserHolder.onDestroy();
        }

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

    public Activity getActivity()
    {
        return weakActivity.get();
    }

    /**
     * The activity should be strongly referenced elsewhere
     * @param activity
     */
    public void setActivity(Activity activity)
    {
        this.weakActivity = new WeakReference<>(activity);
    }

    @Override public boolean isBillingAvailable() // TODO review to make less HACKy
    {
        return true;
    }

    @Override public int getUnusedRequestCode()
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

    public boolean isUnusedRequestCode(int randomNumber)
    {
        return
                productIdentifierFetcherHolder.isUnusedRequestCode(randomNumber) &&
                inventoryFetcherHolder.isUnusedRequestCode(randomNumber) &&
                purchaseFetcherHolder.isUnusedRequestCode(randomNumber) &&
                purchaserHolder.isUnusedRequestCode(randomNumber) &&

                !iabPurchaseConsumers.containsKey(randomNumber) &&
                !consumptionFinishedListeners.containsKey(randomNumber) &&
                !parentConsumeFinishedHandlers.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        productIdentifierFetcherHolder.unregisterProductIdentifierFetchedListener(requestCode);
        inventoryFetcherHolder.unRegisterInventoryFetchedListener(requestCode);
        purchaseFetcherHolder.unregisterPurchaseFetchedListener(requestCode);
        purchaserHolder.unregisterPurchaseFinishedListener(requestCode);

        iabPurchaseConsumers.remove(requestCode);
        consumptionFinishedListeners.remove(requestCode);
        parentConsumeFinishedHandlers.remove(requestCode);
    }

    //<editor-fold desc="IABPurchaseFetcherHolder">
    //</editor-fold>

    //<editor-fold desc="IABPurchaseConsumerHolder">
    /**
     * The purchaseConsumeHandler should be strongly referenced elsewhere
     * @param requestCode
     * @param purchaseConsumeHandler
     */
    protected void registerConsumeFinishedListener(int requestCode, IABConsumeFinishedListenerType purchaseConsumeHandler)
    {
        parentConsumeFinishedHandlers.put(requestCode, new WeakReference<>(purchaseConsumeHandler));
    }

    @Override public int registerConsumeFinishedListener(IABConsumeFinishedListenerType purchaseConsumeHandler)
    {
        int requestCode = getUnusedRequestCode();
        registerConsumeFinishedListener(requestCode, purchaseConsumeHandler);
        return requestCode;
    }

    @Override public void unregisterConsumeFinishedListener(int requestCode)
    {
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

    @Override public IABConsumeFinishedListenerType getConsumeFinishedListener(int requestCode)
    {
        WeakReference<IABConsumeFinishedListenerType> weakHandler = parentConsumeFinishedHandlers.get(requestCode);
        if (weakHandler != null)
        {
            return weakHandler.get();
        }
        return null;
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
    //</editor-fold>

    abstract protected BaseIABSKUList<IABSKUType> getAllSkus();
    abstract protected IABProductIdentifierFetcherHolderType createProductIdentifierFetcherHolder();
    abstract protected IABInventoryFetcherHolderType createInventoryFetcherHolder();
    abstract protected IABPurchaseFetcherHolderType createPurchaseFetcherHolder();
    abstract protected IABPurchaserHolderType createPurchaserHolder();

    abstract protected IABPurchaseConsumerType createPurchaseConsumer();

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        purchaserHolder.onActivityResult(requestCode, resultCode, data);
    }
}
