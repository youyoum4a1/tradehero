package com.tradehero.th.activities;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.billing.googleplay.GooglePurchase;
import com.tradehero.common.billing.googleplay.IABBadResponseException;
import com.tradehero.common.billing.googleplay.IABException;
import com.tradehero.common.billing.googleplay.IABResponse;
import com.tradehero.common.billing.googleplay.IABServiceConnector;
import com.tradehero.common.billing.googleplay.IABVerificationFailedException;
import com.tradehero.common.billing.googleplay.PurchaseFetcher;
import com.tradehero.common.billing.googleplay.SKU;
import com.tradehero.common.billing.googleplay.SKUFetcher;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.DashboardNavigator;
import java.util.Map;

public class DashboardActivity extends SherlockFragmentActivity
    implements DashboardNavigatorActivity, PurchaseFetcher.PublicFetcherListener, IABServiceConnector.ConnectorListener
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
        skuFetcher.fetchSkus();

        purchaseFetcher = new PurchaseFetcher(this, skuFetcher.getAvailableInAppSkus());
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
