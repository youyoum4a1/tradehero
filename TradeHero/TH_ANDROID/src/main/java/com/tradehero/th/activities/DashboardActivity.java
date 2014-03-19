package com.tradehero.th.activities;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.billing.googleplay.THIABLogicHolder;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseRestorer;
import com.tradehero.th.billing.googleplay.THIABPurchaseRestorerAlertUtil;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.settings.AboutFragment;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.models.intent.THIntentFactory;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
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
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<THIntentFactory> thIntentFactory;
    @Inject DTOCacheUtil dtoCacheUtil;
    @Inject THIABPurchaseRestorerAlertUtil IABPurchaseRestorerAlertUtil;
    @Inject CurrentActivityHolder currentActivityHolder;
    @Inject Lazy<LocalyticsSession> localyticsSession;
    @Inject Lazy<AlertDialogUtil> alertDialogUtil;

    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileFetchTask;
    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileFetchListener;

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

        launchIAB();

        detachUserProfileFetchTask();
        userProfileFetchListener = new UserProfileFetchListener();
        userProfileFetchTask = userProfileCache.get().getOrFetch(currentUserId.toUserBaseKey(), false, userProfileFetchListener);
        userProfileFetchTask.execute();

        suggestUpgradeIfNecessary();
        this.dtoCacheUtil.initialPrefetches();
    }

    private void detachUserProfileFetchTask()
    {
        if (userProfileFetchTask != null)
        {
            userProfileFetchTask.setListener(null);
        }
        userProfileFetchTask = null;
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

    private void suggestUpgradeIfNecessary()
    {
        if (getIntent() != null && getIntent().getBooleanExtra(UserLoginDTO.SUGGEST_UPGRADE, false))
        {
            alertDialogUtil.get().popWithOkCancelButton(
                    this, R.string.upgrade_needed, R.string.suggest_to_upgrade, R.string.update_now, R.string.later,
                    new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialog, int which)
                        {
                            try
                            {
                                THToast.show(R.string.update_guide);
                                startActivity(
                                        new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Constants.PLAYSTORE_APP_ID)));
                            }
                            catch (ActivityNotFoundException ex)
                            {
                                startActivity(
                                        new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("https://play.google.com/store/apps/details?id=" + Constants.PLAYSTORE_APP_ID)));
                            }
                        }
                    });
        }
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

        detachUserProfileFetchTask();
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

    private class UserProfileFetchListener implements DTOCache.Listener<UserBaseKey,UserProfileDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            supportInvalidateOptionsMenu();
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {

        }
    }
}
