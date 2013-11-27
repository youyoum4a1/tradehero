package com.tradehero.th.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.billing.BasePurchaseReporter;
import com.tradehero.th.billing.googleplay.IABSKUFetcher;
import com.tradehero.th.billing.googleplay.PurchaseRestorer;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABActorUser;
import com.tradehero.th.billing.googleplay.THIABLogicHolderExtended;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.PurchaseRestorerAlertUtil;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import javax.inject.Inject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DashboardActivity extends SherlockFragmentActivity
        implements DashboardNavigatorActivity, THIABActorUser
{
    public static final String TAG = DashboardActivity.class.getSimpleName();
    public static final String EXTRA_FRAGMENT = DashboardActivity.class.getName() + ".fragment";

    private DashboardNavigator navigator;
    private THIABLogicHolderExtended thiabLogicHolderExtended;
    private PurchaseRestorer purchaseRestorer;
    private PurchaseRestorer.OnPurchaseRestorerFinishedListener purchaseRestorerFinishedListener;
    @Inject CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
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
        purchaseRestorer = new PurchaseRestorer(this,
                getBillingActor(),
                getBillingActor(),
                getBillingActor(),
                getBillingActor(),
                currentUserBaseKeyHolder.getCurrentUserBaseKey());
        purchaseRestorerFinishedListener = new PurchaseRestorer.OnPurchaseRestorerFinishedListener()
        {
            @Override
            public void onPurchaseRestoreFinished(List<SKUPurchase> consumed, List<SKUPurchase> reportFailed, List<SKUPurchase> consumeFailed)
            {
                THLog.d(TAG, "onPurchaseRestoreFinished3");
                PurchaseRestorerAlertUtil.handlePurchaseRestoreFinished(
                        DashboardActivity.this,
                        consumed,
                        reportFailed,
                        consumeFailed,
                        createFailedRestoreClickListener(new Exception())); // TODO have a better exception
            }

            @Override public void onPurchaseRestoreFinished(List<SKUPurchase> consumed, List<SKUPurchase> consumeFailed)
            {
                THLog.d(TAG, "onPurchaseRestoreFinished2");
            }

            @Override public void onPurchaseRestoreFailed(Throwable throwable)
            {
                THLog.d(TAG, "onPurchaseRestoreFailed");
                // We keep silent on this one as we don't want to bother the user if for instance billing is not available
                // On the other hand, the settings fragment will inform
            }
        };
        purchaseRestorer.setFinishedListener(purchaseRestorerFinishedListener);
        purchaseRestorer.init();
        purchaseRestorer.launchRestorePurchaseSequence();

        // TODO fetch more stuff?
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

    @Override public void setBillingActor(THIABActor billingActor)
    {
        throw new NotImplementedException(); // You should not use this method
    }

    @Override public THIABActor getBillingActor()
    {
        return thiabLogicHolderExtended;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Passing it on just in case it is expecting something
        thiabLogicHolderExtended.onActivityResult(requestCode, resultCode, data);
    }

    protected DialogInterface.OnClickListener createFailedRestoreClickListener(final Exception exception)
    {
        return new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                PurchaseRestorerAlertUtil.sendSupportEmailRestoreFailed(DashboardActivity.this, exception);
            }
        };
    }
}
