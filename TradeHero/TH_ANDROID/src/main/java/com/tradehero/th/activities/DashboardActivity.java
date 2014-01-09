package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABActorUser;
import com.tradehero.th.billing.googleplay.THIABLogicHolder;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseRestorer;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.PurchaseRestorerAlertUtil;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.FacebookUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class DashboardActivity extends SherlockFragmentActivity
        implements DashboardNavigatorActivity, THIABActorUser
{
    public static final String TAG = DashboardActivity.class.getSimpleName();
    public static final String EXTRA_FRAGMENT = DashboardActivity.class.getName() + ".fragment";

    private DashboardNavigator navigator;
    private THIABLogicHolder thiabLogicHolder;
    private THIABPurchaseRestorer purchaseRestorer;
    private THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener purchaseRestorerFinishedListener;

    @Inject protected Lazy<FacebookUtils> facebookUtils;
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
        thiabLogicHolder = new THIABLogicHolder(this);
        purchaseRestorer = new THIABPurchaseRestorer(this,
                getBillingActor(),
                getBillingActor(),
                getBillingActor(),
                getBillingActor());
        purchaseRestorerFinishedListener = new THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener()
        {
            @Override
            public void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> reportFailed, List<THIABPurchase> consumeFailed)
            {
                THLog.d(TAG, "onPurchaseRestoreFinished3");
                PurchaseRestorerAlertUtil.handlePurchaseRestoreFinished(
                        DashboardActivity.this,
                        consumed,
                        reportFailed,
                        consumeFailed,
                        PurchaseRestorerAlertUtil.createFailedRestoreClickListener(DashboardActivity.this, new Exception())); // TODO have a better exception
            }

            @Override public void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> consumeFailed)
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

    @Override public void setBillingActor(THIABActor billingActor)
    {
        throw new UnsupportedOperationException("Not implemented"); // You should not use this method
    }

    @Override public THIABActor getBillingActor()
    {
        return thiabLogicHolder;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        facebookUtils.get().finishAuthentication(requestCode, resultCode, data);
        // Passing it on just in case it is expecting something
        thiabLogicHolder.onActivityResult(requestCode, resultCode, data);
    }
}
