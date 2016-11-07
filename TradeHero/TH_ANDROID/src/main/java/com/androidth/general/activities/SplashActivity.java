package com.androidth.general.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import com.androidth.general.BuildConfig;
import com.androidth.general.R;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserLiveAccount;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.prefs.BooleanPreference;
import com.androidth.general.models.time.AppTiming;
import com.androidth.general.persistence.prefs.AuthHeader;
import com.androidth.general.persistence.prefs.FirstLaunch;
import com.androidth.general.persistence.prefs.ResetHelpScreens;
import com.androidth.general.persistence.user.LiveUserAccountCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.receivers.CustomAirshipReceiver;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.VersionUtils;
import com.androidth.general.utils.metrics.MetricsModule;
import com.androidth.general.utils.metrics.appsflyer.THAppsFlyer;
import com.tapstream.sdk.Config;
import com.tapstream.sdk.Event;
import com.tapstream.sdk.Tapstream;
import com.tune.Tune;
import com.urbanairship.util.UriUtils;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;
import io.realm.Realm;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class SplashActivity extends BaseActivity
{
    @Inject @FirstLaunch BooleanPreference firstLaunchPreference;
    @Inject @ResetHelpScreens BooleanPreference resetHelpScreens;

//    @Inject Lazy<Tapstream> tapStream;
    //TODO Change Analytics
    //@Inject
    //Analytics analytics;
    @Inject @AuthHeader String authToken;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject LiveUserAccountCacheRx liveUserAccountCacheRx;
    @Inject DTOCacheUtilRx dtoCacheUtil;

    @Nullable Subscription userProfileSubscription;
    @Nullable Uri deepLink;

    boolean isFromPush;
    String uaMessage;

    public static final String DEEP_LINK_PROVIDERS_ENROLL = "providers-enroll";

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        AppTiming.splashCreate = System.currentTimeMillis();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);
        TextView appVersion = (TextView) findViewById(R.id.app_version);
        if (appVersion != null)
        {
            appVersion.setText(VersionUtils.getAppVersion(this));
        }
        try
        {
            getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundColor(Color.parseColor("#ffffff"));
        } catch (Throwable e)
        {
            Timber.e(e, "Failed to set guide background");
            getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundColor(
                    getResources().getColor(R.color.authentication_guide_bg_color));
        }

        if(getIntent()!=null && getIntent().getAction()!=null){
            if(getIntent().getAction().equals(CustomAirshipReceiver.NOTIFICATION_OPENED)
                    && getIntent().hasExtra(CustomAirshipReceiver.MESSAGE)){

                isFromPush = true;
                uaMessage = getIntent().getStringExtra(CustomAirshipReceiver.MESSAGE);
            }
        }

        deepLink = getIntent().getData();
        if (deepLink != null && getIntent().getAction().equals(CustomAirshipReceiver.NOTIFICATION_OPENED))
        {
            openDeepLink(getIntent().getData());

        }
    }

    @Override protected void onResume()
    {
        super.onResume();

        //TODO Add code for Google Analytics
        //analytics.tagScreen(AnalyticsConstants.Loading);

//        AppEventsLogger.activateApp(this, SocialConstants.FACEBOOK_APP_ID);//old

        setupTapStream();

        Tune.getInstance().setReferralSources(this);
        Tune.getInstance().measureSession();

        THAppsFlyer.setAppsFlyerKey(this, MetricsModule.APP_FLYER_KEY);
        THAppsFlyer.sendTracking(this);

//        if (!Constants.RELEASE)
//        {
//            VersionUtils.logScreenMeasurements(this);
//        }
        //TODO Add code for Google Analytics
        //analytics.addEvent(new AppLaunchEvent()).addEvent(new SimpleEvent(AnalyticsConstants.LoadingScreen));

        if (firstLaunchPreference.get() || resetHelpScreens.get() || authToken == null)
        {
            if(isFromPush && uaMessage!=null){
                ActivityHelper.launchAuthentication(this, deepLink);
            }else{
                ActivityHelper.launchAuthentication(this, deepLink, uaMessage);
            }

            firstLaunchPreference.set(false);
            resetHelpScreens.set(false);
            finish();
        }
        else
        {
            if(BuildConfig.HAS_LIVE_ACCOUNT_FEATURE){
                //get virtual and live account
                Log.v("Live1b", "getting live account");
                userProfileSubscription = AppObservable.bindActivity(
                        this,
                        userProfileCache.get(currentUserId.toUserBaseKey())
                                .flatMap(new Func1<Pair<UserBaseKey, UserProfileDTO>, Observable<Pair<UserProfileDTO, UserLiveAccount>>>() {
                                    @Override
                                    public Observable<Pair<UserProfileDTO, UserLiveAccount>> call(Pair<UserBaseKey, UserProfileDTO> userBaseKeyUserProfileDTOPair) {
                                        Log.v("Live1b", "getting live account"+userBaseKeyUserProfileDTOPair.first);

                                        return liveUserAccountCacheRx.get(currentUserId.toUserBaseKey())
                                                .flatMap(new Func1<Pair<UserBaseKey, UserLiveAccount>, Observable<Pair<UserProfileDTO, UserLiveAccount>>>() {
                                                    @Override
                                                    public Observable<Pair<UserProfileDTO, UserLiveAccount>> call(Pair<UserBaseKey, UserLiveAccount> userBaseKeyUserLiveAccountPair) {
                                                        Log.v("Live1b", "flatting");
                                                        return Observable.just(new Pair<>(userBaseKeyUserProfileDTOPair.second, userBaseKeyUserLiveAccountPair.second));
                                                    }
                                                })
                                                .onErrorReturn(new Func1<Throwable, Pair<UserProfileDTO, UserLiveAccount>>() {
                                                    @Override
                                                    public Pair<UserProfileDTO, UserLiveAccount> call(Throwable throwable) {
                                                        Log.v("Live1b", "Error return "+throwable.getLocalizedMessage());
                                                        return new Pair<>(userBaseKeyUserProfileDTOPair.second, null);
                                                    }
                                                });

                                    }
                                }))
                        .subscribe(new Action1<Pair<UserProfileDTO, UserLiveAccount>>() {
                            @Override
                            public void call(Pair<UserProfileDTO, UserLiveAccount> userProfileDTOUserLiveAccountPair) {
                                dtoCacheUtil.prefetchesUponLogin(userProfileDTOUserLiveAccountPair.first);
//                                liveUserAccountCacheRx.onNext(currentUserId.toUserBaseKey(), userProfileDTOUserLiveAccountPair.second);

                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                realm.copyToRealm(userProfileDTOUserLiveAccountPair.second);
                                realm.commitTransaction();


                                Log.v("Live1b", "Saving live account: "+currentUserId.toUserBaseKey());
                                Log.v("Live1b", "Logging live account: "+userProfileDTOUserLiveAccountPair.second);

//                                if(userProfileDTOUserLiveAccountPair.second!=null){
//                                    dtoCacheUtil.prefetchesUponLogin(userProfileDTOUserLiveAccountPair.second);
//                                }

                                ActivityHelper.launchDashboardWithFinish(SplashActivity.this, deepLink);
                                finish();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.v("Live1b", "Error login "+throwable.getLocalizedMessage());
                                ActivityHelper.launchAuthentication(SplashActivity.this, deepLink);
                                finish();
                            }
                        });


            }else{
                Log.v("Live1b", "getting virtual");
                //virtual
                userProfileSubscription = AppObservable.bindActivity(
                        this,
                        userProfileCache.get(currentUserId.toUserBaseKey()))
                        .subscribe(
                                new Action1<Pair<UserBaseKey, UserProfileDTO>>()
                                {
                                    @Override public void call(Pair<UserBaseKey, UserProfileDTO> pair)
                                    {
                                        dtoCacheUtil.prefetchesUponLogin(pair.second);
                                        if(isFromPush && uaMessage!=null){
                                            ActivityHelper.launchDashboardWithFinish(SplashActivity.this, deepLink, uaMessage);
                                        }else{
                                            ActivityHelper.launchDashboardWithFinish(SplashActivity.this, deepLink);
                                        }
                                    }
                                },
                                new Action1<Throwable>()
                                {
                                    @Override public void call(Throwable throwable)
                                    {
                                        ActivityHelper.launchAuthentication(SplashActivity.this, deepLink);
                                        finish();
                                    }
                                });
            }

        }
    }

    @Override protected void onPause()
    {
        if (userProfileSubscription != null)
        {
            userProfileSubscription.unsubscribe();
        }
        super.onPause();
    }

    @Override protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Uri newDeepLink = intent.getData();
        deepLink = newDeepLink != null ? newDeepLink : deepLink;
    }

    @Override protected boolean requireLogin()
    {
        return false;
    }

    private void openDeepLink(Uri deepLink){

        String host = deepLink.getHost();//providers-enroll
        Bundle options = new Bundle();
        Map<String, List<String>> queryParameters = UriUtils.getQueryParameters(deepLink);

        if (DEEP_LINK_PROVIDERS_ENROLL.equals(host) && uaMessage!=null) {
            ActivityHelper.launchDashboardWithFinish(this, deepLink, uaMessage);
//            thRouter.open(deepLink.getPath(), getApplicationContext());
        }else{
            if(isFromPush && uaMessage!=null){
                ActivityHelper.launchDashboardWithFinish(this, deepLink, uaMessage);
            }
        }

    }

    private void setupTapStream(){
        Config config = new Config(Constants.TAPSTREAM_APP_NAME, Constants.TAPSTREAM_KEY);
//        config.setFireAutomaticOpenEvent(false);//this will send twice
        config.setInstallEventName(getString(Constants.TAP_STREAM_TYPE.installResId));

        Tapstream.create(getApplication(), config);
        Tapstream.getInstance().fireEvent(new Event("App started", false));//Jeff
//        Tapstream.getInstance().fireEvent(new Event(getString(Constants.TAP_STREAM_TYPE.openResId), false));
    }
}
