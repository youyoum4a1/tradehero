package com.tradehero.common.billing.googleplay;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.exception.IABBillingUnavailableException;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
abstract public class BaseIABLogicHolder<
        IABSKUType extends IABSKU,
        OnProductIdentifierFetchedListenerType extends ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKUType, IABException>,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABInventoryFetcherType extends IABBillingInventoryFetcher<
                        IABSKUType,
                        IABProductDetailType>,
        IABInventoryFetchedListenerType extends BillingInventoryFetcher.OnInventoryFetchedListener<
                IABSKUType,
                IABProductDetailType,
                IABException>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<
                IABSKUType,
                IABOrderIdType>,
        IABPurchaseFetcherType extends IABPurchaseFetcher<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType>,
        IABPurchaseFetchedListenerType extends IABPurchaseFetcher.OnPurchaseFetchedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType>,
        IABPurchaserType extends IABPurchaser<
                IABSKUType,
                IABProductDetailType,
                IABOrderIdType,
                IABPurchaseOrderType,
                IABPurchaseType>,
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
            OnProductIdentifierFetchedListenerType,
            IABProductDetailType,
            IABInventoryFetchedListenerType,
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
    protected boolean inventoryReady = false; // TODO this feels HACKy
    protected boolean errorLoadingInventory = false; // TODO here too
    protected Exception latestInventoryFetcherException; // TODO here too

    protected Map<Integer /*requestCode*/, IABInventoryFetcherType> iabInventoryFetchers;
    protected Map<Integer /*requestCode*/, BillingInventoryFetcher.OnInventoryFetchedListener<IABSKUType, IABProductDetailType, IABException>> inventoryFetchedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<IABInventoryFetchedListenerType>>parentInventoryFetchedListeners;

    protected Map<Integer /*requestCode*/, IABPurchaseFetcherType> purchaseFetchers;
    protected Map<Integer /*requestCode*/, IABPurchaseFetcher.OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType>> purchaseFetchedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<IABPurchaseFetchedListenerType>> parentPurchaseFetchedListeners;

    protected Map<Integer /*requestCode*/, IABPurchaserType> iabPurchasers;
    protected Map<Integer /*requestCode*/, BillingPurchaser.OnPurchaseFinishedListener<IABSKUType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType, IABException>> purchaseFinishedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<IABPurchaseFinishedListenerType>> parentPurchaseFinishedListeners;

    protected Map<Integer /*requestCode*/, IABPurchaseConsumerType> iabPurchaseConsumers;
    protected Map<Integer /*requestCode*/, IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException>> consumptionFinishedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<IABConsumeFinishedListenerType>> parentConsumeFinishedHandlers;

    public BaseIABLogicHolder(Activity activity)
    {
        super();
        setActivity(activity);
        iabInventoryFetchers = new HashMap<>();
        inventoryFetchedListeners = new HashMap<>();
        parentInventoryFetchedListeners = new HashMap<>();

        purchaseFetchers = new HashMap<>();
        purchaseFetchedListeners = new HashMap<>();
        parentPurchaseFetchedListeners = new HashMap<>();

        iabPurchasers = new HashMap<>();
        purchaseFinishedListeners = new HashMap<>();
        parentPurchaseFinishedListeners = new HashMap<>();

        iabPurchaseConsumers = new HashMap<>();
        consumptionFinishedListeners = new HashMap<>();
        parentConsumeFinishedHandlers = new HashMap<>();
    }

    public void onDestroy()
    {
        for (IABInventoryFetcherType inventoryFetcher : iabInventoryFetchers.values())
        {
            if (inventoryFetcher != null)
            {
                inventoryFetcher.setListener(null);
                inventoryFetcher.setInventoryFetchedListener(null);
                inventoryFetcher.onDestroy();
            }
        }
        iabInventoryFetchers.clear();
        inventoryFetchedListeners.clear();
        parentInventoryFetchedListeners.clear();

        for (IABPurchaseFetcherType purchaseFetcher : purchaseFetchers.values())
        {
            if (purchaseFetcher != null)
            {
                purchaseFetcher.setListener(null);
                purchaseFetcher.setFetchListener(null);
                purchaseFetcher.onDestroy();
            }
        }
        purchaseFetchers.clear();
        purchaseFetchedListeners.clear();
        parentPurchaseFetchedListeners.clear();

        for (IABPurchaserType iabPurchaser: iabPurchasers.values())
        {
            if (iabPurchaser != null)
            {
                iabPurchaser.setListener(null);
                iabPurchaser.setPurchaseFinishedListener(null);
                iabPurchaser.onDestroy();
            }
        }
        iabPurchasers.clear();
        purchaseFinishedListeners.clear();
        parentPurchaseFinishedListeners.clear();

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
        return latestInventoryFetcherException == null || !(latestInventoryFetcherException instanceof IABBillingUnavailableException);
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
        return !iabInventoryFetchers.containsKey(randomNumber) &&
                !inventoryFetchedListeners.containsKey(randomNumber) &&
                !parentInventoryFetchedListeners.containsKey(randomNumber) &&

                !purchaseFetchers.containsKey(randomNumber) &&
                !purchaseFetchedListeners.containsKey(randomNumber) &&
                !parentPurchaseFetchedListeners.containsKey(randomNumber) &&

                !iabPurchasers.containsKey(randomNumber) &&
                !purchaseFinishedListeners.containsKey(randomNumber) &&
                !parentPurchaseFinishedListeners.containsKey(randomNumber) &&

                !iabPurchaseConsumers.containsKey(randomNumber) &&
                !consumptionFinishedListeners.containsKey(randomNumber) &&
                !parentConsumeFinishedHandlers.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        iabInventoryFetchers.remove(requestCode);
        inventoryFetchedListeners.remove(requestCode);
        parentInventoryFetchedListeners.remove(requestCode);

        purchaseFetchers.remove(requestCode);
        purchaseFetchedListeners.remove(requestCode);
        parentPurchaseFetchedListeners.remove(requestCode);

        iabPurchasers.remove(requestCode);
        purchaseFinishedListeners.remove(requestCode);
        parentPurchaseFinishedListeners.remove(requestCode);

        iabPurchaseConsumers.remove(requestCode);
        consumptionFinishedListeners.remove(requestCode);
        parentConsumeFinishedHandlers.remove(requestCode);
    }

    @Override public IABInventoryFetchedListenerType getInventoryFetchedListener(int requestCode)
    {
        WeakReference<IABInventoryFetchedListenerType> weakFetchedListener = parentInventoryFetchedListeners.get(requestCode);
        if (weakFetchedListener == null)
        {
            return null;
        }
        return weakFetchedListener.get();
    }

    /**
     * The listener needs to be strong referenced elsewhere
     * @param requestCode
     * @param inventoryFetchedListener
     */
    protected void registerInventoryFetchedListener(int requestCode, IABInventoryFetchedListenerType inventoryFetchedListener)
    {
        parentInventoryFetchedListeners.put(requestCode, new WeakReference<>(inventoryFetchedListener));
    }

    /**
     * The listener needs to be strong referenced elsewhere
     * @param inventoryFetchedListener
     * @return
     */
    @Override public int registerInventoryFetchedListener(IABInventoryFetchedListenerType inventoryFetchedListener)
    {
        int requestCode = getUnusedRequestCode();
        registerInventoryFetchedListener(requestCode, inventoryFetchedListener);
        return requestCode;
    }

    @Override public void launchInventoryFetchSequence(int requestCode)
    {
        latestInventoryFetcherException = null;
        BillingInventoryFetcher.OnInventoryFetchedListener<IABSKUType, IABProductDetailType, IABException>
                fetchedListener = new BillingInventoryFetcher.OnInventoryFetchedListener<IABSKUType, IABProductDetailType, IABException>()
        {
            @Override public void onInventoryFetchSuccess(int requestCode, List<IABSKUType> productIdentifiers, Map<IABSKUType, IABProductDetailType> inventory)
            {
                notifyInventoryFetchedSuccess(requestCode, productIdentifiers, inventory);
            }

            @Override public void onInventoryFetchFail(int requestCode, List<IABSKUType> productIdentifiers, IABException exception)
            {
                notifyInventoryFetchFailed(requestCode, productIdentifiers, exception);
            }
        };
        inventoryFetchedListeners.put(requestCode, fetchedListener);

        IABInventoryFetcherType inventoryFetcher = createInventoryFetcher();
        iabInventoryFetchers.put(requestCode, inventoryFetcher);
        inventoryFetcher.setProductIdentifiers(getAllSkus());
        inventoryFetcher.setInventoryFetchedListener(fetchedListener);
        inventoryFetcher.fetchInventory(requestCode);
    }

    protected void notifyInventoryFetchedSuccess(int requestCode, List<IABSKUType> productIdentifiers, Map<IABSKUType, IABProductDetailType> inventory)
    {
        inventoryReady = true;
        errorLoadingInventory = false;
        IABInventoryFetchedListenerType parentFetchedListener = getInventoryFetchedListener(requestCode);
        if (parentFetchedListener != null)
        {
            parentFetchedListener.onInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
        }
    }

    protected void notifyInventoryFetchFailed(int requestCode, List<IABSKUType> productIdentifiers, IABException exception)
    {
        latestInventoryFetcherException = exception;
        inventoryReady = false;
        errorLoadingInventory = !(exception instanceof IABBillingUnavailableException);
        IABInventoryFetchedListenerType parentFetchedListener = getInventoryFetchedListener(requestCode);
        if (parentFetchedListener != null)
        {
            parentFetchedListener.onInventoryFetchFail(requestCode, productIdentifiers, exception);
        }
    }

    @Override public boolean hadErrorLoadingInventory()
    {
        return errorLoadingInventory;
    }

    @Override public boolean isInventoryReady()
    {
        return inventoryReady;
    }

    @Override public IABPurchaseFetchedListenerType getPurchaseFetchedListener(int requestCode)
    {
        WeakReference<IABPurchaseFetchedListenerType> weakListener = parentPurchaseFetchedListeners.get(requestCode);
        if (weakListener == null)
        {
            return null;
        }
        return weakListener.get();
    }

    /**
     * The listener needs to be strongly referenced elsewhere.
     * @param requestCode
     * @param purchaseFetchedListener
     */
    protected void registerPurchaseFetchedListener(int requestCode, IABPurchaseFetchedListenerType purchaseFetchedListener)
    {
        parentPurchaseFetchedListeners.put(requestCode, new WeakReference<>(purchaseFetchedListener));
    }

    /**
     * The listener needs to be strongly referenced elsewhere.
     * @param purchaseFetchedListener
     * @return
     */
    @Override public int registerPurchaseFetchedListener(IABPurchaseFetchedListenerType purchaseFetchedListener)
    {
        int requestCode = getUnusedRequestCode();
        registerPurchaseFetchedListener(requestCode, purchaseFetchedListener);
        return requestCode;
    }

    @Override public void launchFetchPurchaseSequence(int requestCode)
    {
        IABPurchaseFetcher.OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType> purchaseFetchedListener = new IABPurchaseFetcher.OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType>()
        {
            @Override public void onFetchPurchasesFailed(int requestCode, IABException exception)
            {
                notifyPurchaseFetchedFailed(requestCode, exception);
            }

            @Override public void onFetchedPurchases(int requestCode, Map<IABSKUType, IABPurchaseType> purchases)
            {
                notifyPurchaseFetchedSuccess(requestCode, purchases);
            }
        };
        purchaseFetchedListeners.put(requestCode, purchaseFetchedListener);
        IABPurchaseFetcherType purchaseFetcher = createPurchaseFetcher();
        purchaseFetcher.setFetchListener(purchaseFetchedListener);
        purchaseFetchers.put(requestCode, purchaseFetcher);
        purchaseFetcher.fetchPurchases(requestCode);
    }

    protected void notifyPurchaseFetchedSuccess(int requestCode, Map<IABSKUType, IABPurchaseType> purchases)
    {
        IABPurchaseFetchedListenerType parentListener = getPurchaseFetchedListener(requestCode);
        if (parentListener != null)
        {
            parentListener.onFetchedPurchases(requestCode, purchases);
        }
    }

    protected void notifyPurchaseFetchedFailed(int requestCode, IABException exception)
    {
        IABPurchaseFetchedListenerType parentListener = getPurchaseFetchedListener(requestCode);
        if (parentListener != null)
        {
            parentListener.onFetchPurchasesFailed(requestCode, exception);
        }
    }

    /**
     * The listener should be strongly referenced elsewhere.
     * @param purchaseFinishedListener
     * @return
     */
    protected void registerPurchaseFinishedListener(int requestCode, IABPurchaseFinishedListenerType purchaseFinishedListener)
    {
        parentPurchaseFinishedListeners.put(requestCode, new WeakReference<>(purchaseFinishedListener));
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param purchaseFinishedListener
     * @return
     */
    @Override public int registerPurchaseFinishedListener(IABPurchaseFinishedListenerType purchaseFinishedListener)
    {
        int requestCode = getUnusedRequestCode();
        registerPurchaseFinishedListener(requestCode, purchaseFinishedListener);
        return requestCode;
    }

    @Override public void launchPurchaseSequence(int requestCode, IABPurchaseOrderType purchaseOrder)
    {
        BillingPurchaser.OnPurchaseFinishedListener<IABSKUType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType, IABException> purchaseListener = new BillingPurchaser.OnPurchaseFinishedListener<IABSKUType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType, IABException>()
        {
            @Override public void onPurchaseFinished(int requestCode, IABPurchaseOrderType purchaseOrder, IABPurchaseType purchase)
            {
                notifyIABPurchaseFinished(requestCode, purchaseOrder, purchase);
            }

            @Override public void onPurchaseFailed(int requestCode, IABPurchaseOrderType purchaseOrder, IABException exception)
            {
                notifyIABPurchaseFailed(requestCode, purchaseOrder, exception);
            }
        };
        purchaseFinishedListeners.put(requestCode, purchaseListener);
        IABPurchaserType iabPurchaser = createPurchaser();
        iabPurchaser.setPurchaseFinishedListener(purchaseListener);
        iabPurchasers.put(requestCode, iabPurchaser);
        iabPurchaser.purchase(requestCode, purchaseOrder);
    }

    @Override public IABPurchaseFinishedListenerType getPurchaseFinishedListener(int requestCode)
    {
        WeakReference<IABPurchaseFinishedListenerType> weakHandler = parentPurchaseFinishedListeners.get(requestCode);
        if (weakHandler != null)
        {
            return weakHandler.get();
        }
        return null;
    }

    protected void notifyIABPurchaseFinished(int requestCode, IABPurchaseOrderType purchaseOrder, IABPurchaseType purchase)
    {
        Timber.d("notifyIABPurchaseFinished Purchase " + purchase);
        IABPurchaseFinishedListenerType handler = getPurchaseFinishedListener(requestCode);
        if (handler != null)
        {
            Timber.d("notifyIABPurchaseFinished passing on the purchase for requestCode " + requestCode);
            handler.onPurchaseFinished(requestCode, purchaseOrder, purchase);
        }
        else
        {
            Timber.d("notifyIABPurchaseFinished No OnPurchaseFinishedListener for requestCode " + requestCode);
        }
    }

    protected void notifyIABPurchaseFailed(int requestCode, IABPurchaseOrderType purchaseOrder, IABException exception)
    {
        Timber.e("notifyIABPurchaseFailed There was an exception during the purchase", exception);
        IABPurchaseFinishedListenerType handler = getPurchaseFinishedListener(requestCode);
        if (handler != null)
        {
            Timber.d("notifyIABPurchaseFailed passing on the exception for requestCode " + requestCode);
            handler.onPurchaseFailed(requestCode, purchaseOrder, exception);
        }
        else
        {
            Timber.d("onPurchaseFailed No THIABPurchaseHandler for requestCode " + requestCode);
        }
    }

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

    abstract protected BaseIABSKUList<IABSKUType> getAllSkus();
    abstract protected IABInventoryFetcherType createInventoryFetcher();
    abstract protected IABPurchaseFetcherType createPurchaseFetcher();
    abstract protected IABPurchaserType createPurchaser();
    abstract protected IABPurchaseConsumerType createPurchaseConsumer();

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Timber.d("onActivityResult requestCode: " + requestCode + ", resultCode: " + resultCode);
        IABPurchaser iabPurchaser = iabPurchasers.get(requestCode);
        if (iabPurchaser != null)
        {
            iabPurchaser.handleActivityResult(requestCode, resultCode, data);
        }
        else
        {
            Timber.w("onActivityResult no handler");
        }
    }
}
