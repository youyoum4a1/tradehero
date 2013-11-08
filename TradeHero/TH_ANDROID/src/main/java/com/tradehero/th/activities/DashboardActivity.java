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
import com.tradehero.common.billing.googleplay.exceptions.IABUserCancelledException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import com.tradehero.common.billing.googleplay.PurchaseFetcher;
import com.tradehero.common.billing.googleplay.SKU;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.billing.googleplay.IABAlertUtils;
import com.tradehero.th.billing.googleplay.SKUDetailsPurchaser;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABLogicHolder;
import com.tradehero.th.billing.googleplay.THIABPurchaseHandler;
import com.tradehero.th.billing.googleplay.THInventoryFetcher;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.billing.googleplay.THSKUDetailsTuner;
import com.tradehero.th.billing.googleplay.SKUFetcher;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.DashboardNavigator;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends SherlockFragmentActivity implements DashboardNavigatorActivity, THIABActor
{
    public static final String TAG = DashboardActivity.class.getSimpleName();

    private DashboardNavigator navigator;
    private THIABLogicHolder thiabLogicHolder;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_with_bottom_bar);
        navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);

        thiabLogicHolder = new THIABLogicHolder(this);
        launchSkuInventorySequence();
        thiabLogicHolder.launchFetchPurchasesSequence();
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
        if (thiabLogicHolder != null)
        {
            thiabLogicHolder.onDestroy();
        }
        thiabLogicHolder = null;
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

    //<editor-fold desc="THIABActor">
    @Override public void launchSkuInventorySequence()
    {
        thiabLogicHolder.launchSkuInventorySequence();
    }

    @Override public boolean isBillingAvailable()
    {
        return thiabLogicHolder.isBillingAvailable();
    }

    @Override public boolean hadErrorLoadingInventory()
    {
        return thiabLogicHolder.hadErrorLoadingInventory();
    }

    @Override public boolean isInventoryReady()
    {
        return thiabLogicHolder.isInventoryReady();
    }

    @Override public List<THSKUDetails> getDetailsOfDomain(String domain)
    {
        return thiabLogicHolder.getDetailsOfDomain(domain);
    }

    @Override public int launchPurchaseSequence(THIABPurchaseHandler purchaseHandler, THSKUDetails skuDetails)
    {
        return thiabLogicHolder.launchPurchaseSequence(purchaseHandler, skuDetails);
    }

    @Override public int launchPurchaseSequence(THIABPurchaseHandler purchaseHandler, THSKUDetails skuDetails, Object extraData)
    {
        return thiabLogicHolder.launchPurchaseSequence(purchaseHandler, skuDetails, extraData);
    }

    @Override public int launchPurchaseSequence(THIABPurchaseHandler purchaseHandler, THSKUDetails skuDetails, String extraData)
    {
        return thiabLogicHolder.launchPurchaseSequence(purchaseHandler, skuDetails, extraData);
    }
    //</editor-fold>

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Passing it on just in case it is expecting something
        thiabLogicHolder.onActivityResult(requestCode, resultCode, data);
    }
}
