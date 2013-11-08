package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABLogicHolderExtended;
import com.tradehero.th.billing.googleplay.THIABPurchaseHandler;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.DashboardNavigator;
import java.util.List;

public class DashboardActivity extends SherlockFragmentActivity implements DashboardNavigatorActivity, THIABActor
{
    public static final String TAG = DashboardActivity.class.getSimpleName();

    private DashboardNavigator navigator;
    private THIABLogicHolderExtended thiabLogicHolderExtended;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_with_bottom_bar);
        navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);

        thiabLogicHolderExtended = new THIABLogicHolderExtended(this);
        launchSkuInventorySequence();
        thiabLogicHolderExtended.launchFetchPurchasesSequence();
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
        if (thiabLogicHolderExtended != null)
        {
            thiabLogicHolderExtended.onDestroy();
        }
        thiabLogicHolderExtended = null;
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
        thiabLogicHolderExtended.launchSkuInventorySequence();
    }

    @Override public boolean isBillingAvailable()
    {
        return thiabLogicHolderExtended.isBillingAvailable();
    }

    @Override public boolean hadErrorLoadingInventory()
    {
        return thiabLogicHolderExtended.hadErrorLoadingInventory();
    }

    @Override public boolean isInventoryReady()
    {
        return thiabLogicHolderExtended.isInventoryReady();
    }

    @Override public List<THSKUDetails> getDetailsOfDomain(String domain)
    {
        return thiabLogicHolderExtended.getDetailsOfDomain(domain);
    }

    @Override public int launchPurchaseSequence(THIABPurchaseHandler purchaseHandler, THSKUDetails skuDetails)
    {
        return thiabLogicHolderExtended.launchPurchaseSequence(purchaseHandler, skuDetails);
    }

    @Override public int launchPurchaseSequence(THIABPurchaseHandler purchaseHandler, THSKUDetails skuDetails, Object extraData)
    {
        return thiabLogicHolderExtended.launchPurchaseSequence(purchaseHandler, skuDetails, extraData);
    }

    @Override public int launchPurchaseSequence(THIABPurchaseHandler purchaseHandler, THSKUDetails skuDetails, String extraData)
    {
        return thiabLogicHolderExtended.launchPurchaseSequence(purchaseHandler, skuDetails, extraData);
    }
    //</editor-fold>

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Passing it on just in case it is expecting something
        thiabLogicHolderExtended.onActivityResult(requestCode, resultCode, data);
    }
}
