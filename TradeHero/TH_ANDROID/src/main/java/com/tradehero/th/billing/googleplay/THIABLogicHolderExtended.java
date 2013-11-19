package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.billing.googleplay.BaseIABActor;
import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumeHandler;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.InventoryFetcher;
import com.tradehero.common.billing.googleplay.PurchaseFetcher;
import com.tradehero.common.billing.googleplay.exceptions.IABBillingUnavailableException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.PurchaseReportedHandler;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
public class THIABLogicHolderExtended
    extends THIABLogicHolder
    implements SKUFetcher.SKUFetcherListener,
        PurchaseFetcher.PublicFetcherListener,
        InventoryFetcher.InventoryListener<THInventoryFetcher, THSKUDetails>
{
    public static final String TAG = THIABLogicHolderExtended.class.getSimpleName();

    protected SKUFetcher skuFetcher;
    protected PurchaseFetcher purchaseFetcher;
    protected THInventoryFetcher inventoryFetcher;

    protected Exception latestSkuFetcherException;
    protected Exception latestInventoryFetcherException;
    protected Exception latestPurchaseFetcherException;

    @Inject Lazy<UserProfileCache> userProfileCache;

    public THIABLogicHolderExtended(Activity activity)
    {
        super(activity);
        DaggerUtils.inject(this);
    }

    @Override public void onDestroy()
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
            inventoryFetcher.dispose();
        }
        inventoryFetcher = null;
        super.onDestroy();
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
            inventoryFetcher.setInventoryListener(this);
            inventoryFetcher.fetchInventory();
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
                sequentiallyConsume(purchases);
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

    protected void sequentiallyConsume(Map<IABSKU, SKUPurchase> purchases)
    {
        final THIABPurchaseConsumeHandler consumeListener = new THIABPurchaseConsumeHandler()
        {
            @Override public void handlePurchaseConsumeException(int requestCode, IABException exception)
            {
                THToast.show("There are some purchases that could not be consumed");
                THLog.e(TAG, "Could not consume a purchase", exception);
            }

            @Override public void handlePurchaseConsumed(int requestCode, SKUPurchase purchase)
            {
                THToast.show(purchase.getProductIdentifier().identifier + " has been successfully consumed");
            }
        };

        PurchaseReportedHandler reportedListener = new PurchaseReportedHandler()
        {
            @Override public void handlePurchaseReported(int requestCode, SKUPurchase purchase, UserProfileDTO userProfileDTO)
            {
                launchConsumeSequence(consumeListener, purchase);
            }

            @Override public void handlePurchaseReportFailed(int requestCode, Throwable exception)
            {
                THToast.show("A purchase could not be been reported");
                THLog.e(TAG, "A purchase could not be reported", exception);
            }
        };

        for (SKUPurchase purchase : purchases.values())
        {
            THLog.d(TAG, "Purchasing " + purchase);
            // launchReportSequence(reportedListener, purchase);
            break;
        }

    }

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
}
