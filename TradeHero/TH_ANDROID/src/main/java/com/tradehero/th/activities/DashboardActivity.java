package com.tradehero.th.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.AbsListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.crashlytics.android.Crashlytics;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnScrollViewOnScrollChangedListener;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.special.residemenu.ResideMenu;
import com.tradehero.common.activities.ActivityResultRequester;
import com.tradehero.common.billing.restore.PurchaseRestoreTotalResult;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.common.utils.OnlineStateReceiver;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.NotifyingWebView;
import com.tradehero.common.widget.QuickReturnWebViewOnScrollChangedListener;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.BottomTabsQuickReturnScrollViewListener;
import com.tradehero.th.BottomTabsQuickReturnWebViewListener;
import com.tradehero.th.R;
import com.tradehero.th.UIModule;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.level.UserXPAchievementDTO;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.system.SystemStatusKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.NavigationAnalyticsReporter;
import com.tradehero.th.fragments.achievement.AbstractAchievementDialogFragment;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.competition.CompetitionEnrollmentBroadcastSignal;
import com.tradehero.th.fragments.competition.CompetitionEnrollmentWebViewFragment;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.competition.ProviderVideoListFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.fragments.discovery.DiscoveryMainFragment;
import com.tradehero.th.fragments.fxonboard.FxOnBoardDialogFragment;
import com.tradehero.th.fragments.games.GameWebViewFragment;
import com.tradehero.th.fragments.home.HomeFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.onboarding.OnBoardDialogFragment;
import com.tradehero.th.fragments.onboarding.OnBoardingBroadcastSignal;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.settings.AboutFragment;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.fragments.settings.AskForReviewSuggestedDialogFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFXFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingStockFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.messages.MessagesCenterFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationClickHandler;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.notification.NotificationCacheRx;
import com.tradehero.th.persistence.prefs.IsFxShown;
import com.tradehero.th.persistence.prefs.IsOnBoardShown;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.ui.AppContainer;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.metrics.ForAnalytics;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.XpToast;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.tradehero.th.utils.broadcast.BroadcastConstants.ACHIEVEMENT_INTENT_FILTER;
import static com.tradehero.th.utils.broadcast.BroadcastConstants.ENROLLMENT_INTENT_FILTER;
import static com.tradehero.th.utils.broadcast.BroadcastConstants.KEY_USER_ACHIEVEMENT_ID;
import static com.tradehero.th.utils.broadcast.BroadcastConstants.KEY_XP_BROADCAST;
import static com.tradehero.th.utils.broadcast.BroadcastConstants.ONBOARD_INTENT_FILTER;
import static com.tradehero.th.utils.broadcast.BroadcastConstants.SEND_LOVE_INTENT_FILTER;
import static com.tradehero.th.utils.broadcast.BroadcastConstants.XP_INTENT_FILTER;
import static rx.android.observables.AndroidObservable.bindActivity;
import static rx.android.observables.AndroidObservable.fromLocalBroadcast;

public class DashboardActivity extends BaseActivity
        implements ResideMenu.OnMenuListener
{
    private DashboardNavigator navigator;
    @Inject Set<DashboardNavigator.DashboardFragmentWatcher> dashboardFragmentWatchers;

    // It is important to have Lazy here because we set the current Activity after the injection
    // and the LogicHolder creator needs the current Activity...
    @Inject Lazy<THBillingInteractorRx> billingInteractorRx;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Lazy<UserProfileDTOUtil> userProfileDTOUtilLazy;
    @Inject Lazy<AlertDialogUtil> alertDialogUtil;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtil;
    @Inject Lazy<NotificationCacheRx> notificationCache;
    @Inject SystemStatusCache systemStatusCache;
    @Inject Lazy<MarketUtil> marketUtilLazy;

    @Inject AppContainer appContainer;
    @Inject ResideMenu resideMenu;

    @Inject THRouter thRouter;
    @Inject Lazy<PushNotificationManager> pushNotificationManager;
    @Inject Analytics analytics;
    @Inject Lazy<BroadcastUtils> broadcastUtilsLazy;
    @Inject AbstractAchievementDialogFragment.Creator achievementDialogCreator;
    @Inject @IsOnBoardShown BooleanPreference isOnboardShown;
    @Inject @IsFxShown BooleanPreference isFxShown;
    @Inject Set<ActivityResultRequester> activityResultRequesters;
    @Inject @ForAnalytics Lazy<DashboardNavigator.DashboardFragmentWatcher> analyticsReporter;

    @Inject Lazy<ProviderListCacheRx> providerListCache;
    private final Set<Integer> enrollmentScreenOpened = new HashSet<>();
    private boolean enrollmentScreenIsOpened = false;

    @InjectView(R.id.xp_toast_box) XpToast xpToast;

    private Subscription notificationFetchSubscription;

    private ProgressDialog progressDialog;
    private DashboardTabHost dashboardTabHost;
    private int tabHostHeight;
    private BroadcastReceiver onlineStateReceiver;
    private MenuItem networkIndicator;
    private CompositeSubscription subscriptions;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        AppTiming.dashboardCreate = System.currentTimeMillis();
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);

        if (Constants.RELEASE)
        {
            Crashlytics.setString(Constants.TH_CLIENT_TYPE,
                    String.format("%s:%d", Constants.DEVICE_TYPE, Constants.TAP_STREAM_TYPE.type));
            Crashlytics.setUserIdentifier("" + currentUserId.get());
        }

        appContainer.wrap(this);

        if (Constants.RELEASE)
        {
            launchBilling();
        }

        // TODO better staggering of starting popups.
        suggestUpgradeIfNecessary();
        showStartDialogsPlease();

        tabHostHeight = (int) getResources().getDimension(R.dimen.dashboard_tabhost_height);
        setupNavigator();
        setupDashboardTabHost();

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

        initBroadcastReceivers();

        localBroadcastManager.registerReceiver(onlineStateReceiver, new IntentFilter(OnlineStateReceiver.ONLINE_STATE_CHANGED));

        ButterKnife.inject(this);
    }

    private void setupNavigator()
    {
        navigator = new DashboardNavigator(this, R.id.realtabcontent);
        CollectionUtils.apply(dashboardFragmentWatchers, navigator::addDashboardFragmentWatcher);
    }

    private void setupDashboardTabHost()
    {
        dashboardTabHost = (DashboardTabHost) findViewById(android.R.id.tabhost);
        dashboardTabHost.setup();
        dashboardTabHost.setOnTabChangedListener(tabId -> {
            RootFragmentType selectedFragmentType = RootFragmentType.valueOf(tabId);
            navigator.goToTab(selectedFragmentType);
        });
        navigator.addDashboardFragmentWatcher(analyticsReporter.get());
        navigator.addDashboardFragmentWatcher(dashboardTabHost);
    }

    private void initBroadcastReceivers()
    {
        onlineStateReceiver = new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                updateNetworkStatus();
            }
        };
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev)
    {
        return resideMenu.onInterceptTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    private void launchBilling()
    {
        // TODO fetch more stuff?
        //noinspection unchecked
        bindActivity(
                this,
                billingInteractorRx.get().restorePurchasesAndClear())
                .subscribe(new Observer<PurchaseRestoreTotalResult>()
                {
                    @Override public void onNext(PurchaseRestoreTotalResult args)
                    {
                        //TODO
                    }

                    @Override public void onCompleted()
                    {
                        THToast.show("Restore completed");
                    }

                    @Override public void onError(Throwable e)
                    {
                        THToast.show("Restore failed");
                        Timber.e(e, "Restore failed");
                    }
                });
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
                    (dialog, which) -> {
                        THToast.show(R.string.update_guide);
                        marketUtilLazy.get().showAppOnMarket(DashboardActivity.this);
                    });
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        UserProfileDTO currentUserProfile =
                userProfileCache.get().getValue(currentUserId.toUserBaseKey());
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.network_menu, menu);

        networkIndicator = menu.findItem(R.id.menu_network);
        updateNetworkStatus();

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
            case R.id.menu_network:
                alertDialogUtil.get().popNetworkUnavailable(this);
                return true;
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
        systemStatusCache.get(new SystemStatusKey());
    }

    @Override protected void onResume()
    {
        super.onResume();
        launchActions();
        analytics.openSession();

        subscriptions = new CompositeSubscription();
        subscriptions.add(bindActivity(this, fromLocalBroadcast(this, ACHIEVEMENT_INTENT_FILTER)
                .filter(intent -> intent != null && intent.getBundleExtra(KEY_USER_ACHIEVEMENT_ID) != null)
                .map(intent -> intent.getBundleExtra(KEY_USER_ACHIEVEMENT_ID))
                .map(UserAchievementId::new)
                .flatMap(achievementDialogCreator::newInstance))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        fragment -> fragment.show(getFragmentManager(), AbstractAchievementDialogFragment.TAG),
                        throwable -> {
                        }, () -> broadcastUtilsLazy.get().nextPlease()
                ));

        subscriptions.add(fromLocalBroadcast(this, XP_INTENT_FILTER)
                .filter(intent -> (intent != null) && (intent.getBundleExtra(KEY_XP_BROADCAST) != null))
                .map(intent -> new UserXPAchievementDTO(intent.getBundleExtra(KEY_XP_BROADCAST)))
                .subscribe(xpToast::showWhenReady, throwable -> {}, () -> broadcastUtilsLazy.get().nextPlease()));

        subscriptions.add(fromLocalBroadcast(this, ONBOARD_INTENT_FILTER)
                .subscribe(intent -> {
                    isOnboardShown.set(true);
                    OnBoardDialogFragment.showOnBoardDialog(getFragmentManager());
                }, throwable -> {}));

        // get providers for enrollment page
        subscriptions.add(bindActivity(this, fromLocalBroadcast(this, ENROLLMENT_INTENT_FILTER)
                        .flatMap(intent -> providerListCache.get().get(new ProviderListKey()))
                        .flatMapIterable(pair -> pair.second)
                        .filter(providerDTO -> {
                            boolean r = !providerDTO.isUserEnrolled && !enrollmentScreenOpened.contains(providerDTO.id);
                            if(!r)
                            {
                                broadcastUtilsLazy.get().nextPlease();
                            }
                            return r;
                        }))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                providerDTO -> {
                                    if (!enrollmentScreenIsOpened)
                                    {
                                        enrollmentScreenIsOpened = true;
                                        enrollmentScreenOpened.add(providerDTO.id);
                                        navigator.pushFragment(CompetitionEnrollmentWebViewFragment.class, providerDTO.getProviderId().getArgs());
                                    }
                                },
                                throwable -> {
                                    THToast.show(R.string.error_fetch_provider_competition_list);
                                    broadcastUtilsLazy.get().nextPlease();
                                },
                                () -> broadcastUtilsLazy.get().nextPlease())
        );

        subscriptions.add(fromLocalBroadcast(this, SEND_LOVE_INTENT_FILTER)
                .subscribe(intent ->
                        AskForReviewSuggestedDialogFragment.showReviewDialog(getFragmentManager()), throwable -> {} ));
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
            notificationFetchSubscription = bindActivity(
                    this,
                    notificationCache.get().get(key))
                    .subscribe(createNotificationFetchObserver());
        }
    }

    private void detachNotificationFetchTask()
    {
        detachSubscription(notificationFetchSubscription);
        notificationFetchSubscription = null;
    }

    @Override protected void onPause()
    {
        analytics.closeSession();
        subscriptions.unsubscribe();
        super.onPause();
    }

    @Override protected void onStop()
    {
        detachNotificationFetchTask();

        super.onStop();
    }

    @Override protected void onDestroy()
    {
        notificationFetchSubscription = null;

        xpToast.destroy();

        networkIndicator = null;

        if (navigator != null)
        {
            navigator.onDestroy();
        }
        navigator = null;

        localBroadcastManager.unregisterReceiver(onlineStateReceiver);

        ButterKnife.reset(this);
        super.onDestroy();
    }

    protected void detachSubscription(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    private void showStartDialogsPlease()
    {
        bindActivity(this,
                userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .first()
                .subscribe(new EmptyObserver<Pair<UserBaseKey, UserProfileDTO>>()
                {
                    @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> args)
                    {
                        UserProfileDTO userProfileDTO = args.second;
                        if (!isOnboardShown.get() && userProfileDTO != null && userProfileDTOUtilLazy.get().shouldShowOnBoard(userProfileDTO))
                        {
                            broadcastUtilsLazy.get().enqueue(new OnBoardingBroadcastSignal());
                            return;
                        }

                        if (!isFxShown.get())
                        {
                            isFxShown.set(true);
                            FxOnBoardDialogFragment.showOnBoardDialog(getFragmentManager());
                            return;
                        }

                        broadcastUtilsLazy.get().enqueue(new CompetitionEnrollmentBroadcastSignal());
                    }
                });
    }

    @Override protected List<Object> getModules()
    {
        List<Object> superModules = new ArrayList<>(super.getModules());
        superModules.add(new DashboardActivityModule());
        return superModules;
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

    @Override protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        CollectionUtils.apply(activityResultRequesters, requester -> requester.onActivityResult(requestCode, resultCode, data));
    }

    @Override public void openMenu()
    {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
        if (currentFragment != null && currentFragment instanceof ResideMenu.OnMenuListener)
        {
            ((ResideMenu.OnMenuListener) currentFragment).openMenu();
        }
    }

    @Override public void closeMenu()
    {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
        if (currentFragment != null && currentFragment instanceof ResideMenu.OnMenuListener)
        {
            ((ResideMenu.OnMenuListener) currentFragment).closeMenu();
        }
    }

    protected void updateNetworkStatus()
    {
        Boolean connected = OnlineStateReceiver.isOnline(this);
        if (networkIndicator != null)
        {
            if (connected)
            {
                networkIndicator.setVisible(false);
            }
            else
            {
                networkIndicator.setVisible(true);
            }
        }
        if (getActionBar() != null)
        {
            Resources r = getResources();
            getActionBar().setBackgroundDrawable(r.getDrawable((connected ? R.drawable.ab_background : R.drawable.ab_background_state_disabled)));
        }
    }

    protected Observer<Pair<NotificationKey, NotificationDTO>> createNotificationFetchObserver()
    {
        return new NotificationFetchObserver();
    }

    protected class NotificationFetchObserver
            implements Observer<Pair<NotificationKey, NotificationDTO>>
    {
        @Override public void onNext(Pair<NotificationKey, NotificationDTO> pair)
        {
            onFinish();

            NotificationClickHandler notificationClickHandler = new NotificationClickHandler(DashboardActivity.this, pair.second);
            notificationClickHandler.handleNotificationItemClicked();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            onFinish();
            THToast.show(new THException(e));
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
        Fragment currentFragmentName = navigator.getCurrentFragment();
        Timber.e(new RuntimeException("LowMemory " + currentFragmentName), "%s", currentFragmentName);
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
                    TrendingStockFragment.class,
                    FriendsInvitationFragment.class,
                    SettingsFragment.class,
                    MainCompetitionFragment.class,
                    BuySellStockFragment.class,
                    BuySellFXFragment.class,
                    StoreScreenFragment.class,
                    LeaderboardCommunityFragment.class,
                    CompetitionWebViewFragment.class,
                    PositionListFragment.class,
                    TradeListFragment.class,
                    HomeFragment.class,
                    ProviderVideoListFragment.class,
                    WebViewFragment.class,
                    GameWebViewFragment.class,
                    DiscoveryMainFragment.class
            );
            router.registerAlias("messages", "updatecenter/0");
            router.registerAlias("notifications", "updatecenter/1");
            router.registerAlias("reset-portfolio", "store/" + ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO.ordinal());
            router.registerAlias("store/reset-portfolio", "store/" + ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO.ordinal());
            return router;
        }

        @Provides @BottomTabs DashboardTabHost provideDashboardBottomBar()
        {
            return dashboardTabHost;
        }

        @Provides @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener provideDashboardBottomTabScrollListener()
        {
            QuickReturnListViewOnScrollListener listener =
                    new QuickReturnListViewOnScrollListener(QuickReturnType.FOOTER, null, 0, dashboardTabHost, tabHostHeight);
            listener.setCanSlideInIdleScrollState(true);
            return listener;
        }

        @Provides @BottomTabsQuickReturnScrollViewListener NotifyingScrollView.OnScrollChangedListener provideQuickReturnListViewOnScrollListener()
        {
            return new QuickReturnScrollViewOnScrollChangedListener(QuickReturnType.FOOTER, null, 0, dashboardTabHost, tabHostHeight);
        }

        @Provides @BottomTabsQuickReturnWebViewListener NotifyingWebView.OnScrollChangedListener provideQuickReturnWebViewOnScrollListener()
        {
            return new QuickReturnWebViewOnScrollChangedListener(QuickReturnType.FOOTER, null, 0, dashboardTabHost, tabHostHeight);
        }

        @Provides @ForAnalytics DashboardNavigator.DashboardFragmentWatcher provideAnalyticsReporter()
        {
            return new NavigationAnalyticsReporter(analytics, dashboardTabHost);
        }
    }
}
