package com.tradehero.th.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TabHost;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import com.special.residemenu.ResideMenu;
import com.tradehero.common.billing.BillingPurchaseRestorer;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.UIModule;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THApp;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.competition.ProviderVideoListFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.fragments.home.HomeFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.onboarding.OnBoardDialogFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.settings.AboutFragment;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.social.friend.InviteCodeDialogFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.messages.MessagesCenterFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationClickHandler;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.tradehero.th.inject.Injector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.prefs.FirstShowInviteCodeDialog;
import com.tradehero.th.persistence.prefs.FirstShowOnBoardDialog;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.ui.AppContainer;
import com.tradehero.th.ui.ViewWrapper;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.WeiboUtils;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class DashboardActivity extends SherlockFragmentActivity
        implements Injector, ResideMenu.OnMenuListener
{
    private DashboardNavigator navigator;
    @Inject Set<DashboardNavigator.DashboardFragmentWatcher> dashboardFragmentWatchers;

    // It is important to have Lazy here because we set the current Activity after the injection
    // and the LogicHolder creator needs the current Activity...
    @Inject Lazy<THBillingInteractor> billingInteractor;
    @Inject Provider<BaseTHUIBillingRequest.Builder> thUiBillingRequestBuilderProvider;

    private BillingPurchaseRestorer.OnPurchaseRestorerListener purchaseRestorerFinishedListener;
    private Integer restoreRequestCode;

    @Inject Lazy<FacebookUtils> facebookUtils;
    @Inject Lazy<WeiboUtils> weiboUtils;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<AlertDialogUtil> alertDialogUtil;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtil;
    @Inject Lazy<NotificationCache> notificationCache;
    @Inject DeviceTokenHelper deviceTokenHelper;
    @Inject @FirstShowInviteCodeDialog BooleanPreference firstShowInviteCodeDialogPreference;
    @Inject @FirstShowOnBoardDialog TimingIntervalPreference firstShowOnBoardDialogPreference;
    @Inject SystemStatusCache systemStatusCache;
    @Inject Lazy<MarketUtil> marketUtilLazy;

    @Inject AppContainer appContainer;
    @Inject ViewWrapper slideMenuContainer;
    @Inject ResideMenu resideMenu;

    @Inject THRouter thRouter;
    @Inject Lazy<PushNotificationManager> pushNotificationManager;
    @Inject Analytics analytics;

    private DTOCacheNew.HurriedListener<NotificationKey, NotificationDTO> notificationFetchListener;

    private ProgressDialog progressDialog;
    private Injector newInjector;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        AppTiming.dashboardCreate = System.currentTimeMillis();
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);

        THApp app = THApp.get(this);
        newInjector = app.plus(new DashboardActivityModule());
        newInjector.inject(this);

        if (Constants.RELEASE)
        {
            Crashlytics.setString(Constants.TH_CLIENT_TYPE,
                    String.format("%s:%d", deviceTokenHelper.getDeviceType(), Constants.TAP_STREAM_TYPE.type));
            Crashlytics.setUserIdentifier("" + currentUserId.get());
        }

        ViewGroup dashboardWrapper = appContainer.get(this);

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

        detachNotificationFetchTask();
        notificationFetchListener = createNotificationFetchListener();

        // TODO better staggering of starting popups.
        suggestUpgradeIfNecessary();
        //dtoCacheUtil.initialPrefetches();//this will block first initial launch securities list,
        // and this line is no use for it will update after login in prefetchesUponLogin

        showInviteCodeDialog();

        navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);
        for (DashboardNavigator.DashboardFragmentWatcher watcher: dashboardFragmentWatchers)
        {
            navigator.addDashboardFragmentWatcher(watcher);
        }

        DashboardTabHost fragmentTabHost = (DashboardTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup();
        navigator.addDashboardFragmentWatcher(fragmentTabHost);
        fragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            @Override public void onTabChanged(String tabId)
            {
                RootFragmentType selectedFragmentType = RootFragmentType.valueOf(tabId);
                navigator.goToTab(selectedFragmentType);
            }
        });

        if (savedInstanceState == null && navigator.getCurrentFragment() == null)
        {
            navigator.goToTab(RootFragmentType.getInitialTab());
        }

        if (getIntent() != null)
        {
            processNotificationDataIfPresence(getIntent().getExtras());
        }
        //TODO need check whether this is ok for urbanship,
        //TODO for baidu, PushManager.startWork can't run in Application.init() for stability, it will run in a circle. by alex
        pushNotificationManager.get().enablePush();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        return resideMenu.onInterceptTouchEvent(ev) || super.dispatchTouchEvent(ev);
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
        BaseTHUIBillingRequest.Builder builder = thUiBillingRequestBuilderProvider.get();
        //noinspection unchecked,PointlessBooleanExpression
        builder.restorePurchase(true)
                .startWithProgressDialog(!Constants.RELEASE)
                .popRestorePurchaseOutcome(true)
                .popRestorePurchaseOutcomeVerbose(false)
                .purchaseRestorerListener(purchaseRestorerFinishedListener);
        return builder.build();
    }

    @Override public void onBackPressed()
    {
        navigator.popFragment();
    }

    private void suggestUpgradeIfNecessary()
    {
        if (getIntent() != null && getIntent().getBooleanExtra(UserLoginDTO.SUGGEST_UPGRADE, false))
        {
            alertDialogUtil.get().popWithOkCancelButton(
                this, R.string.upgrade_needed, R.string.suggest_to_upgrade, R.string.update_now,
                R.string.later,
                new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialog, int which)
                    {
                        THToast.show(R.string.update_guide);
                        marketUtilLazy.get().showAppOnMarket(DashboardActivity.this);
                    }
                });
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        UserProfileDTO currentUserProfile =
                userProfileCache.get().get(currentUserId.toUserBaseKey());
        MenuInflater menuInflater = getSupportMenuInflater();

        menuInflater.inflate(R.menu.hardware_menu, menu);

        if (currentUserProfile != null)
        {
            if (currentUserProfile.isAdmin || !Constants.RELEASE)
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
                navigator.pushFragment(AdminSettingsFragment.class);
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
        Fragment currentDashboardFragment = navigator.getCurrentFragment();
        if (!(fragmentClass.isInstance(currentDashboardFragment)))
        {
            navigator.pushFragment(fragmentClass);
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
        launchActions();
        analytics.openSession();
    }

    @Override protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        processNotificationDataIfPresence(extras);
    }

    private void processNotificationDataIfPresence(Bundle extras)
    {
        if (extras != null && extras.containsKey(NotificationKey.BUNDLE_KEY_KEY))
        {
            progressDialog = progressDialogUtil.get().show(this, "", "");

            detachNotificationFetchTask();
            NotificationKey key = new NotificationKey(extras);
            notificationCache.get().register(key, notificationFetchListener);
            notificationCache.get().getOrFetchAsync(key, false);
        }
    }

    private void detachNotificationFetchTask()
    {
        notificationCache.get().unregister(notificationFetchListener);
    }

    @Override protected void onPause()
    {
        analytics.closeSession();
        super.onPause();
    }

    @Override protected void onStop()
    {
        detachNotificationFetchTask();

        super.onStop();
    }

    @Override protected void onDestroy()
    {
        THBillingInteractor billingInteractorCopy = billingInteractor.get();
        if (billingInteractorCopy != null && restoreRequestCode != null)
        {
            billingInteractorCopy.forgetRequestCode(restoreRequestCode);
        }

        if (navigator != null)
        {
            navigator.onDestroy();
        }
        navigator = null;

        purchaseRestorerFinishedListener = null;
        notificationFetchListener = null;

        super.onDestroy();
    }

    private void showInviteCodeDialog()
    {
        if (shouldShowOnBoard())
        {
            showOnboard();
        }
        else if (shouldShowInviteCode())
        {
            firstShowInviteCodeDialogPreference.set(false);
            InviteCodeDialogFragment dialogFragment = InviteCodeDialogFragment.showInviteCodeDialog(getSupportFragmentManager());
            dialogFragment.setDismissedListener(new DashboardOnInviteCodeDismissed());
        }
    }

    protected boolean shouldShowInviteCode()
    {
        UserProfileDTO userProfileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());
        return firstShowInviteCodeDialogPreference.get()
                //&& !(THUser.getTHAuthenticationProvider() instanceof EmailAuthenticationProvider)
                && (userProfileDTO == null || userProfileDTO.inviteCode == null || userProfileDTO.inviteCode.isEmpty());
    }

    @Override public void inject(Object o)
    {
        newInjector.inject(o);
    }

    protected class DashboardOnInviteCodeDismissed implements BaseDialogFragment.OnDismissedListener
    {
        @Override public void onDismissed(DialogInterface dialog)
        {
            shouldShowOnBoard();
        }
    }

    protected boolean shouldShowOnBoard()
    {
        if (firstShowOnBoardDialogPreference.isItTime())
        {
            UserProfileDTO currentUserProfile =
                    userProfileCache.get().get(currentUserId.toUserBaseKey());
            if (currentUserProfile != null)
            {
                if (currentUserProfile.heroIds != null && currentUserProfile.heroIds.size() > 0)
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    protected void showOnboard()
    {
        if (shouldShowOnBoard())
        {
            OnBoardDialogFragment.showOnBoardDialog(getSupportFragmentManager());
        }
    }

    private void launchActions()
    {
        Intent intent = getIntent();
        if (intent == null || intent.getAction() == null)
        {
            return;
        }

        if (intent.getData() != null)
        {
            String url = intent.getData().toString();
            url = url.replace("tradehero://", "");
            thRouter.open(url, this);
            return;
        }

        Timber.d(getIntent().getAction());
        Timber.e(new Exception("thIntentFactory"), "Was handled by thIntentFactory");
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        facebookUtils.get().finishAuthentication(requestCode, resultCode, data);
        // Passing it on just in case it is expecting something
        billingInteractor.get().onActivityResult(requestCode, resultCode, data);
        weiboUtils.get().authorizeCallBack(requestCode, resultCode, data);
    }

    @Override public void openMenu()
    {
        Fragment currentFragment =
                getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
        if (currentFragment != null && currentFragment instanceof ResideMenu.OnMenuListener)
        {
            ((ResideMenu.OnMenuListener) currentFragment).openMenu();
        }
    }

    @Override public void closeMenu()
    {
        Fragment currentFragment =
                getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
        if (currentFragment != null && currentFragment instanceof ResideMenu.OnMenuListener)
        {
            ((ResideMenu.OnMenuListener) currentFragment).closeMenu();
        }
    }

    protected DTOCacheNew.HurriedListener<NotificationKey, NotificationDTO> createNotificationFetchListener()
    {
        return new NotificationFetchListener();
    }

    protected class NotificationFetchListener
            implements DTOCacheNew.HurriedListener<NotificationKey, NotificationDTO>
    {
        @Override public void onPreCachedDTOReceived(@NotNull NotificationKey key, @NotNull NotificationDTO value)
        {
            onDTOReceived(key, value);
        }

        @Override
        public void onDTOReceived(@NotNull NotificationKey key, @NotNull NotificationDTO value)
        {
            onFinish();

            NotificationClickHandler notificationClickHandler = new NotificationClickHandler(DashboardActivity.this, value);
            notificationClickHandler.handleNotificationItemClicked();
        }

        @Override public void onErrorThrown(@NotNull NotificationKey key, @NotNull Throwable error)
        {
            onFinish();
            THToast.show(new THException(error));
        }

        private void onFinish()
        {
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
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

    @Module(
            addsTo = AppModule.class,
            includes = {
                    UIModule.class
            },
            library = true,
            complete = false,
            overrides = true
    )
    public class DashboardActivityModule
    {
        @Provides Activity provideActivity()
        {
            return DashboardActivity.this;
        }

        @Provides DashboardNavigator provideDashboardNavigator()
        {
            return navigator;
        }

        @Provides @Singleton THRouter provideTHRouter(Context context, Provider<DashboardNavigator> navigatorProvider)
        {
            THRouter router = new THRouter(context, navigatorProvider);
            router.registerRoutes(
                    PushableTimelineFragment.class,
                    MeTimelineFragment.class,
                    NotificationsCenterFragment.class,
                    MessagesCenterFragment.class,
                    UpdateCenterFragment.class,
                    TrendingFragment.class,
                    FriendsInvitationFragment.class,
                    SettingsFragment.class,
                    MainCompetitionFragment.class,
                    BuySellFragment.class,
                    StoreScreenFragment.class,
                    LeaderboardCommunityFragment.class,
                    CompetitionWebViewFragment.class,
                    PositionListFragment.class,
                    TradeListFragment.class,
                    HomeFragment.class,
                    ProviderVideoListFragment.class
            );
            router.registerAlias("messages", "updatecenter/0");
            router.registerAlias("notifications", "updatecenter/1");
            router.registerAlias("reset-portfolio", "store/" + ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO.ordinal());
            router.registerAlias("store/reset-portfolio", "store/" + ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO.ordinal());
            return router;
        }
    }
}
