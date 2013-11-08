package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.billing.googleplay.BaseIABActor;
import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaser;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.InventoryFetcher;
import com.tradehero.common.billing.googleplay.PurchaseFetcher;
import com.tradehero.common.billing.googleplay.exceptions.IABBillingUnavailableException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
public class THIABLogicHolder
    extends BaseIABActor<IABSKU, THSKUDetails, IABOrderId, SKUPurchase, SKUDetailsPurchaser, THIABPurchaseHandler>
    implements THIABActor,
        SKUFetcher.SKUFetcherListener,
        PurchaseFetcher.PublicFetcherListener,
        InventoryFetcher.InventoryListener<THInventoryFetcher, THSKUDetails>
{
    public static final String TAG = THIABLogicHolder.class.getSimpleName();
    public static final int MAX_RANDOM_RETRIES = 50;

    protected SKUFetcher skuFetcher;
    protected THSKUDetailsTuner thSKUDetailsTuner;
    protected PurchaseFetcher purchaseFetcher;
    protected THInventoryFetcher inventoryFetcher;

    protected Exception latestSkuFetcherException;
    protected Exception latestInventoryFetcherException;
    protected Exception latestPurchaseFetcherException;

    public THIABLogicHolder(Activity activity)
    {
        super(activity);
        thSKUDetailsTuner = new THSKUDetailsTuner();
    }

    public void onDestroy()
    {
        super.onDestroy();
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

    @Override public void onFetchedPurchases(PurchaseFetcher fetcher, Map<IABSKU, SKUPurchase> purchases)
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

    protected SKUDetailsPurchaser createPurchaser(final int requestCode)
    {
        SKUDetailsPurchaser purchaser = new SKUDetailsPurchaser(getActivity());
        purchaser.setPurchaseFinishedListener(purchaseFinishedListeners.get(requestCode));
        return purchaser;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        SKUDetailsPurchaser skuDetailsPurchaser = iabPurchasers.get(requestCode);
        if (skuDetailsPurchaser != null)
        {
            skuDetailsPurchaser.handleActivityResult(requestCode, resultCode, data);
        }
    }
}
