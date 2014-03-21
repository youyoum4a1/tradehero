package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.billing.BillingPurchaseRestorer;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.googleplay.THIABPurchaseRestorerAlertUtil;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.settings.AboutFragment;
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
import javax.inject.Provider;
import timber.log.Timber;

public class DashboardActivity extends SherlockFragmentActivity
        implements DashboardNavigatorActivity
{
    private DashboardNavigator navigator;

    // It is important to have Lazy here because we set the current Activity after the injection
    // and the LogicHolder creator needs the current Activity...
    @Inject protected Lazy<THBillingInteractor> billingInteractor;
    @Inject protected Provider<THUIBillingRequest> emptyBillingRequestProvider;

    private BillingPurchaseRestorer.OnPurchaseRestorerListener purchaseRestorerFinishedListener;
    private Integer restoreRequestCode;

    @Inject protected Lazy<FacebookUtils> facebookUtils;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<THIntentFactory> thIntentFactory;
    @Inject DTOCacheUtil dtoCacheUtil;
    @Inject THIABPurchaseRestorerAlertUtil IABPurchaseRestorerAlertUtil;
    @Inject CurrentActivityHolder currentActivityHolder;
    @Inject Lazy<LocalyticsSession> localyticsSession;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // request the progress-bar feature for the activity
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        DaggerUtils.inject(this);
        currentActivityHolder.setCurrentActivity(this);

        if (Constants.RELEASE)
        {
            Crashlytics.setUserIdentifier("" + currentUserId.get());
        }

        setContentView(R.layout.dashboard_with_bottom_bar);

        purchaseRestorerFinishedListener = new BillingPurchaseRestorer.OnPurchaseRestorerListener()
        {
            @Override public void onPurchaseRestored(
                    int requestCode,
                    List restoredPurchases,
                    List failedRestorePurchases,
                    List failExceptions)
            {
                if (Integer.valueOf(requestCode).equals(restoreRequestCode))
                {
                    restoreRequestCode = null;
                }
            }
        };
        launchBilling();

        this.dtoCacheUtil.initialPrefetches();
    }

    private void launchBilling()
    {
        if (restoreRequestCode != null)
        {
            billingInteractor.get().forgetRequestCode(restoreRequestCode);
        }
        restoreRequestCode = billingInteractor.get().run(createRestoreRequest());
        // TODO fetch more stuff?
    }

    protected THUIBillingRequest createRestoreRequest()
    {
        THUIBillingRequest request = emptyBillingRequestProvider.get();
        request.restorePurchase = true;
        request.startWithProgressDialog = false;
        request.popRestorePurchaseOutcome = true;
        request.popRestorePurchaseOutcomeVerbose = false;
        request.purchaseRestorerListener = purchaseRestorerFinishedListener;
        return request;
    }

    protected THUIBillingRequest createFetchInventoryRequest()
    {
        THUIBillingRequest request = emptyBillingRequestProvider.get();
        request.fetchInventory = true;
        return request;
    }

    @Override public void onBackPressed()
    {
        getNavigator().popFragment();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        UserProfileDTO currentUserProfile = userProfileCache.get().get(currentUserId.toUserBaseKey());
        MenuInflater menuInflater = getSupportMenuInflater();

        menuInflater.inflate(R.menu.hardware_menu, menu);

        if (currentUserProfile != null)
        {
            if (currentUserProfile.isAdmin)
            {
                menuInflater.inflate(R.menu.admin_menu, menu);
            }
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
                return true;
            case R.id.hardware_menu_settings:
                pushFragmentIfNecessary(SettingsFragment.class);
                return true;
            case R.id.hardware_menu_about:
                pushFragmentIfNecessary(AboutFragment.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pushFragmentIfNecessary(Class<? extends Fragment> fragmentClass)
    {
        Fragment currentDashboardFragment = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
        if (!(fragmentClass.isInstance(currentDashboardFragment)))
        {
            getNavigator().pushFragment(fragmentClass);
        }
    }

    @Override protected void onResume()
    {
        super.onResume();

        if (navigator == null)
        {
            navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);
        }

        launchActions();

        localyticsSession.get().open();
    }

    @Override protected void onPause()
    {
        localyticsSession.get().close();
        localyticsSession.get().upload();

        super.onPause();
    }

    @Override protected void onDestroy()
    {
        billingInteractor.get().forgetRequestCode(restoreRequestCode);
        billingInteractor = null;
        if (navigator != null)
        {
            navigator.onDestroy();
        }
        navigator = null;

        if (currentActivityHolder != null)
        {
            currentActivityHolder.unsetActivity(this);
        }
        purchaseRestorerFinishedListener = null;

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

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        facebookUtils.get().finishAuthentication(requestCode, resultCode, data);
        // Passing it on just in case it is expecting something
        billingInteractor.get().onActivityResult(requestCode, resultCode, data);
    }
}
