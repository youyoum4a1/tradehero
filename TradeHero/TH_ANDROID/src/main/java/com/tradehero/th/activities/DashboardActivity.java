package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.view.Window;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.billing.googleplay.THIABPurchaseRestorerAlertUtil;
import com.tradehero.th.billing.googleplay.THIABLogicHolder;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseRestorer;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.settings.AboutFragment;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.models.intent.THIntentFactory;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.ui.AppContainer;
import com.tradehero.th.ui.ViewWrapper;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.FacebookUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class DashboardActivity extends SherlockFragmentActivity
        implements DashboardNavigatorActivity
{
    private DashboardNavigator navigator;

    // It is important to have Lazy here because we set the current Activity after the injection
    // and the LogicHolder creator needs the current Activity...
    // TODO BillingLogicHolder
    @Inject protected Lazy<THIABLogicHolder> billingLogicHolder;

    private THIABPurchaseRestorer purchaseRestorer;
    private THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener purchaseRestorerFinishedListener;

    @Inject Lazy<FacebookUtils> facebookUtils;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<THIntentFactory> thIntentFactory;
    @Inject CurrentUserId currentUserId;
    @Inject DTOCacheUtil dtoCacheUtil;
    @Inject THIABPurchaseRestorerAlertUtil IABPurchaseRestorerAlertUtil;
    @Inject CurrentActivityHolder currentActivityHolder;
    @Inject AppContainer appContainer;
    @Inject ViewWrapper slideMenuContainer;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        DaggerUtils.inject(this);

        currentActivityHolder.setCurrentActivity(this);

        if (Constants.RELEASE)
        {
            Crashlytics.setUserIdentifier("" + currentUserId.get());
        }

        // wrap main view inside a container, this container can be generic, which adds in view components like sidebar, slide-in widget ...
        ViewGroup dashboardWrapper = appContainer.get(this);
        ViewGroup slideMenuWrapper = slideMenuContainer.get(dashboardWrapper);
        getLayoutInflater().inflate(R.layout.dashboard_with_bottom_bar, slideMenuWrapper);

        launchIAB();

        dtoCacheUtil.initialPrefetches();
    }

    private void launchIAB()
    {
        purchaseRestorer = new THIABPurchaseRestorer(billingLogicHolder.get());
        purchaseRestorerFinishedListener = new THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener()
        {
            @Override
            public void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> reportFailed, List<THIABPurchase> consumeFailed)
            {
                IABPurchaseRestorerAlertUtil.handlePurchaseRestoreFinished(
                        DashboardActivity.this,
                        consumed,
                        reportFailed,
                        consumeFailed,
                        IABPurchaseRestorerAlertUtil.createFailedRestoreClickListener(DashboardActivity.this, new Exception("Tracing"))); // TODO have a better exception
            }

            @Override public void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> consumeFailed)
            {
            }

            @Override public void onPurchaseRestoreFailed(IABException iabException)
            {
                // We keep silent on this one as we don't want to bother the user if for instance billing is not available
                // On the other hand, the settings fragment will inform
            }
        };
        purchaseRestorer.setPurchaseRestoreFinishedListener(purchaseRestorerFinishedListener);
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
        if (navigator == null)
        {
            navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);
        }

        launchActions();

        super.onResume();
    }

    @Override protected void onDestroy()
    {
        billingLogicHolder = null;
        if (navigator != null)
        {
            navigator.onDestroy();
        }
        navigator = null;

        if (currentActivityHolder != null)
        {
            currentActivityHolder.unsetActivity(this);
        }
        if (purchaseRestorer != null)
        {
            purchaseRestorer.setPurchaseRestoreFinishedListener(null);
        }
        purchaseRestorer = null;
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
        billingLogicHolder.get().onActivityResult(requestCode, resultCode, data);
    }
}
