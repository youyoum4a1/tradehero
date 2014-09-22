package com.tradehero.th.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.view.Window;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.crashlytics.android.Crashlytics;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.prefs.FirstShowReferralCodeDialog;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.ui.AppContainer;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.WeiboUtils;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th2.R;
import dagger.Lazy;
import java.util.Date;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

//import com.tradehero.th.utils.FacebookUtils;

public class DashboardActivity extends SherlockFragmentActivity
        implements DashboardNavigatorActivity
        //,ResideMenu.OnMenuListener
{
    // It is important to have Lazy here because we set the current Activity after the injection
    // and the LogicHolder creator needs the current Activity...
    //@Inject Lazy<THBillingInteractor> billingInteractor;
    //@Inject Provider<THUIBillingRequest> emptyBillingRequestProvider;
    //@Inject Lazy<THIntentFactory> thIntentFactory;
    //@Inject THIABPurchaseRestorerAlertUtil IABPurchaseRestorerAlertUtil;
    //private Integer restoreRequestCode;
    //private BillingPurchaseRestorer.OnPurchaseRestorerListener purchaseRestorerFinishedListener;
    //@Inject ViewWrapper slideMenuContainer;
    @Inject AppContainer appContainer;
    //@Inject ResideMenu resideMenu;
    //private DTOCacheNew.HurriedListener<NotificationKey, NotificationDTO> notificationFetchListener;
    //private AlertDialog mReferralCodeDialog;
    //@Inject THRouter thRouter;

    private final DashboardTabType INITIAL_TAB = DashboardTabType.HOME;
    private DashboardNavigator navigator;
    //@Inject Lazy<FacebookUtils> facebookUtils;
    @Inject Lazy<WeiboUtils> weiboUtils;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject CurrentActivityHolder currentActivityHolder;
    @Inject Lazy<AlertDialogUtil> alertDialogUtil;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtil;
    @Inject Lazy<NotificationCache> notificationCache;
    @Inject DeviceTokenHelper deviceTokenHelper;
    @Inject @FirstShowReferralCodeDialog BooleanPreference firstShowReferralCodeDialogPreference;
    @Inject SystemStatusCache systemStatusCache;
    private ProgressDialog progressDialog;
    //@Inject Lazy<PushNotificationManager> pushNotificationManager;
    @Inject Analytics analytics;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        AppTiming.dashboardCreate = System.currentTimeMillis();
        // this need tobe early than super.onCreate or it will crash
        // when device scroll into landscape.
        // request the progress-bar feature for the activity
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);

        DaggerUtils.inject(this);

        currentActivityHolder.setCurrentActivity(this);

        if (Constants.RELEASE)
        {
            Crashlytics.setString(Constants.TH_CLIENT_TYPE,
                    String.format("%s:%d", deviceTokenHelper.getDeviceType(), Constants.TAP_STREAM_TYPE.type));
            Crashlytics.setUserIdentifier("" + currentUserId.get());
        }

        ViewGroup dashboardWrapper = appContainer.get(this);

        //purchaseRestorerFinishedListener = new BillingPurchaseRestorer.OnPurchaseRestorerListener()
        //{
        //    @Override public void onPurchaseRestored(
        //            int requestCode,
        //            List restoredPurchases,
        //            List failedRestorePurchases,
        //            List failExceptions)
        //    {
        //        if (Integer.valueOf(requestCode).equals(restoreRequestCode))
        //        {
        //            restoreRequestCode = null;
        //        }
        //    }
        //};
        //launchBilling();

        detachUserProfileCache();
        userProfileCacheListener = createUserProfileFetchListener();

        //detachNotificationFetchTask();
        //notificationFetchListener = createNotificationFetchListener();

        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());

        //suggestUpgradeIfNecessary();
        //dtoCacheUtil.initialPrefetches();//this will block first initial launch securities list,
        // and this line is no use for it will update after login in prefetchesUponLogin
        //showReferralCodeDialog();

        navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);

        if (savedInstanceState == null && navigator.getCurrentFragment() == null)
        {
            //navigator.goToTab(DashboardTabType.REFERRAL);
            Bundle args = getIntent().getExtras();
            if (args != null)
            {
                String CLASS_NAME = args.getString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME);
                Timber.d("CLASS_NAME = " + CLASS_NAME);
                try
                {
                    Class onwClass = Class.forName(CLASS_NAME);
                    if(onwClass!=null)
                    {
                        navigator.goToFragment(onwClass, args);
                    }
                } catch (Exception e)
                {
                    finish();
                }
            }
            //navigator.goToFragment(FragmentTest01.class,null);
        }

        //if (getIntent() != null)
        //{
        //    processNotificationDataIfPresence(getIntent().getExtras());
        //}
        //TODO need check whether this is ok for urbanship,
        //TODO for baidu, PushManager.startWork can't run in Application.init() for stability, it will run in a circle. by alex
        //pushNotificationManager.get().enablePush();
    }

    private void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    @Override public void onBackPressed()
    {
        getNavigator().popFragment();
    }

    private void pushFragmentIfNecessary(Class<? extends Fragment> fragmentClass)
    {
        Fragment currentDashboardFragment = navigator.getCurrentFragment();
        if (!(fragmentClass.isInstance(currentDashboardFragment)))
        {
            getNavigator().pushFragment(fragmentClass);
        }
    }

    @Override protected void onStart()
    {
        super.onStart();
        systemStatusCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    @Override protected void onResume()
    {
        super.onResume();
        //launchActions();
        analytics.openSession();
    }

    @Override protected void onPause()
    {
        analytics.closeSession();
        super.onPause();
    }

    @Override protected void onDestroy()
    {
        //THBillingInteractor billingInteractorCopy = billingInteractor.get();
        //if (billingInteractorCopy != null && restoreRequestCode != null)
        //{
        //    billingInteractorCopy.forgetRequestCode(restoreRequestCode);
        //}

        if (navigator != null)
        {
            navigator.onDestroy();
        }
        navigator = null;

        if (currentActivityHolder != null)
        {
            currentActivityHolder.unsetActivity(this);
        }
        //purchaseRestorerFinishedListener = null;

        detachUserProfileCache();
        userProfileCacheListener = null;

        //detachNotificationFetchTask();
        //notificationFetchListener = null;

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

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //facebookUtils.get().finishAuthentication(requestCode, resultCode, data);
        // Passing it on just in case it is expecting something
        //billingInteractor.get().onActivityResult(requestCode, resultCode, data);
        weiboUtils.get().authorizeCallBack(requestCode, resultCode, data);
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileFetchListener()
    {
        return new UserProfileFetchListener();
    }

    protected class UserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            supportInvalidateOptionsMenu();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {

        }
    }

    @Override public void onLowMemory()
    {
        super.onLowMemory();

        // TODO remove
        // for DEBUGGING purpose only
        String currentFragmentName =
                getSupportFragmentManager().findFragmentById(R.id.realtabcontent)
                        .getClass()
                        .getName();
        Timber.e(new RuntimeException("LowMemory " + currentFragmentName), "%s",
                currentFragmentName);
        Crashlytics.setString("LowMemoryAt", new Date().toString());
    }

    //@Override protected void onNewIntent(Intent intent)
    //{
    //    super.onNewIntent(intent);

    //Bundle extras = intent.getExtras();
    //processNotificationDataIfPresence(extras);
    //}

    //private void processNotificationDataIfPresence(Bundle extras)
    //{
    //    if (extras != null && extras.containsKey(NotificationKey.BUNDLE_KEY_KEY))
    //    {
    //        progressDialog = progressDialogUtil.get().show(this, "", "");
    //
    //        detachNotificationFetchTask();
    //        NotificationKey key = new NotificationKey(extras);
    //        notificationCache.get().register(key, notificationFetchListener);
    //        notificationCache.get().getOrFetchAsync(key, false);
    //    }
    //}
    //
    //private void detachNotificationFetchTask()
    //{
    //    notificationCache.get().unregister(notificationFetchListener);
    //}
    //private void showReferralCodeDialog()
    //{
    //    if (firstShowReferralCodeDialogPreference.get())
    //    {
    //        firstShowReferralCodeDialogPreference.set(false);
    //        if (THUser.getTHAuthenticationProvider() != null)
    //        {
    //            if (THUser.getTHAuthenticationProvider().getAuthType().equals(EmailCredentialsDTO.EMAIL_AUTH_TYPE))
    //            {
    //                //not show referral code dialog if login by email by alex
    //                return;
    //            }
    //        }
    //        UserProfileDTO userProfileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());
    //        if (userProfileDTO != null)
    //        {
    //            if (userProfileDTO.inviteCode != null && !userProfileDTO.inviteCode.isEmpty())
    //            {
    //                return;
    //            }
    //        }
    //        if (mReferralCodeDialog == null)
    //        {
    //            mReferralCodeDialog = alertDialogUtil.get().getReferralCodeDialog(this, currentUserId.toUserBaseKey(), new TrackCallback());
    //        }
    //        mReferralCodeDialog.show();
    //    }
    //}

    //public class TrackCallback implements retrofit.Callback<Response>
    //{
    //    @Override public void success(Response response, Response response2)
    //    {
    //        alertDialogUtil.get().dismissProgressDialog();
    //        if (mReferralCodeDialog != null)
    //        {
    //            mReferralCodeDialog.dismiss();
    //        }
    //        userProfileCache.get().invalidate(currentUserId.toUserBaseKey());
    //        THToast.show(R.string.referral_code_callback_success);
    //    }
    //
    //    @Override public void failure(RetrofitError retrofitError)
    //    {
    //        alertDialogUtil.get().dismissProgressDialog();
    //        if ((new THException(retrofitError)).getMessage().contains("Already invited"))
    //        {
    //            if (mReferralCodeDialog != null)
    //            {
    //                mReferralCodeDialog.dismiss();
    //            }
    //            userProfileCache.get().invalidate(currentUserId.toUserBaseKey());
    //            THToast.show(R.string.referral_code_callback_success);
    //        }
    //        else
    //        {
    //            THToast.show(new THException(retrofitError));
    //        }
    //    }
    //}

    //@Override
    //public boolean dispatchTouchEvent(MotionEvent ev)
    //{
    //    return resideMenu.onInterceptTouchEvent(ev) || super.dispatchTouchEvent(ev);
    //}

    //private void launchBilling()
    //{
    //    if (restoreRequestCode != null)
    //    {
    //        billingInteractor.get().forgetRequestCode(restoreRequestCode);
    //    }
    //    restoreRequestCode = billingInteractor.get().run(createRestoreRequest());
    //    // TODO fetch more stuff?
    //}

    //protected THUIBillingRequest createRestoreRequest()
    //{
    //    THUIBillingRequest request = emptyBillingRequestProvider.get();
    //    request.restorePurchase = true;
    //    request.startWithProgressDialog = false;
    //    request.popRestorePurchaseOutcome = true;
    //    request.popRestorePurchaseOutcomeVerbose = false;
    //    request.purchaseRestorerListener = purchaseRestorerFinishedListener;
    //    return request;
    //}

    //private void suggestUpgradeIfNecessary()
    //{
    //    if (getIntent() != null && getIntent().getBooleanExtra(UserLoginDTO.SUGGEST_UPGRADE, false))
    //    {
    //        alertDialogUtil.get().popWithOkCancelButton(
    //            this, R.string.upgrade_needed, R.string.suggest_to_upgrade, R.string.update_now,
    //            R.string.later,
    //            new DialogInterface.OnClickListener()
    //            {
    //                @Override public void onClick(DialogInterface dialog, int which)
    //                {
    //                    try
    //                    {
    //                        THToast.show(R.string.update_guide);
    //                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
    //                                        "market://details?id=" + Constants.PLAYSTORE_APP_ID)));
    //                    } catch (ActivityNotFoundException ex)
    //                    {
    //                        startActivity(new Intent(Intent.ACTION_VIEW,
    //                                        Uri.parse("https://play.google.com/store/apps/details?id="
    //                                                        + Constants.PLAYSTORE_APP_ID)));
    //                    }
    //                }
    //            });
    //    }
    //}

    //@Override public boolean onCreateOptionsMenu(Menu menu)
    //{
    //    UserProfileDTO currentUserProfile =
    //            userProfileCache.get().get(currentUserId.toUserBaseKey());
    //    MenuInflater menuInflater = getSupportMenuInflater();
    //
    //    menuInflater.inflate(R.menu.hardware_menu, menu);
    //
    //    if (currentUserProfile != null)
    //    {
    //        if (currentUserProfile.isAdmin)
    //        {
    //            menuInflater.inflate(R.menu.admin_menu, menu);
    //        }
    //    }
    //    return super.onCreateOptionsMenu(menu);
    //}

    //@Override public boolean onOptionsItemSelected(MenuItem item)
    //{
    //    // required for fragment onOptionItemSelected to be called
    //    switch (item.getItemId())
    //    {
    //        case R.id.admin_settings:
    //            getDashboardNavigator().pushFragment(AdminSettingsFragment.class);
    //            return true;
    //        case R.id.hardware_menu_settings:
    //            pushFragmentIfNecessary(SettingsFragment.class);
    //            return true;
    //        case R.id.hardware_menu_about:
    //            pushFragmentIfNecessary(AboutFragment.class);
    //            return true;
    //    }
    //    return super.onOptionsItemSelected(item);
    //}

    //private void launchActions()
    //{
    //    Intent intent = getIntent();
    //    if (intent == null || intent.getAction() == null)
    //    {
    //        return;
    //    }
    //
    //    if (intent.getData() != null)
    //    {
    //        String url = intent.getData().toString();
    //        url = url.replace("tradehero://", "");
    //        thRouter.open(url, this);
    //        return;
    //    }
    //
    //    switch (intent.getAction())
    //    {
    //        case Intent.ACTION_VIEW:
    //        case Intent.ACTION_MAIN:
    //            if (thIntentFactory.get().isHandlableIntent(intent))
    //            {
    //                getDashboardNavigator().goToPage(thIntentFactory.get().create(intent));
    //            }
    //            break;
    //    }
    //    Timber.d(getIntent().getAction());
    //}

    //@Override public void openMenu()
    //{
    //    Fragment currentFragment =
    //            getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
    //    if (currentFragment != null && currentFragment instanceof ResideMenu.OnMenuListener)
    //    {
    //        ((ResideMenu.OnMenuListener) currentFragment).openMenu();
    //    }
    //}

    //@Override public void closeMenu()
    //{
    //    Fragment currentFragment =
    //            getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
    //    if (currentFragment != null && currentFragment instanceof ResideMenu.OnMenuListener)
    //    {
    //        ((ResideMenu.OnMenuListener) currentFragment).closeMenu();
    //    }
    //}

    //protected DTOCacheNew.HurriedListener<NotificationKey, NotificationDTO> createNotificationFetchListener()
    //{
    //    return new NotificationFetchListener();
    //}
    //
    //protected class NotificationFetchListener
    //        implements DTOCacheNew.HurriedListener<NotificationKey, NotificationDTO>
    //{
    //    @Override public void onPreCachedDTOReceived(@NotNull NotificationKey key, @NotNull NotificationDTO value)
    //    {
    //        onDTOReceived(key, value);
    //    }
    //
    //    @Override
    //    public void onDTOReceived(@NotNull NotificationKey key, @NotNull NotificationDTO value)
    //    {
    //        onFinish();
    //
    //        NotificationClickHandler notificationClickHandler = new NotificationClickHandler(DashboardActivity.this, value);
    //        notificationClickHandler.handleNotificationItemClicked();
    //    }
    //
    //    @Override public void onErrorThrown(@NotNull NotificationKey key, @NotNull Throwable error)
    //    {
    //        onFinish();
    //        THToast.show(new THException(error));
    //    }
    //
    //    private void onFinish()
    //    {
    //        if (progressDialog != null)
    //        {
    //            progressDialog.hide();
    //        }
    //    }
    //}
}
