package com.tradehero.th.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TabHost;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.activities.ActivityResultRequester;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.common.utils.OnlineStateReceiver;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
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
import com.tradehero.th.fragments.competition.CompetitionEnrollmentBroadcastSignal;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.dashboard.DrawerLayoutUtil;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.fragments.fxonboard.FxOnBoardDialogFragment;
import com.tradehero.th.fragments.onboarding.OnBoardingBroadcastSignal;
import com.tradehero.th.fragments.settings.AskForReviewSuggestedDialogFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationClickHandler;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.notification.NotificationCacheRx;
import com.tradehero.th.persistence.prefs.IsFxShown;
import com.tradehero.th.persistence.prefs.IsOnBoardShown;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.rx.view.DismissDialogAction1;
import com.tradehero.th.ui.LeftDrawerMenuItemClickListener;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.metrics.ForAnalytics;
import com.tradehero.th.utils.metrics.appsflyer.THAppsFlyer;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import rx.Notification;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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
        implements AchievementAcceptor
{
    @Inject Set<DashboardNavigator.DashboardFragmentWatcher> dashboardFragmentWatchers;

    // It is important to have Lazy here because we set the current Activity after the injection
    // and the LogicHolder creator needs the current Activity...
    @Inject Lazy<THBillingInteractorRx> billingInteractorRx;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Lazy<UserProfileDTOUtil> userProfileDTOUtilLazy;
    @Inject Lazy<NotificationCacheRx> notificationCache;
    @Inject SystemStatusCache systemStatusCache;

    @Inject THRouter thRouter;
    //TODO Add code for Google Analytics
    //@Inject Analytics analytics;
    @Inject Lazy<BroadcastUtils> broadcastUtilsLazy;
    @Inject @IsOnBoardShown BooleanPreference isOnBoardShown;
    @Inject @IsFxShown BooleanPreference isFxShown;
    @Inject Set<ActivityResultRequester> activityResultRequesters;
    @Inject @ForAnalytics Lazy<DashboardNavigator.DashboardFragmentWatcher> analyticsReporter;
    @Inject ProviderUtil providerUtil;
    @Inject LeftDrawerMenuItemClickListener leftDrawerMenuItemClickListener;

    @Inject Lazy<ProviderListCacheRx> providerListCache;
    private final Set<Integer> enrollmentScreenOpened = new HashSet<>();
    private boolean enrollmentScreenIsOpened = false;

    @Bind(R.id.my_toolbar) Toolbar toolbar;
    @Bind(R.id.dashboard_drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.drawer_content_container) ViewGroup drawerContents;
    @Bind(R.id.left_drawer) ViewGroup leftDrawerContainer;
    @Bind(android.R.id.tabhost) DashboardTabHost dashboardTabHost;

    private Subscription notificationFetchSubscription;

    //TODO Add code for Google Analytics
    private DashboardActivityModule activityModule;
    private BroadcastReceiver onlineStateReceiver;
    private MenuItem networkIndicator;
    private CompositeSubscription onDestroySubscriptions;
    private CompositeSubscription onPauseSubscriptions;

    private LiveActivityUtil liveActivityUtil;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        onDestroySubscriptions = new CompositeSubscription();
        AppTiming.dashboardCreate = System.currentTimeMillis();
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);
        //TODO Add code for Google Analytics
        //activityModule.analytics = analytics;
        setContentView(R.layout.dashboard_with_bottom_bar);

        ActivityBuildTypeUtil.setUpCrashReports(currentUserId.toUserBaseKey());

        if (Constants.RELEASE)
        {
            launchBilling();
        }

        // TODO better staggering of starting popups.
        suggestUpgradeIfNecessary();
        showStartDialogsPlease();

        ButterKnife.bind(this);

        liveActivityUtil = new LiveActivityUtil(this);
        activityModule.liveActivityUtil = liveActivityUtil;

        activityModule.drawerLayout = drawerLayout;

        activityModule.toolbar = toolbar;
        setSupportActionBar(toolbar);

        setupDrawerLayout();

        activityModule.tabHostHeight = (int) getResources().getDimension(R.dimen.dashboard_tabhost_height);
        setupNavigator();
        setupDashboardTabHost();

        if (savedInstanceState == null && activityModule.navigator.getCurrentFragment() == null)
        {
            activityModule.navigator.goToTab(RootFragmentType.getInitialTab());
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
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        liveActivityUtil.onCreateOptionsMenu(menu);
        return true;
    }

    @Override public void supportInvalidateOptionsMenu()
    {
        super.supportInvalidateOptionsMenu();
        if (liveActivityUtil != null)
        {
            liveActivityUtil.supportInvalidateOptionsMenu();
        }
    }

    private void setupNavigator()
    {
        activityModule.navigator = new DashboardNavigator(this, R.id.realtabcontent);
        CollectionUtils.apply(dashboardFragmentWatchers, new Action1<DashboardNavigator.DashboardFragmentWatcher>()
        {
            @Override public void call(DashboardNavigator.DashboardFragmentWatcher watcher)
            {
                activityModule.navigator.addDashboardFragmentWatcher(watcher);
            }
        });
    }

    private void setupDashboardTabHost()
    {
        activityModule.dashboardTabHost = dashboardTabHost;
        activityModule.dashboardTabHost.setup();
        activityModule.dashboardTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            @Override public void onTabChanged(String tabId)
            {
                try
                {
                    RootFragmentType selectedFragmentType = RootFragmentType.valueOf(tabId);
                    activityModule.navigator.goToTab(selectedFragmentType);
                } catch (IllegalStateException e)
                {
                    Timber.d("setOnTabChangedListener goToTab " + e.toString());
                }
            }
        });
        activityModule.navigator.addDashboardFragmentWatcher(analyticsReporter.get());
        activityModule.navigator.addDashboardFragmentWatcher(activityModule.dashboardTabHost);
    }

    private void setupDrawerLayout()
    {
        try
        {
            getWindow().getDecorView().findViewById(R.id.drawer_bg_image).setBackgroundResource(R.drawable.login_bg_1);
        } catch (Throwable e)
        {
            Timber.e(e, "Failed to set drawer background");
            getWindow().getDecorView().findViewById(R.id.left_drawer).setBackgroundColor(
                    getResources().getColor(R.color.authentication_guide_bg_color));
        }

        //Setup Drawer Layout.
        activityModule.drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.setDrawerListener(activityModule.drawerToggle);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        onDestroySubscriptions.add(currentUserId.getKeyObservable()
                .flatMap(new Func1<Integer, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(Integer userId)
                    {
                        return userProfileCache.get().getOne(new UserBaseKey(userId))
                                .map(new PairGetSecond<UserBaseKey, UserProfileDTO>());
                    }
                })
                .map(new Func1<UserProfileDTO, Collection<RootFragmentType>>()
                {
                    @Override public Collection<RootFragmentType> call(UserProfileDTO userProfileDTO)
                    {
                        Collection<RootFragmentType> menus = new LinkedHashSet<>(RootFragmentType.forLeftDrawer());
                        if (userProfileDTO != null && userProfileDTO.isAdmin)
                        {
                            menus.add(RootFragmentType.ADMIN_SETTINGS);
                        }
                        return menus;
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Collection<RootFragmentType>>>()
                {
                    @Override public Observable<? extends Collection<RootFragmentType>> call(Throwable throwable)
                    {
                        return Observable.just(RootFragmentType.forLeftDrawer());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Collection<RootFragmentType>>()
                        {
                            @Override public void call(Collection<RootFragmentType> fragmentTypes)
                            {
                                for (RootFragmentType fragmentType : fragmentTypes)
                                {
                                    View content = DrawerLayoutUtil.createDrawerItemFromTabType(DashboardActivity.this, drawerLayout, fragmentType);
                                    content.setOnClickListener(leftDrawerMenuItemClickListener);
                                    drawerContents.addView(content);
                                }
                            }
                        },
                        new TimberOnErrorAction1("Failed to load drawer")));

        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.tradehero_blue_status_bar));

        int width = DeviceUtil.getScreenWidth(this);
        Integer actionBarHeight = getActionBarHeight();
        if (actionBarHeight != null)
        {
            int maxWidth = getResources().getDimensionPixelSize(R.dimen.max_drawer_size);
            int drawerWidth = width - actionBarHeight;
            leftDrawerContainer.getLayoutParams().width = drawerWidth > maxWidth ? maxWidth : drawerWidth;
        }
    }

    @Nullable private Integer getActionBarHeight()
    {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return null;
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

    private void launchBilling()
    {
        // TODO fetch more stuff?
        //noinspection unchecked
        onDestroySubscriptions.add(bindActivity(
                this,
                billingInteractorRx.get().restorePurchasesAndClear(false))
                .subscribe(
                        new EmptyAction1<OnDialogClickEvent>(),
                        new TimberOnErrorAction1("Failed to restore")));
    }

    @Override public void onBackPressed()
    {
        activityModule.navigator.popFragment();
    }

    private void suggestUpgradeIfNecessary()
    {
        if (getIntent() != null && getIntent().getBooleanExtra(UserLoginDTO.SUGGEST_UPGRADE, false))
        {
            showUpgradeDialog();
        }
    }

    @Override protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        activityModule.drawerToggle.syncState();
    }

    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        activityModule.drawerToggle.onConfigurationChanged(newConfig);
    }

    private void pushFragmentIfNecessary(Class<? extends Fragment> fragmentClass)
    {
        Fragment currentDashboardFragment = activityModule.navigator.getCurrentFragment();
        if (!(fragmentClass.isInstance(currentDashboardFragment)))
        {
            activityModule.navigator.pushFragment(fragmentClass);
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

        onPauseSubscriptions = new CompositeSubscription();

        onPauseSubscriptions.add(fromLocalBroadcast(this, ONBOARD_INTENT_FILTER)
                .subscribe(
                        new Action1<Intent>()
                        {
                            @Override public void call(Intent intent)
                            {
                                isOnBoardShown.set(true);
                                activityModule.navigator.launchActivity(OnBoardActivity.class);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                broadcastUtilsLazy.get().nextPlease();
                            }
                        }));

        if (!launchActions(getIntent()))
        {
            // get providers for enrollment page
            onPauseSubscriptions.add(bindActivity(this, fromLocalBroadcast(this, ENROLLMENT_INTENT_FILTER)
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
                                                activityModule.navigator.pushFragment(CompetitionWebViewFragment.class, args);
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
        }

        onPauseSubscriptions.add(fromLocalBroadcast(this, SEND_LOVE_INTENT_FILTER)
                .subscribe(new Action1<Intent>()
                {
                    @Override public void call(Intent intent)
                    {
                        AskForReviewSuggestedDialogFragment.showReviewDialog(DashboardActivity.this.getSupportFragmentManager());
                    }
                }, new EmptyAction1<Throwable>()));
    }

    @Override protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        processNotificationDataIfPresence(extras);
        launchActions(intent);
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
                            new ToastOnErrorAction1());
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
        onPauseSubscriptions.unsubscribe();
        super.onPause();
    }

    @Override protected void onStop()
    {
        detachNotificationFetchTask();
        drawerLayout.closeDrawers();
        super.onStop();
    }

    @Override protected void onDestroy()
    {
        onDestroySubscriptions.unsubscribe();
        notificationFetchSubscription = null;

        networkIndicator = null;

        if (activityModule.navigator != null)
        {
            activityModule.navigator.onDestroy();
        }
        activityModule.navigator = null;

        localBroadcastManager.unregisterReceiver(onlineStateReceiver);

        ButterKnife.unbind(this);

        liveActivityUtil.onDestroy();
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
                                if (!isOnBoardShown.get() && userProfileDTO != null && userProfileDTOUtilLazy.get().shouldShowOnBoard(userProfileDTO))
                                {
                                    broadcastUtilsLazy.get().enqueue(new OnBoardingBroadcastSignal());
                                    return;
                                }

                                if (!isFxShown.get() && userProfileDTO != null && userProfileDTO.fxPortfolio == null)
                                {
                                    isFxShown.set(true);
                                    FxOnBoardDialogFragment.showOnBoardDialog(DashboardActivity.this.getSupportFragmentManager());
                                    return;
                                }

                                broadcastUtilsLazy.get().enqueue(new CompetitionEnrollmentBroadcastSignal());
                            }
                        },
                        new ToastOnErrorAction1());
    }

    @NonNull @Override protected List<Object> getModules()
    {
        List<Object> superModules = new ArrayList<>(super.getModules());
        activityModule = new DashboardActivityModule();
        superModules.add(activityModule);
        return superModules;
    }

    private boolean launchActions(Intent intent)
    {
        if (intent == null)
        {
            return false;
        }

        Uri data = intent.getData();
        if (data != null)
        {
            thRouter.open(data, null, this);
            intent.setData(null);
            drawerLayout.closeDrawers();
            return true;
        }
        return false;
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
            thRouter.open(routeParams.deepLink, routeParams.extras, this);
        }
        Fragment currentFragment = activityModule.navigator.getCurrentFragment();
        if (currentFragment != null)
        {
            currentFragment.onActivityResult(requestCode, resultCode, data);
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
        Fragment currentFragmentName = activityModule.navigator.getCurrentFragment();
        Timber.e(new RuntimeException("LowMemory " + currentFragmentName), "%s", currentFragmentName);
        ActivityBuildTypeUtil.flagLowMemory();
    }
}
