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
import com.tradehero.common.billing.googleplay.IABResponse;
import com.tradehero.common.billing.googleplay.IABServiceConnector;
import com.tradehero.common.billing.googleplay.exceptions.IABRemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import com.tradehero.common.billing.googleplay.PurchaseFetcher;
import com.tradehero.common.billing.googleplay.SKU;
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
        PurchaseFetcher.PublicFetcherListener, InventoryFetcher.InventoryListener
{
    public static final String TAG = DashboardActivity.class.getSimpleName();

    private DashboardNavigator navigator;
    private SKUFetcher skuFetcher;
    private PurchaseFetcher purchaseFetcher;
    private InventoryFetcher inventoryFetcher;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_with_bottom_bar);
        navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);

        skuFetcher = new SKUFetcher();
        skuFetcher.setListener(this);
        skuFetcher.fetchSkus();

        purchaseFetcher = new PurchaseFetcher(this);
        purchaseFetcher.setFetchListener(this);
        purchaseFetcher.startConnectionSetup();
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

    //<editor-fold desc="SKUFetcher.SKUFetcherListener">
    @Override public void onFetchSKUsFailed(SKUFetcher fetcher, Exception exception)
    {
        THToast.show("There was an error fetching the list of SKUs");
    }

    @Override public void onFetchedSKUs(SKUFetcher fetcher, Map<String, List<SKU>> availableSkus)
    {
        List<SKU> mixedSKUs = availableSkus.get(Constants.ITEM_TYPE_INAPP);
        if (availableSkus.containsKey(Constants.ITEM_TYPE_SUBS))
        {
            mixedSKUs.addAll(availableSkus.get(Constants.ITEM_TYPE_SUBS));
        }
        inventoryFetcher = new InventoryFetcher(this, mixedSKUs);
        inventoryFetcher.setInventoryListener(this);
        inventoryFetcher.startConnectionSetup();
    }
    //</editor-fold>

    //<editor-fold desc="PurchaseFetcher.PublicFetcherListener">
    @Override public void onFetchPurchasesFailed(PurchaseFetcher fetcher, IABException exception)
    {
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
    @Override public void onInventoryFetchSuccess(InventoryFetcher fetcher, Map<SKU, SKUDetails> inventory)
    {
        THToast.show("Inventory successfully fetched");
    }

    @Override public void onInventoryFetchFail(InventoryFetcher fetcher, IABException exception)
    {
        handleException(exception);
    }
    //</editor-fold>

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
}
