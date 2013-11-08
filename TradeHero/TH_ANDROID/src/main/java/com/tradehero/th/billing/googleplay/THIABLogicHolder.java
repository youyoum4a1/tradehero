package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaser;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.InventoryFetcher;
import com.tradehero.common.billing.googleplay.PurchaseFetcher;
import com.tradehero.common.billing.googleplay.exceptions.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exceptions.IABBillingUnavailableException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABRemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABUserCancelledException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
public class THIABLogicHolder
    implements THIABActor,
        SKUFetcher.SKUFetcherListener,
        PurchaseFetcher.PublicFetcherListener,
        InventoryFetcher.InventoryListener<THInventoryFetcher, THSKUDetails>
{
    public static final String TAG = THIABLogicHolder.class.getSimpleName();
    public static final int MAX_RANDOM_RETRIES = 50;

    private WeakReference<Activity> weakActivity = new WeakReference<>(null);
    private SKUFetcher skuFetcher;
    private THSKUDetailsTuner thSKUDetailsTuner;
    private PurchaseFetcher purchaseFetcher;
    private THInventoryFetcher inventoryFetcher;

    private Exception latestSkuFetcherException;
    private Exception latestInventoryFetcherException;
    private Exception latestPurchaseFetcherException;
    private Exception latestPurchaserException;

    private Map<Integer, SKUDetailsPurchaser> skuDetailsPurchasers;
    private Map<Integer, IABPurchaser.OnIABPurchaseFinishedListener> purchaseFinishedListeners;
    private Map<Integer, WeakReference<THIABPurchaseHandler>> purchaseHandlers;

    public THIABLogicHolder(Activity activity)
    {
        super();
        thSKUDetailsTuner = new THSKUDetailsTuner();
        setActivity(activity);
        skuDetailsPurchasers = new HashMap<>();
        purchaseFinishedListeners = new HashMap<>();
        purchaseHandlers = new HashMap<>();
    }

    public void onDestroy()
    {
        if (skuFetcher != null)
        {
            skuFetcher.setListener(null);
            skuFetcher.dispose();
        }
        skuFetcher = null;

        if (purchaseFetcher != null)
        {
            purchaseFetcher.setListener(null);
            purchaseFetcher.setFetchListener(null);
            purchaseFetcher.dispose();
        }
        purchaseFetcher = null;

        if (inventoryFetcher != null)
        {
            inventoryFetcher.setListener(null);
            inventoryFetcher.setInventoryListener(null);
            inventoryFetcher.setSkuDetailsTuner(null);
            inventoryFetcher.dispose();
        }
        inventoryFetcher = null;

        for (SKUDetailsPurchaser skuDetailsPurchaser: skuDetailsPurchasers.values())
        {
            if (skuDetailsPurchaser != null)
            {
                skuDetailsPurchaser.setListener(null);
                skuDetailsPurchaser.setPurchaseFinishedListener(null);
                skuDetailsPurchaser.dispose();
            }
        }
        skuDetailsPurchasers.clear();
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
            if (!skuDetailsPurchasers.containsKey(randomNumber) &&
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
        skuDetailsPurchasers.remove(requestCode);
        purchaseFinishedListeners.remove(requestCode);
        purchaseHandlers.remove(requestCode);
    }

    /**
     * The purchaseHandler should be strongly referenced elsewhere.
     * @param purchaseHandler
     * @return
     */
    protected void registerPurchaseHandler(int requestCode, THIABPurchaseHandler purchaseHandler)
    {
        purchaseHandlers.put(requestCode, new WeakReference<>(purchaseHandler));
    }

    //<editor-fold desc="THIABActor">
    @Override public void launchSkuInventorySequence()
    {
        latestSkuFetcherException = null;
        latestInventoryFetcherException = null;
        if (inventoryFetcher != null)
        {
            inventoryFetcher.setListener(null);
            inventoryFetcher.setInventoryListener(null);
            inventoryFetcher.setSkuDetailsTuner(null);
        }
        inventoryFetcher = null;

        if (skuFetcher != null)
        {
            skuFetcher.setListener(null);
        }
        skuFetcher = new SKUFetcher();
        skuFetcher.setListener(this);
        skuFetcher.fetchSkus();
    }

    @Override public boolean isBillingAvailable()
    {
        return latestInventoryFetcherException == null || !(latestInventoryFetcherException instanceof IABBillingUnavailableException);
    }

    @Override public boolean hadErrorLoadingInventory()
    {
        return latestInventoryFetcherException != null && !(latestInventoryFetcherException instanceof IABBillingUnavailableException);
    }

    @Override public boolean isInventoryReady()
    {
        return inventoryFetcher != null && inventoryFetcher.getInventory() != null && inventoryFetcher.getInventory().size() > 0;
    }

    @Override public List<THSKUDetails> getDetailsOfDomain(String domain)
    {
        List<THSKUDetails> details = null;
        if (inventoryFetcher != null && inventoryFetcher.getInventory() != null)
        {
            details = ArrayUtils.filter(inventoryFetcher.getInventory().values(), THSKUDetails.getPredicateIsOfCertainDomain(domain));
        }
        return details;
    }

    @Override public int launchPurchaseSequence(THIABPurchaseHandler purchaseHandler, THSKUDetails skuDetails)
    {
        return launchPurchaseSequence(purchaseHandler, skuDetails, null);
    }

    @Override public int launchPurchaseSequence(THIABPurchaseHandler purchaseHandler, THSKUDetails skuDetails, Object extraData)
    {
        if (!(extraData instanceof String))
        {
            throw new IllegalArgumentException("Extra data needs to be a String");
        }
        return launchPurchaseSequence(purchaseHandler, skuDetails, (String) extraData);
    }

    @Override public int launchPurchaseSequence(THIABPurchaseHandler purchaseHandler, THSKUDetails skuDetails, String extraData)
    {
        int requestCode = getUnusedRequestCode();
        registerPurchaseHandler(requestCode, purchaseHandler);
        createAndRegisterPurchaseFinishedListener(requestCode);

        SKUDetailsPurchaser skuDetailsPurchaser = createSkuDetailsPurchaser(requestCode);
        skuDetailsPurchasers.put(requestCode, skuDetailsPurchaser);
        skuDetailsPurchaser.purchase(skuDetails, extraData, requestCode);
        return requestCode;
    }
    //</editor-fold>

    //<editor-fold desc="SKUFetcher.SKUFetcherListener">
    @Override public void onFetchSKUsFailed(SKUFetcher fetcher, Exception exception)
    {
        if (fetcher == this.skuFetcher)
        {
            latestSkuFetcherException = exception;
            THToast.show("There was an error fetching the list of SKUs");
        }
        else
        {
            THLog.e(TAG, "We have received a callback from another sku fetcher", exception);
        }
    }

    @Override public void onFetchedSKUs(SKUFetcher fetcher, Map<String, List<IABSKU>> availableSkus)
    {
        if (fetcher == this.skuFetcher)
        {
            List<IABSKU> mixedIABSKUs = availableSkus.get(Constants.ITEM_TYPE_INAPP);
            if (availableSkus.containsKey(Constants.ITEM_TYPE_SUBS))
            {
                mixedIABSKUs.addAll(availableSkus.get(Constants.ITEM_TYPE_SUBS));
            }
            latestInventoryFetcherException = null;
            inventoryFetcher = new THInventoryFetcher(getActivity(), mixedIABSKUs);
            inventoryFetcher.setSkuDetailsTuner(thSKUDetailsTuner);
            inventoryFetcher.setInventoryListener(this);
            inventoryFetcher.startConnectionSetup();
        }
        else
        {
            THLog.w(TAG, "We have received a callback from another skuFetcher");
        }
    }
    //</editor-fold>

    public void launchFetchPurchasesSequence()
    {
        latestPurchaseFetcherException = null;
        if (purchaseFetcher != null)
        {
            purchaseFetcher.setListener(null);
            purchaseFetcher.setFetchListener(null);
        }
        purchaseFetcher = new PurchaseFetcher(getActivity());
        purchaseFetcher.setFetchListener(this);
        purchaseFetcher.startConnectionSetup();
    }

    //<editor-fold desc="PurchaseFetcher.PublicFetcherListener">
    @Override public void onFetchPurchasesFailed(PurchaseFetcher fetcher, IABException exception)
    {
        if (fetcher == this.purchaseFetcher)
        {
            latestPurchaseFetcherException = exception;
            //handleException(exception); // TODO
        }
        else
        {
            THLog.e(TAG, "We have received a callback from another purchaseFetcher", exception);
        }
    }

    @Override public void onFetchedPurchases(PurchaseFetcher fetcher, Map<IABSKU, IABPurchase> purchases)
    {
        if (fetcher == this.purchaseFetcher)
        {
            if (purchases != null && purchases.size() > 0)
            {
                THToast.show("There are some purchases to be consumed");
            }
            else
            {
                //THToast.show("There is no purchase waiting to be consumed");
            }
        }
        else
        {
            THLog.w(TAG, "We have received a callback from another purchaseFetcher");
        }

    }
    //</editor-fold>

    //<editor-fold desc="InventoryFetcher.InventoryListener">
    @Override public void onInventoryFetchSuccess(THInventoryFetcher fetcher, Map<IABSKU, THSKUDetails> inventory)
    {
        if (fetcher == this.inventoryFetcher)
        {
            //THToast.show("Inventory successfully fetched");
        }
        else
        {
            THLog.w(TAG, "We have received a callback from another inventoryFetcher");
        }

    }

    @Override public void onInventoryFetchFail(THInventoryFetcher fetcher, IABException exception)
    {
        if (fetcher == inventoryFetcher)
        {
            latestInventoryFetcherException = exception;
            //handleException(exception); // TODO
        }
        else
        {
            THLog.e(TAG, "We have received a callback from another inventoryFetcher", exception);
        }
    }
    //</editor-fold>

    protected void createAndRegisterPurchaseFinishedListener(final int requestCode)
    {
        purchaseFinishedListeners.put(requestCode, createPurchaseFinishedListener(requestCode));
    }

    protected IABPurchaser.OnIABPurchaseFinishedListener createPurchaseFinishedListener(final int requestCode)
    {
        return new IABPurchaser.OnIABPurchaseFinishedListener()
        {
            private THIABPurchaseHandler getPurchaseHandler()
            {
                WeakReference<THIABPurchaseHandler> weakHandler = purchaseHandlers.get(requestCode);
                if (weakHandler != null)
                {
                    return weakHandler.get();
                }
                return null;
            }

            @Override public void onIABPurchaseFinished(IABPurchaser purchaser, IABPurchase info)
            {
                THToast.show("OnIABPurchaseFinishedListener.onIABPurchaseFinished Purchase went through ok");
                THLog.d(TAG, "OnIABPurchaseFinishedListener.onIABPurchaseFinished Purchase info " + info);
                THIABPurchaseHandler handler = getPurchaseHandler();
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
                THIABPurchaseHandler handler = getPurchaseHandler();
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

    protected SKUDetailsPurchaser createSkuDetailsPurchaser(final int requestCode)
    {
        SKUDetailsPurchaser purchaser = new SKUDetailsPurchaser(getActivity());
        purchaser.setPurchaseFinishedListener(purchaseFinishedListeners.get(requestCode));
        return purchaser;
    }

    public Exception getLatestInventoryFetcherException()
    {
        return latestInventoryFetcherException;
    }

    public Exception getLatestPurchaseFetcherException()
    {
        return latestPurchaseFetcherException;
    }

    public Exception getLatestSkuFetcherException()
    {
        return latestSkuFetcherException;
    }

    public Exception getLatestPurchaserException()
    {
        return latestPurchaserException;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        SKUDetailsPurchaser skuDetailsPurchaser = skuDetailsPurchasers.get(requestCode);
        if (skuDetailsPurchaser != null)
        {
            skuDetailsPurchaser.handleActivityResult(requestCode, resultCode, data);
        }
    }

    protected void handleException(IABException exception) // TODO better
    {
        if (exception instanceof IABBillingUnavailableException)
        {
            THToast.show("User has no account or did not allow billing");
        }
        else if (exception instanceof IABVerificationFailedException)
        {
            THToast.show("The communication with Google Play may have been tampered with");
        }
        else if (exception instanceof IABBadResponseException)
        {
            THToast.show("Google Play returned unexpected information");
        }
        else if (exception instanceof IABRemoteException)
        {
            THToast.show("Problem when accessing a remote service");
        }
        else if (exception instanceof IABUserCancelledException)
        {
            IABAlertUtils.popUserCancelled(getActivity());
        }
        else
        {
            THToast.show("There was some error communicating with Google Play " + exception.getClass().getSimpleName());
        }
    }
}
