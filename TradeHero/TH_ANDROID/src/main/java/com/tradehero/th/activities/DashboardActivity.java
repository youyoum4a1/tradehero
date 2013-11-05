package com.tradehero.th.activities;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.GooglePurchase;
import com.tradehero.common.billing.googleplay.IABBadResponseException;
import com.tradehero.common.billing.googleplay.IABException;
import com.tradehero.common.billing.googleplay.IABResponse;
import com.tradehero.common.billing.googleplay.IABServiceConnector;
import com.tradehero.common.billing.googleplay.IABVerificationFailedException;
import com.tradehero.common.billing.googleplay.PurchaseFetcher;
import com.tradehero.common.billing.googleplay.SKU;
import com.tradehero.th.billing.googleplay.SKUFetcher;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.DashboardNavigator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends SherlockFragmentActivity
    implements DashboardNavigatorActivity, SKUFetcher.SKUFetcherListener,
        PurchaseFetcher.PublicFetcherListener, IABServiceConnector.ConnectorListener
{
    public static final String TAG = DashboardActivity.class.getSimpleName();

    private DashboardNavigator navigator;
    private SKUFetcher skuFetcher;
    private PurchaseFetcher purchaseFetcher;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_with_bottom_bar);
        navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);

        skuFetcher = new SKUFetcher();
        skuFetcher.setListener(this);
        skuFetcher.fetchSkus();
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
        purchaseFetcher = new PurchaseFetcher(this, mixedSKUs);
        purchaseFetcher.setFetchListener(this);
        purchaseFetcher.startConnectionSetup();
    }
    //</editor-fold>

    //<editor-fold desc="IABServiceConnector.ConnectorListener">
    @Override public void onSetupFinished(IABServiceConnector connector, IABResponse response)
    {
        // TODO
    }

    @Override public void onSetupFailed(IABServiceConnector connector, IABException exception)
    {
        // TODO
    }
    //</editor-fold>

    //<editor-fold desc="PurchaseFetcher.PublicFetcherListener">
    @Override public void onFetchPurchasesFailed(PurchaseFetcher fetcher, IABException exception)
    {
        if (exception instanceof IABVerificationFailedException)
        {
            THToast.show("The communication with Google Play may have been tampered with");
        }
        else if (exception instanceof IABBadResponseException)
        {
            THToast.show("Google Play returned unexpected information");
        }
        else
        {
            THToast.show("There was some error communicating with Google Play");
        }
    }

    @Override public void onFetchedPurchases(PurchaseFetcher fetcher, Map<SKU, GooglePurchase> purchases)
    {
        if (purchases != null && purchases.size() > 0)
        {
            THToast.show("There are some purchases to be consumed");
        }
    }
    //</editor-fold>
}
