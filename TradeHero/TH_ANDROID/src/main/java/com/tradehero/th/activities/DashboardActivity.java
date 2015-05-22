package com.tradehero.th.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.TabHost;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnScrollViewOnScrollChangedListener;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.special.residemenu.ResideMenu;
import com.tradehero.common.activities.ActivityResultRequester;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.rx.PairGetSecond;
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
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.system.SystemStatusKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.NavigationAnalyticsReporter;
import com.tradehero.th.fragments.base.DashboardFragmentOuterElements;
import com.tradehero.th.fragments.base.FragmentOuterElements;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.competition.CompetitionEnrollmentBroadcastSignal;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.competition.ProviderVideoListFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.fragments.discovery.DiscoveryMainFragment;
import com.tradehero.th.fragments.fxonboard.FxOnBoardDialogFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.news.NewsWebFragment;
import com.tradehero.th.fragments.onboarding.OnBoardingBroadcastSignal;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.position.TabbedPositionListFragment;
import com.tradehero.th.fragments.settings.AskForReviewSuggestedDialogFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.trade.FXInfoFragment;
import com.tradehero.th.fragments.trade.FXMainFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingMainFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.messageNew.MessagesCenterNewFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationClickHandler;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.notification.NotificationCacheRx;
import com.tradehero.th.persistence.prefs.IsFxShown;
import com.tradehero.th.persistence.prefs.IsOnBoardShown;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.rx.view.DismissDialogAction1;
import com.tradehero.th.ui.AppContainer;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.metrics.ForAnalytics;
import com.tradehero.th.utils.metrics.appsflyer.THAppsFlyer;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import rx.Notification;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.tradehero.th.utils.broadcast.BroadcastConstants.ENROLLMENT_INTENT_FILTER;
import static com.tradehero.th.utils.broadcast.BroadcastConstants.ONBOARD_INTENT_FILTER;
import static com.tradehero.th.utils.broadcast.BroadcastConstants.SEND_LOVE_INTENT_FILTER;
import static rx.android.app.AppObservable.bindActivity;
import static rx.android.content.ContentObservable.fromLocalBroadcast;

public class DashboardActivity extends BaseActivity
        implements ResideMenu.OnMenuListener, AchievementAcceptor
{
    private DashboardNavigator navigator;
    @Inject Set<DashboardNavigator.DashboardFragmentWatcher> dashboardFragmentWatchers;

    // It is important to have Lazy here because we set the current Activity after the injection
    // and the LogicHolder creator needs the current Activity...
    @Inject Lazy<THBillingInteractorRx> billingInteractorRx;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Lazy<UserProfileDTOUtil> userProfileDTOUtilLazy;
    @Inject Lazy<NotificationCacheRx> notificationCache;
    @Inject SystemStatusCache systemStatusCache;

    @Inject AppContainer appContainer;
    @Inject ResideMenu resideMenu;

    @Inject THRouter thRouter;
    @Inject Analytics analytics;
    @Inject Lazy<BroadcastUtils> broadcastUtilsLazy;
    @Inject @IsOnBoardShown BooleanPreference isOnboardShown;
    @Inject @IsFxShown BooleanPreference isFxShown;
    @Inject Set<ActivityResultRequester> activityResultRequesters;
    @Inject @ForAnalytics Lazy<DashboardNavigator.DashboardFragmentWatcher> analyticsReporter;
    @Inject ProviderUtil providerUtil;

    @Inject Lazy<ProviderListCacheRx> providerListCache;
    private final Set<Integer> enrollmentScreenOpened = new HashSet<>();
    private boolean enrollmentScreenIsOpened = false;

    @InjectView(R.id.my_toolbar) Toolbar toolbar;

    private Subscription notificationFetchSubscription;

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

        ActivityBuildTypeUtil.setUpCrashReports(currentUserId.toUserBaseKey());

        appContainer.wrap(this);

        if (Constants.RELEASE)
        {
            launchBilling();
        }

        // TODO better staggering of starting popups.
        suggestUpgradeIfNecessary();
        showStartDialogsPlease();

        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

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
        //pushNotificationManager.get().enablePush();

        initBroadcastReceivers();

        localBroadcastManager.registerReceiver(onlineStateReceiver, new IntentFilter(OnlineStateReceiver.ONLINE_STATE_CHANGED));

        routeDeepLink(getIntent());
    }

    private void setupNavigator()
    {
        navigator = new DashboardNavigator(this, R.id.realtabcontent);
        CollectionUtils.apply(dashboardFragmentWatchers, new Action1<DashboardNavigator.DashboardFragmentWatcher>()
        {
            @Override public void call(DashboardNavigator.DashboardFragmentWatcher watcher)
            {
                navigator.addDashboardFragmentWatcher(watcher);
            }
        });
    }

    private void setupDashboardTabHost()
    {
        dashboardTabHost = (DashboardTabHost) findViewById(android.R.id.tabhost);
        dashboardTabHost.setup();
        dashboardTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            @Override public void onTabChanged(String tabId)
            {
                try
                {
                    RootFragmentType selectedFragmentType = RootFragmentType.valueOf(tabId);
                    navigator.goToTab(selectedFragmentType);
                } catch (IllegalStateException e)
                {
                    Timber.d("setOnTabChangedListener goToTab " + e.toString());
                }
            }
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

    private void routeDeepLink(@NonNull Intent intent)
    {
        Uri data = intent.getData();
        if (data != null)
        {
            thRouter.open(data, null, this);
            intent.setData(null);
        }
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
                billingInteractorRx.get().restorePurchasesAndClear(false))
                .subscribe(
                        new EmptyAction1<OnDialogClickEvent>(),
                        new TimberOnErrorAction("Failed to restore"));
    }

    @Override public void onBackPressed()
    {
        navigator.popFragment();
    }

    private void suggestUpgradeIfNecessary()
    {
        if (getIntent() != null && getIntent().getBooleanExtra(UserLoginDTO.SUGGEST_UPGRADE, false))
        {
            showUpgradeDialog();
        }
    }

/*
        P2: There is a unnecessary menu button on Me page
        https://www.pivotaltracker.com/n/projects/559137/stories/91165728

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        UserProfileDTO currentUserProfile =
                userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey());
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
                AlertDialogRxUtil.popNetworkUnavailable(this).subscribe(
                        new EmptyAction1<OnDialogClickEvent>(),
                        new EmptyAction1<Throwable>());
                return true;
            case R.id.admin_settings:
                navigator.launchActivity(AdminSettingsActivity.class);
                return true;
            case R.id.hardware_menu_settings:
                navigator.launchActivity(SettingsActivity.class);
                return true;
            case R.id.hardware_menu_about:
                pushFragmentIfNecessary(AboutFragment.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/

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
        THAppsFlyer.sendTracking(this);
    }

    @Override protected void onResume()
    {
        super.onResume();
        launchActions();

        subscriptions = new CompositeSubscription();

        subscriptions.add(fromLocalBroadcast(this, ONBOARD_INTENT_FILTER)
                .subscribe(
                        new Action1<Intent>()
                        {
                            @Override public void call(Intent intent)
                            {
                                isOnboardShown.set(true);
                                navigator.launchActivity(OnBoardActivity.class);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                broadcastUtilsLazy.get().nextPlease();
                            }
                        }));

        // get providers for enrollment page
        subscriptions.add(bindActivity(this, fromLocalBroadcast(this, ENROLLMENT_INTENT_FILTER)
                        .flatMap(new Func1<Intent, Observable<? extends Pair<ProviderListKey, ProviderDTOList>>>()
                        {
                            @Override public Observable<? extends Pair<ProviderListKey, ProviderDTOList>> call(Intent intent)
                            {
                                return providerListCache.get().get(new ProviderListKey());
                            }
                        })
                        .flatMap(new Func1<Pair<ProviderListKey, ProviderDTOList>, Observable<ProviderDTO>>()
                        {
                            @Override public Observable<ProviderDTO> call(Pair<ProviderListKey, ProviderDTOList> pair)
                            {
                                for (ProviderDTO providerDTO : pair.second)
                                {
                                    boolean r = !providerDTO.isUserEnrolled && !enrollmentScreenOpened.contains(providerDTO.id);
                                    if (r)
                                    {
                                        return Observable.just(providerDTO);
                                    }
                                }
                                broadcastUtilsLazy.get().nextPlease();
                                return Observable.empty();
                            }
                        })
                        .subscribeOn(Schedulers.io()))
                        .subscribe(
                                new Observer<ProviderDTO>()
                                {
                                    @Override public void onNext(ProviderDTO providerDTO)
                                    {
                                        if (!enrollmentScreenIsOpened)
                                        {
                                            enrollmentScreenIsOpened = true;
                                            enrollmentScreenOpened.add(providerDTO.id);
                                            Bundle args = new Bundle();
                                            CompetitionWebViewFragment.putUrl(args, providerUtil.getLandingPage(
                                                    providerDTO.getProviderId()
                                            ));
                                            navigator.pushFragment(CompetitionWebViewFragment.class, args);
                                        }
                                    }

                                    @Override public void onCompleted()
                                    {
                                    }

                                    @Override public void onError(Throwable e)
                                    {
                                        THToast.show(R.string.error_fetch_provider_competition_list);
                                        broadcastUtilsLazy.get().nextPlease();
                                    }
                                })
        );

        subscriptions.add(fromLocalBroadcast(this, SEND_LOVE_INTENT_FILTER)
                .subscribe(new Action1<Intent>()
                {
                    @Override public void call(Intent intent)
                    {
                        AskForReviewSuggestedDialogFragment.showReviewDialog(DashboardActivity.this.getFragmentManager());
                    }
                }, new EmptyAction1<Throwable>()));

        if (resideMenu.isOpened())
        {
            userProfileCache.get().get(currentUserId.toUserBaseKey());
        }

    }

    @Override protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        processNotificationDataIfPresence(extras);
        routeDeepLink(intent);
    }

    private void processNotificationDataIfPresence(Bundle extras)
    {
        if (extras != null && extras.containsKey(NotificationKey.BUNDLE_KEY_KEY))
        {
            final ProgressDialog progressDialog = ProgressDialog.show(this, "", "", true);

            detachNotificationFetchTask();
            NotificationKey key = new NotificationKey(extras);
            notificationFetchSubscription = bindActivity(
                    this,
                    notificationCache.get().get(key))
                    .doOnEach(new DismissDialogAction1<Notification<? super Pair<NotificationKey, NotificationDTO>>>(progressDialog))
                    .subscribe(
                            new Action1<Pair<NotificationKey, NotificationDTO>>()
                            {
                                @Override public void call(Pair<NotificationKey, NotificationDTO> pair)
                                {
                                    DashboardActivity.this.onNotificationReceived(pair);
                                }
                            },
                            new ToastOnErrorAction());
        }
    }

    public void onNotificationReceived(Pair<NotificationKey, NotificationDTO> pair)
    {
        NotificationClickHandler notificationClickHandler = new NotificationClickHandler(DashboardActivity.this, pair.second);
        notificationClickHandler.handleNotificationItemClicked();
    }

    private void detachNotificationFetchTask()
    {
        detachSubscription(notificationFetchSubscription);
        notificationFetchSubscription = null;
    }

    @Override protected void onPause()
    {
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
        bindActivity(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>()))
                .first()
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO userProfileDTO)
                            {
                                if (!isOnboardShown.get() && userProfileDTO != null && userProfileDTOUtilLazy.get().shouldShowOnBoard(userProfileDTO))
                                {
                                    broadcastUtilsLazy.get().enqueue(new OnBoardingBroadcastSignal());
                                    return;
                                }

                                if (!isFxShown.get() && userProfileDTO != null && userProfileDTO.fxPortfolio == null)
                                {
                                    isFxShown.set(true);
                                    FxOnBoardDialogFragment.showOnBoardDialog(DashboardActivity.this.getFragmentManager());
                                    return;
                                }

                                broadcastUtilsLazy.get().enqueue(new CompetitionEnrollmentBroadcastSignal());
                            }
                        },
                        new ToastOnErrorAction());
    }

    @NonNull @Override protected List<Object> getModules()
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
        CollectionUtils.apply(activityResultRequesters, new Action1<ActivityResultRequester>()
        {
            @Override public void call(ActivityResultRequester requester)
            {
                requester.onActivityResult(DashboardActivity.this, requestCode, resultCode, data);
            }
        });
        RouteParams routeParams = getRouteParams(data);
        if (routeParams != null)
        {
            resideMenu.closeMenu();
            thRouter.open(routeParams.deepLink, routeParams.extras, this);
        }
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
        //TODO
        //if (getActionBar() != null)
        //{
        //    Resources r = getResources();
        //    getActionBar().setBackgroundDrawable(r.getDrawable((connected ? R.drawable.bar_background : R.drawable.ab_background_state_disabled)));
        //}
    }

    @Override public void onLowMemory()
    {
        super.onLowMemory();

        // TODO remove
        // for DEBUGGING purpose only
        Fragment currentFragmentName = navigator.getCurrentFragment();
        Timber.e(new RuntimeException("LowMemory " + currentFragmentName), "%s", currentFragmentName);
        ActivityBuildTypeUtil.flagLowMemory();
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
                    BuySellStockFragment.class,
                    CompetitionWebViewFragment.class,
                    DiscoveryMainFragment.class,
                    FacebookShareActivity.class,
                    FriendsInvitationFragment.class,
                    FXInfoFragment.class,
                    FXMainFragment.class,
                    FXMainFragment.class,
                    LeaderboardCommunityFragment.class,
                    MainCompetitionFragment.class,
                    MessagesCenterNewFragment.class,
                    MeTimelineFragment.class,
                    NewsWebFragment.class,
                    NotificationsCenterFragment.class,
                    PositionListFragment.class,
                    ProviderVideoListFragment.class,
                    PushableTimelineFragment.class,
                    SettingsFragment.class,
                    StoreScreenFragment.class,
                    TabbedPositionListFragment.class,
                    TradeListFragment.class,
                    TrendingMainFragment.class,
                    UpdateCenterFragment.class,
                    WebViewFragment.class
            );
            DiscoveryMainFragment.registerAliases(router);
            StoreScreenFragment.registerAliases(router);
            UpdateCenterFragment.registerAliases(router);
            return router;
        }

        @Provides FragmentOuterElements provideFragmentElements(DashboardFragmentOuterElements dashboardFragmentElements)
        {
            return dashboardFragmentElements;
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
