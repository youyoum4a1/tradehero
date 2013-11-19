package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.billing.PurchaseReportedHandler;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABLogicHolderExtended;
import com.tradehero.th.billing.googleplay.THIABPurchaseConsumeHandler;
import com.tradehero.th.billing.googleplay.THIABPurchaseHandler;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.fragments.DashboardNavigator;
import java.util.List;

public class DashboardActivity extends SherlockFragmentActivity
        implements DashboardNavigatorActivity, THIABActor
{
    public static final String TAG = DashboardActivity.class.getSimpleName();
    public static final String EXTRA_FRAGMENT = DashboardActivity.class.getName() + ".fragment";

    private DashboardNavigator navigator;
    private THIABLogicHolderExtended thiabLogicHolderExtended;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_with_bottom_bar);

        launchIAB();

        THLog.d(TAG, "onCreate");
    }

    private void launchActions()
    {
        Intent intent = getIntent();
        if (intent == null || intent.getAction() == null)
        {
            return;
        }
        switch (intent.getAction())
        {
            case Intent.ACTION_VIEW:
                if (intent.hasExtra(EXTRA_FRAGMENT))
                {
                    switchFragment(intent.getExtras());
                }
                break;
        }
        THLog.d(TAG, getIntent().getAction());
    }

    private void switchFragment(Bundle extras)
    {
        try
        {
            Class fragmentClass = Class.forName(extras.getString(EXTRA_FRAGMENT));
            navigator.pushFragment(fragmentClass, extras);
        }
        catch (ClassNotFoundException e)
        {
            THLog.d(TAG, "Fragment not found");
        }
    }

    private void launchIAB()
    {
        thiabLogicHolderExtended = new THIABLogicHolderExtended(this);
        launchSkuInventorySequence();
        thiabLogicHolderExtended.launchFetchPurchasesSequence();
    }

    @Override public void onBackPressed()
    {
        navigator.popFragment();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // required for fragment onOptionItemSelected to be called
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onResume()
    {
        if (navigator == null)
        {
            navigator = new DashboardNavigator(
                this, getSupportFragmentManager(), R.id.realtabcontent, getIntent() == null || !getIntent().hasExtra(EXTRA_FRAGMENT));
        }

        launchActions();

        super.onResume();
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

    @Override public int launchPurchaseSequence(THIABPurchaseHandler purchaseHandler, THIABPurchaseOrder purchaseOrder)
    {
        return thiabLogicHolderExtended.launchPurchaseSequence(purchaseHandler, purchaseOrder);
    }

    @Override public int launchConsumeSequence(THIABPurchaseConsumeHandler purchaseConsumeHandler, SKUPurchase purchase)
    {
        return thiabLogicHolderExtended.launchConsumeSequence(purchaseConsumeHandler, purchase);
    }

    @Override public int launchReportSequence(PurchaseReportedHandler purchaseReportedHandler, SKUPurchase purchase, THSKUDetails skuDetails)
    {
        return thiabLogicHolderExtended.launchReportSequence(purchaseReportedHandler, purchase, skuDetails);
    }

    @Override public UserProfileDTO launchReportSequenceSync(SKUPurchase purchase, THSKUDetails skuDetails)
    {
        return thiabLogicHolderExtended.launchReportSequenceSync(purchase, skuDetails);
    }
    //</editor-fold>

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Passing it on just in case it is expecting something
        thiabLogicHolderExtended.onActivityResult(requestCode, resultCode, data);
    }
}
