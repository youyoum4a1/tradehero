package com.tradehero.th.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaser;
import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.InventoryFetcher;
import com.tradehero.common.billing.googleplay.exceptions.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exceptions.IABBillingUnavailableException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABRemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import com.tradehero.common.billing.googleplay.PurchaseFetcher;
import com.tradehero.common.billing.googleplay.SKU;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.billing.googleplay.SKUDetailsPurchaser;
import com.tradehero.th.billing.googleplay.THInventoryFetcher;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.billing.googleplay.THSKUDetailsTuner;
import com.tradehero.th.billing.googleplay.SKUFetcher;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.DashboardNavigator;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends SherlockFragmentActivity
    implements DashboardNavigatorActivity, SKUFetcher.SKUFetcherListener,
        PurchaseFetcher.PublicFetcherListener,
        InventoryFetcher.InventoryListener<THInventoryFetcher, THSKUDetails>,
        IABPurchaser.OnIABPurchaseFinishedListener
{
    public static final String TAG = DashboardActivity.class.getSimpleName();

    private DashboardNavigator navigator;
    private SKUFetcher skuFetcher;
    private THSKUDetailsTuner thSKUDetailsTuner;
    private PurchaseFetcher purchaseFetcher;
    private THInventoryFetcher inventoryFetcher;
    private SKUDetailsPurchaser skuDetailsPurchaser;

    private Exception latestSkuFetcherException;
    private Exception latestInventoryFetcherException;
    private Exception latestPurchaseFetcherException;
    private Exception latestPurchaserException;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_with_bottom_bar);
        navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);

        thSKUDetailsTuner = new THSKUDetailsTuner();

        launchSkuInventorySequence();
        launchFetchPurchasesSequence();
    }

    @Override public void onBackPressed()
    {
        //super.onBackPressed();
        navigator.popFragment();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // required for fragment onOptionItemSelected to be called
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onResume()
    {
        THLog.d(TAG, "onResume");
        super.onResume();
    }

    @Override protected void onPause()
    {
        THLog.d(TAG, "onPause");
        super.onPause();
    }

    @Override protected void onDestroy()
    {
        THLog.d(TAG, "onDestroy");
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

        if (skuDetailsPurchaser != null)
        {
            skuDetailsPurchaser.setListener(null);
            skuDetailsPurchaser.setPurchaseFinishedListener(null);
            skuDetailsPurchaser.dispose();
        }
        skuDetailsPurchaser = null;

        super.onDestroy();
    }

    //<editor-fold desc="DashboardNavigatorActivity">
    @Override public Navigator getNavigator()
    {
        return navigator;
    }

    @Override public DashboardNavigator getDashboardNavigator()
    {
        return navigator;
    }
    //</editor-fold>

    public void launchSkuInventorySequence()
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

    @Override public void onFetchedSKUs(SKUFetcher fetcher, Map<String, List<SKU>> availableSkus)
    {
        if (fetcher == this.skuFetcher)
        {
            List<SKU> mixedSKUs = availableSkus.get(Constants.ITEM_TYPE_INAPP);
            if (availableSkus.containsKey(Constants.ITEM_TYPE_SUBS))
            {
                mixedSKUs.addAll(availableSkus.get(Constants.ITEM_TYPE_SUBS));
            }
            latestInventoryFetcherException = null;
            inventoryFetcher = new THInventoryFetcher(this, mixedSKUs);
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
        purchaseFetcher = new PurchaseFetcher(this);
        purchaseFetcher.setFetchListener(this);
        purchaseFetcher.startConnectionSetup();
    }

    //<editor-fold desc="PurchaseFetcher.PublicFetcherListener">
    @Override public void onFetchPurchasesFailed(PurchaseFetcher fetcher, IABException exception)
    {
        if (fetcher == this.purchaseFetcher)
        {
            latestPurchaseFetcherException = exception;
            handleException(exception);
        }
        else
        {
            THLog.e(TAG, "We have received a callback from another purchaseFetcher", exception);
        }
    }

    @Override public void onFetchedPurchases(PurchaseFetcher fetcher, Map<SKU, IABPurchase> purchases)
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
    @Override public void onInventoryFetchSuccess(THInventoryFetcher fetcher, Map<SKU, THSKUDetails> inventory)
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
            handleException(exception);
        }
        else
        {
            THLog.e(TAG, "We have received a callback from another inventoryFetcher", exception);
        }
    }
    //</editor-fold>

    //<editor-fold desc="IABPurchaser.OnIABPurchaseFinishedListener">
    @Override public void onIABPurchaseFinished(IABPurchaser purchaser, IABPurchase info)
    {
        // TODO
        if (purchaser == this.skuDetailsPurchaser)
        {
            THToast.show("Purchase went through ok");
            THLog.d(TAG, "Purchase info " + info);
        }
        else
        {
            THLog.w(TAG, "We have received a callback from another purchaser");
        }
    }

    @Override public void onIABPurchaseFailed(IABPurchaser purchaser, IABException exception)
    {
        if (purchaser == this.skuDetailsPurchaser)
        {
            latestPurchaserException = exception;
            handleException(exception);
        }
        else
        {
            THLog.e(TAG, "We have received a callback from another purchaser", exception);
        }
    }
    //</editor-fold>

    public boolean isBillingAvailable()
    {
        return latestInventoryFetcherException == null || !(latestInventoryFetcherException instanceof IABBillingUnavailableException);
    }

    public boolean hadErrorLoadingInventory()
    {
        return latestInventoryFetcherException != null && !(latestInventoryFetcherException instanceof IABBillingUnavailableException);
    }

    public boolean isInventoryReady()
    {
        return inventoryFetcher != null && inventoryFetcher.getInventory() != null && inventoryFetcher.getInventory().size() > 0;
    }

    protected void handleException(IABException exception)
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
        else
        {
            THToast.show("There was some error communicating with Google Play");
        }
    }

    public List<THSKUDetails> getDetailsOfDomain(String domain)
    {
        List<THSKUDetails> details = null;
        if (inventoryFetcher != null && inventoryFetcher.getInventory() != null)
        {
            details = ArrayUtils.filter(inventoryFetcher.getInventory().values(), THSKUDetails.getPredicateIsOfCertainDomain(domain));
        }
        return details;
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

    public void launchPurchaseSequence(THSKUDetails skuDetails, String extraData)
    {
        if (skuDetailsPurchaser != null)
        {
            skuDetailsPurchaser.setListener(null);
            skuDetailsPurchaser.setPurchaseFinishedListener(null);
        }
        skuDetailsPurchaser = new SKUDetailsPurchaser(this);
        skuDetailsPurchaser.setPurchaseFinishedListener(this);
        skuDetailsPurchaser.purchase(skuDetails, extraData, (int) (Math.random() * Integer.MAX_VALUE));
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.skuDetailsPurchaser != null)
        {
            this.skuDetailsPurchaser.handleActivityResult(requestCode, resultCode, data);
        }
    }
}
