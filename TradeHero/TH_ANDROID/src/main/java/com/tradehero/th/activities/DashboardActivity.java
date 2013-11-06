package com.tradehero.th.activities;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.GooglePurchase;
import com.tradehero.common.billing.googleplay.InventoryFetcher;
import com.tradehero.common.billing.googleplay.SKUDetails;
import com.tradehero.common.billing.googleplay.exceptions.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exceptions.IABBillingUnavailableException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABRemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import com.tradehero.common.billing.googleplay.PurchaseFetcher;
import com.tradehero.common.billing.googleplay.SKU;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.th.billing.googleplay.THInventoryFetcher;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.billing.googleplay.THSKUDetailsTuner;
import com.tradehero.th.billing.googleplay.SKUFetcher;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.DashboardNavigator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends SherlockFragmentActivity
    implements DashboardNavigatorActivity, SKUFetcher.SKUFetcherListener,
        PurchaseFetcher.PublicFetcherListener,
        InventoryFetcher.InventoryListener<THInventoryFetcher, THSKUDetails>
{
    public static final String TAG = DashboardActivity.class.getSimpleName();

    private DashboardNavigator navigator;
    private SKUFetcher skuFetcher;
    private THSKUDetailsTuner thSKUDetailsTuner;
    private PurchaseFetcher purchaseFetcher;
    private THInventoryFetcher inventoryFetcher;

    private Exception latestSkuFetcherException;
    private Exception latestInventoryFetcherException;
    private Exception latestPurchaseFetcherException;

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

    @Override protected void onDestroy()
    {
        if (skuFetcher != null)
        {
            skuFetcher.setListener(null);
            skuFetcher.dispose();
        }
        skuFetcher = null;

        if (purchaseFetcher != null)
        {
            purchaseFetcher.setFetchListener(null);
            purchaseFetcher.dispose();
        }
        purchaseFetcher = null;

        if (inventoryFetcher != null)
        {
            inventoryFetcher.setInventoryListener(null);
            inventoryFetcher.setSkuDetailsTuner(null);
            inventoryFetcher.dispose();
        }
        inventoryFetcher = null;
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
        inventoryFetcher = null;
        skuFetcher = new SKUFetcher();
        skuFetcher.setListener(this);
        skuFetcher.fetchSkus();
    }

    //<editor-fold desc="SKUFetcher.SKUFetcherListener">
    @Override public void onFetchSKUsFailed(SKUFetcher fetcher, Exception exception)
    {
        latestSkuFetcherException = exception;
        THToast.show("There was an error fetching the list of SKUs");
    }

    @Override public void onFetchedSKUs(SKUFetcher fetcher, Map<String, List<SKU>> availableSkus)
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
    //</editor-fold>

    public void launchFetchPurchasesSequence()
    {
        latestPurchaseFetcherException = null;
        purchaseFetcher = new PurchaseFetcher(this);
        purchaseFetcher.setFetchListener(this);
        purchaseFetcher.startConnectionSetup();
    }

    //<editor-fold desc="PurchaseFetcher.PublicFetcherListener">
    @Override public void onFetchPurchasesFailed(PurchaseFetcher fetcher, IABException exception)
    {
        latestPurchaseFetcherException = exception;
        handleException(exception);
    }

    @Override public void onFetchedPurchases(PurchaseFetcher fetcher, Map<SKU, GooglePurchase> purchases)
    {
        if (purchases != null && purchases.size() > 0)
        {
            THToast.show("There are some purchases to be consumed");
        }
        else
        {
            THToast.show("There is no purchase waiting to be consumed");
        }
    }
    //</editor-fold>

    //<editor-fold desc="InventoryFetcher.InventoryListener">
    @Override public void onInventoryFetchSuccess(THInventoryFetcher fetcher, Map<SKU, THSKUDetails> inventory)
    {
        THToast.show("Inventory successfully fetched");
    }

    @Override public void onInventoryFetchFail(THInventoryFetcher fetcher, IABException exception)
    {
        latestInventoryFetcherException = exception;
        handleException(exception);
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
}
