package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.billing.googleplay.THIABLogicHolder;
import com.tradehero.th.billing.googleplay.THIABInteractor;
import com.tradehero.th.billing.googleplay.THIABLogicHolderFull;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseRestorer;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.PurchaseRestorerAlertUtil;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.models.intent.THIntentFactory;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.FacebookUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class DashboardActivity extends SherlockFragmentActivity
        implements DashboardNavigatorActivity, THIABInteractor
{
    private DashboardNavigator navigator;

    // It is important to have Lazy here because we set the current Activity after the injection
    // and the LogicHolder creator needs the current Activity...
    @Inject protected Lazy<THIABLogicHolderFull> thiabLogicHolder;

    private THIABPurchaseRestorer purchaseRestorer;
    private THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener purchaseRestorerFinishedListener;

    @Inject protected Lazy<FacebookUtils> facebookUtils;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<THIntentFactory> thIntentFactory;
    @Inject DTOCacheUtil dtoCacheUtil;
    @Inject PurchaseRestorerAlertUtil purchaseRestorerAlertUtil;
    @Inject CurrentActivityHolder currentActivityHolder;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        currentActivityHolder.setCurrentActivity(this);

        if (Constants.RELEASE)
        {
            Crashlytics.setUserIdentifier("" + currentUserId.get());
        }

        setContentView(R.layout.dashboard_with_bottom_bar);

        launchIAB();

        this.dtoCacheUtil.initialPrefetches();
    }

    private void launchIAB()
    {
        purchaseRestorer = new THIABPurchaseRestorer(this,
                getBillingLogicHolder(),
                getBillingLogicHolder(),
                getBillingLogicHolder(),
                getBillingLogicHolder());
        purchaseRestorerFinishedListener = new THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener()
        {
            @Override
            public void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> reportFailed, List<THIABPurchase> consumeFailed)
            {
                purchaseRestorerAlertUtil.handlePurchaseRestoreFinished(
                        DashboardActivity.this,
                        consumed,
                        reportFailed,
                        consumeFailed,
                        purchaseRestorerAlertUtil.createFailedRestoreClickListener(DashboardActivity.this, new Exception())); // TODO have a better exception
            }

            @Override public void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> consumeFailed)
            {
            }

            @Override public void onPurchaseRestoreFailed(Throwable throwable)
            {
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
        getNavigator().popFragment();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        UserProfileDTO currentUserProfile = userProfileCache.get().get(currentUserId.toUserBaseKey());
        if (currentUserProfile != null && currentUserProfile.isAdmin)
        {
            getSupportMenuInflater().inflate(R.menu.admin_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // required for fragment onOptionItemSelected to be called
        switch (item.getItemId())
        {
            case R.id.admin_settings:
                getDashboardNavigator().pushFragment(AdminSettingsFragment.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onResume()
    {
        if (navigator == null)
        {
            navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);
        }

        launchActions();

        super.onResume();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_MENU:
                Fragment currentDashboardFragment = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
                if (!(currentDashboardFragment instanceof SettingsFragment))
                {
                    getNavigator().openSettings();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override protected void onDestroy()
    {
        if (thiabLogicHolder != null)
        {
            thiabLogicHolder.get().onDestroy();
        }
        thiabLogicHolder = null;
        if (navigator != null)
        {
            navigator.onDestroy();
        }
        navigator = null;

        if (currentActivityHolder != null)
        {
            currentActivityHolder.unsetActivity(this);
        }
        super.onDestroy();
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
            case Intent.ACTION_MAIN:
                if (thIntentFactory.get().isHandlableIntent(intent))
                {
                    getDashboardNavigator().goToPage(thIntentFactory.get().create(intent));
                }
                break;
        }
        Timber.d(getIntent().getAction());
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

    @Override public void setBillingLogicHolder(THIABLogicHolder billingActor)
    {
        throw new UnsupportedOperationException("Not implemented"); // You should not use this method
    }

    @Override public THIABLogicHolder getBillingLogicHolder()
    {
        return thiabLogicHolder.get();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        facebookUtils.get().finishAuthentication(requestCode, resultCode, data);
        // Passing it on just in case it is expecting something
        thiabLogicHolder.get().onActivityResult(requestCode, resultCode, data);
    }
}
