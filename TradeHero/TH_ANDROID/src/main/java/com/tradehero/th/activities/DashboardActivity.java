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
import com.tradehero.common.billing.BillingPurchaseRestorer;
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
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
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
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.NavigationAnalyticsReporter;
import com.tradehero.th.fragments.achievement.AbstractAchievementDialogFragment;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.competition.CompetitionEnrollmentBroadcastSignal;
import com.tradehero.th.fragments.competition.CompetitionEnrollmentWebViewFragment;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.ForCompetitionEnrollment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.competition.ProviderVideoListFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.fragments.home.HomeFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.onboarding.ForOnBoard;
import com.tradehero.th.fragments.onboarding.OnBoardDialogFragment;
import com.tradehero.th.fragments.onboarding.OnBoardingBroadcastSignal;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.settings.AboutFragment;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.fragments.settings.AskForReviewSuggestedDialogFragment;
import com.tradehero.th.fragments.settings.ForSendLove;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.messages.MessagesCenterFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationClickHandler;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.notification.NotificationCacheRx;
import com.tradehero.th.persistence.prefs.IsOnBoardShown;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.ui.AppContainer;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.achievement.AchievementModule;
import com.tradehero.th.utils.achievement.ForAchievement;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.level.ForXP;
import com.tradehero.th.utils.level.XpModule;
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
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;
import timber.log.Timber;

public class DashboardActivity extends BaseActivity
        implements ResideMenu.OnMenuListener
{
    private DashboardNavigator navigator;
    @Inject Set<DashboardNavigator.DashboardFragmentWatcher> dashboardFragmentWatchers;

    // It is important to have Lazy here because we set the current Activity after the injection
    // and the LogicHolder creator needs the current Activity...
    @Inject Lazy<THBillingInteractor> billingInteractor;
    @Inject Provider<BaseTHUIBillingRequest.Builder> thUiBillingRequestBuilderProvider;

    private BillingPurchaseRestorer.OnPurchaseRestorerListener purchaseRestorerFinishedListener;
    private Integer restoreRequestCode;

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
    @Inject @ForAchievement IntentFilter achievementIntentFilter;
    @Inject @ForXP IntentFilter xpIntentFilter;
    @Inject @ForOnBoard IntentFilter onBoardIntentFilter;
    @Inject @ForCompetitionEnrollment IntentFilter competitionEnrollmentIntentFilter;
    @Inject @ForSendLove IntentFilter sendLoveIntentFilter;
    @Inject AbstractAchievementDialogFragment.Creator achievementDialogCreator;
    @Inject @IsOnBoardShown BooleanPreference isOnboardShown;
    @Inject @SocialAuth Set<ActivityResultRequester> activityResultRequesters;
    @Inject @ForAnalytics Lazy<DashboardNavigator.DashboardFragmentWatcher> analyticsReporter;

    @Inject Lazy<ProviderListCacheRx> providerListCache;
    private final Set<Integer> enrollmentScreenOpened = new HashSet<>();

    @InjectView(R.id.xp_toast_box) XpToast xpToast;

    @Nullable private Subscription providerListSubscription;
    private Subscription notificationFetchSubscription;

    private ProgressDialog progressDialog;
    private DashboardTabHost dashboardTabHost;
    private int tabHostHeight;
    private BroadcastReceiver mAchievementBroadcastReceiver;
    private BroadcastReceiver mXPBroadcastReceiver;
    private BroadcastReceiver onBoardBroadcastReceiver;
    private BroadcastReceiver enrollmentBroadcastReceiver;
    private BroadcastReceiver sendLoveBroadcastReceiver;
    private BroadcastReceiver onlineStateReceiver;
    private MenuItem networkIndicator;

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

        purchaseRestorerFinishedListener = (requestCode, restoredPurchases, failedRestorePurchases, failExceptions) -> {
            if (Integer.valueOf(requestCode).equals(restoreRequestCode))
            {
                restoreRequestCode = null;
            }
        };
        launchBilling();

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
        mAchievementBroadcastReceiver = new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                if (intent != null && intent.getBundleExtra(AchievementModule.KEY_USER_ACHIEVEMENT_ID) != null)
                {
                    Bundle bundle = intent.getBundleExtra(AchievementModule.KEY_USER_ACHIEVEMENT_ID);
                    achievementDialogCreator.newInstance(new UserAchievementId(bundle))
                            .subscribe(new Observer<AbstractAchievementDialogFragment>()
                            {
                                private boolean isEmpty = false;

                                @Override public void onCompleted()
                                {
                                    if (isEmpty)
                                    {
                                        broadcastUtilsLazy.get().nextPlease();
                                    }
                                }

                                @Override public void onError(Throwable e)
                                {
                                    Timber.e(e, "Error when creating achievement dialog");
                                }

                                @Override public void onNext(AbstractAchievementDialogFragment abstractAchievementDialogFragment)
                                {
                                    isEmpty = false;
                                    abstractAchievementDialogFragment.show(getFragmentManager(), AbstractAchievementDialogFragment.TAG);
                                }
                            });
                }
                else
                {
                    broadcastUtilsLazy.get().nextPlease();
                }
            }
        };

        mXPBroadcastReceiver = new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                if (intent != null && intent.getBundleExtra(XpModule.KEY_XP_BROADCAST) != null)
                {
                    Bundle b = intent.getBundleExtra(XpModule.KEY_XP_BROADCAST);
                    UserXPAchievementDTO userXPAchievementDTO = new UserXPAchievementDTO(b);
                    xpToast.showWhenReady(userXPAchievementDTO);
                }
                else
                {
                    broadcastUtilsLazy.get().nextPlease();
                }
            }
        };

        onBoardBroadcastReceiver = new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                isOnboardShown.set(true);
                OnBoardDialogFragment.showOnBoardDialog(getFragmentManager());
            }
        };

        enrollmentBroadcastReceiver = new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                fetchProviderList();
            }
        };

        sendLoveBroadcastReceiver = new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                AskForReviewSuggestedDialogFragment.showReviewDialog(getFragmentManager());
            }
        };

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
        localBroadcastManager.registerReceiver(mAchievementBroadcastReceiver, achievementIntentFilter);
        localBroadcastManager.registerReceiver(mXPBroadcastReceiver, xpIntentFilter);
        localBroadcastManager.registerReceiver(onBoardBroadcastReceiver, onBoardIntentFilter);
        // get providers for enrollment page
        localBroadcastManager.registerReceiver(enrollmentBroadcastReceiver, competitionEnrollmentIntentFilter);
        localBroadcastManager.registerReceiver(sendLoveBroadcastReceiver, sendLoveIntentFilter);
    }

    protected void fetchProviderList()
    {
        detachSubscription(providerListSubscription);
        providerListSubscription = AndroidObservable.bindActivity(this, providerListCache.get().get(new ProviderListKey()))
                .subscribe(new ProviderListFetchObserver());
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
            notificationFetchSubscription = AndroidObservable.bindActivity(
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
        localBroadcastManager.unregisterReceiver(mAchievementBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mXPBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(onBoardBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(enrollmentBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(sendLoveBroadcastReceiver);
        super.onPause();
    }

    @Override protected void onStop()
    {
        detachNotificationFetchTask();

        super.onStop();
    }

    @Override protected void onDestroy()
    {
        detachSubscription(providerListSubscription);
        providerListSubscription = null;
        purchaseRestorerFinishedListener = null;
        notificationFetchSubscription = null;

        xpToast.destroy();

        mAchievementBroadcastReceiver = null;
        mXPBroadcastReceiver = null;
        onBoardBroadcastReceiver = null;
        enrollmentBroadcastReceiver = null;
        sendLoveBroadcastReceiver = null;

        networkIndicator = null;

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
        AndroidObservable.bindActivity(this,
                userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .first()
                .subscribe(new EmptyObserver<Pair<UserBaseKey, UserProfileDTO>>()
                {
                    @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> args)
                    {
                        if (args.second != null && !isOnboardShown.get())
                        {
                            if (userProfileDTOUtilLazy.get().shouldShowOnBoard(args.second))
                            {
                                broadcastUtilsLazy.get().enqueue(new OnBoardingBroadcastSignal());
                            }
                        }

                        if (isOnboardShown.get())
                        {
                            broadcastUtilsLazy.get().enqueue(new CompetitionEnrollmentBroadcastSignal());
                        }
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
        // Passing it on just in case it is expecting something
        billingInteractor.get().onActivityResult(requestCode, resultCode, data);
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

            NotificationClickHandler notificationClickHandler = new NotificationClickHandler(DashboardActivity.this,
                    pair.second);
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

    protected class ProviderListFetchObserver implements Observer<Pair<ProviderListKey, ProviderDTOList>>
    {
        @Override public void onNext(Pair<ProviderListKey, ProviderDTOList> pair)
        {
            openEnrollmentPageIfNecessary(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_provider_competition_list);
            broadcastUtilsLazy.get().nextPlease();
        }
    }

    private void openEnrollmentPageIfNecessary(ProviderDTOList providerDTOs)
    {
        boolean isHandled = false;
        for (ProviderDTO providerDTO : providerDTOs)
        {
            if (!providerDTO.isUserEnrolled
                    && !enrollmentScreenOpened.contains(providerDTO.id))
            {
                isHandled = true;
                enrollmentScreenOpened.add(providerDTO.id);

                Runnable handleCompetitionRunnable = createHandleCompetitionRunnable(providerDTO);
                runOnUiThread(handleCompetitionRunnable);
                break;
            }
        }
        if (!isHandled)
        {
            broadcastUtilsLazy.get().nextPlease();
        }
    }

    //<editor-fold desc="Competition Runnable">
    private Runnable createHandleCompetitionRunnable(ProviderDTO providerDTO)
    {
        return new TrendingFragmentHandleCompetitionRunnable(providerDTO);
    }

    private class TrendingFragmentHandleCompetitionRunnable implements Runnable
    {
        private final ProviderDTO providerDTO;

        private TrendingFragmentHandleCompetitionRunnable(ProviderDTO providerDTO)
        {
            this.providerDTO = providerDTO;
        }

        @Override public void run()
        {
            if (!isFinishing())
            {
                navigator.pushFragment(CompetitionEnrollmentWebViewFragment.class, providerDTO.getProviderId().getArgs());
            }
        }
    }
    //</editor-fold>

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
