package com.androidth.general.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.Window;

import com.androidth.general.common.activities.ActivityResultRequester;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.utils.CollectionUtils;
import com.androidth.general.R;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.users.LoginSignUpFormDTO;
import com.androidth.general.api.users.UserLoginDTO;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.auth.AuthData;
import com.androidth.general.auth.AuthDataUtil;
import com.androidth.general.auth.AuthenticationProvider;
import com.androidth.general.auth.SocialAuth;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.authentication.GuideAuthenticationFragment;
import com.androidth.general.fragments.authentication.TwitterEmailFragment;
import com.androidth.general.network.service.SessionServiceWrapper;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.rx.view.DismissDialogAction0;
import com.androidth.general.utils.metrics.appsflyer.AppsFlyerConstants;
import com.androidth.general.utils.metrics.appsflyer.THAppsFlyer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.internal.Assertions;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class AuthenticationActivity extends BaseActivity
{
    //TODO Add code for Google Analytics
    //@Inject Analytics analytics;
    @Inject DTOCacheUtilRx dtoCacheUtil;
    @Inject @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> enumToAuthProviderMap;
    @Inject @SocialAuth Set<ActivityResultRequester> activityResultRequesters;
    @Inject Provider<LoginSignUpFormDTO.Builder2> authenticationFormBuilderProvider;
    @Inject SessionServiceWrapper sessionServiceWrapper;

    private AuthenticationActivityModule activityModule;
    private Subscription socialButtonsSubscription;
    private PublishSubject<SocialNetworkEnum> selectedSocialNetworkSubject;
    private Observable<Pair<AuthData, UserProfileDTO>> authenticationObservable;
    @Nullable Uri deepLink;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_layout);
        //getActionBar().hide();
        setTitle("");

        dtoCacheUtil.clearUserCaches();

        selectedSocialNetworkSubject = PublishSubject.create();
        authenticationObservable = selectedSocialNetworkSubject
                .flatMap(new Func1<SocialNetworkEnum, Observable<? extends Pair<AuthData, UserProfileDTO>>>()
                {
                    @Override public Observable<? extends Pair<AuthData, UserProfileDTO>> call(SocialNetworkEnum socialNetworkEnum)
                    {
                        return handleConnectionRequest(socialNetworkEnum);
                    }
                });

        deepLink = getIntent().getData();
        Bundle args = new Bundle();
        if (deepLink != null)
        {
            GuideAuthenticationFragment.putDeepLink(args, deepLink);
        }
        activityModule.navigator = new DashboardNavigator(this, R.id.fragment_content, GuideAuthenticationFragment.class, 0, args);
    }

    @Override protected void onResume()
    {
        super.onResume();
        setTitle("");
        //TODO Add code for Google Analytics
        //analytics.tagScreen(AnalyticsConstants.Login_Register);
        //analytics.addEvent(new SimpleEvent(AnalyticsConstants.LoginRegisterScreen));

        if (socialButtonsSubscription == null || socialButtonsSubscription.isUnsubscribed())
        {
            resubscribe();
        }
    }

    @NonNull @Override protected List<Object> getModules()
    {
        List<Object> superModules = new ArrayList<>(super.getModules());
        activityModule = new AuthenticationActivityModule();
        superModules.add(activityModule);
        return superModules;
    }

    @Override protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onActivityResult %d, %d, %s", requestCode, resultCode, data);
        CollectionUtils.apply(activityResultRequesters, new Action1<ActivityResultRequester>()
        {
            @Override public void call(ActivityResultRequester requester)
            {
                requester.onActivityResult(AuthenticationActivity.this, requestCode, resultCode, data);
            }
        });
    }

    @Override protected void onDestroy()
    {
        if (socialButtonsSubscription != null)
        {
            // We unsubscribe only onDestroy because we need to account for the fact that we may push the TwitterEmailFragment
            socialButtonsSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override protected boolean requireLogin()
    {
        return false;
    }

    @NonNull public Observer<SocialNetworkEnum> getSelectedSocialNetworkObserver()
    {
        return selectedSocialNetworkSubject;
    }

    @NonNull protected Observable<Pair<AuthData, UserProfileDTO>> handleConnectionRequest(final SocialNetworkEnum socialNetworkEnum)
    {
        final ProgressDialog progressDialog = ProgressDialog.show(
                this,
                getString(R.string.alert_dialog_please_wait),
                getString(R.string.authentication_connecting_to, socialNetworkEnum.getName()),
                true);
        return enumToAuthProviderMap.get(socialNetworkEnum)
                .logIn(this)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<AuthData, Observable<? extends Pair<AuthData, UserProfileDTO>>>()
                {
                    @Override public Observable<? extends Pair<AuthData, UserProfileDTO>> call(AuthData authData)
                    {
                        progressDialog.setMessage(
                                getString(R.string.authentication_connecting_tradehero, authData.socialNetworkEnum.getName()));
                        LoginSignUpFormDTO loginForm = createLoginFormFromAuthData(authData);
                        return safeSignUpAndLogin(loginForm, progressDialog);
                    }
                })
                .doOnError(new TimberAndToastOnErrorAction1("Error on logging in"))
                .doOnNext(new Action1<Pair<AuthData, UserProfileDTO>>()
                {
                    @Override public void call(Pair<AuthData, UserProfileDTO> pair)
                    {
                        trackSocialRegistration(socialNetworkEnum);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog))
                .doOnNext(new Action1<Pair<AuthData, UserProfileDTO>>()
                {
                    @Override public void call(Pair<AuthData, UserProfileDTO> authDataUserProfileDTOPair)
                    {
                        AuthDataUtil.saveAccountAndResult(
                                AuthenticationActivity.this,
                                authDataUserProfileDTOPair.first,
                                authDataUserProfileDTOPair.second.email);
                    }
                })
                .doOnNext(new Action1<Pair<AuthData, UserProfileDTO>>()
                {
                    @Override public void call(Pair<AuthData, UserProfileDTO> authDataUserProfileDTOPair)
                    {
                        ActivityHelper.launchDashboard(AuthenticationActivity.this, deepLink);
                    }
                })
                ;
    }

    @NonNull protected LoginSignUpFormDTO createLoginFormFromAuthData(@NonNull AuthData authData)
    {
        return authenticationFormBuilderProvider.get()
                .authData(authData)
                .build();
    }

    @NonNull protected Observable<Pair<AuthData, UserProfileDTO>> safeSignUpAndLogin(
            @NonNull final LoginSignUpFormDTO loginSignUpFormDTO,
            @NonNull ProgressDialog progressDialog)
    {
        return sessionServiceWrapper.signUpAndLoginOrUpdateTokensRx(loginSignUpFormDTO.authData.getTHToken(), loginSignUpFormDTO)
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new SocialNetworkSpecificExtensions(loginSignUpFormDTO.authData, progressDialog))
                .map(new Func1<UserLoginDTO, Pair<AuthData, UserProfileDTO>>()
                {
                    @Override public Pair<AuthData, UserProfileDTO> call(UserLoginDTO userLoginDTO)
                    {
                        return Pair.create(loginSignUpFormDTO.authData, userLoginDTO.profileDTO);
                    }
                });
    }

    private class SocialNetworkSpecificExtensions implements Func1<Throwable, Observable<UserLoginDTO>>
    {
        @NonNull private final AuthData authData;
        @NonNull private final ProgressDialog progressDialog;

        public SocialNetworkSpecificExtensions(
                @NonNull AuthData authData,
                @NonNull ProgressDialog progressDialog)
        {
            this.authData = authData;
            this.progressDialog = progressDialog;
        }

        @Override public Observable<UserLoginDTO> call(Throwable throwable)
        {
            Assertions.assertUiThread();

            if (authData.socialNetworkEnum == SocialNetworkEnum.TW)
            {
                progressDialog.hide();
                return signUpAndLoginWithTwitterEmail(
                        activityModule.navigator.pushFragment(TwitterEmailFragment.class),
                        authData,
                        progressDialog);
            }

            throw new RuntimeException(throwable);
        }
    }

    @NonNull protected Observable<UserLoginDTO> signUpAndLoginWithTwitterEmail(
            @NonNull TwitterEmailFragment twitterEmailFragment,
            @NonNull final AuthData authData,
            @NonNull final ProgressDialog progressDialog)
    {
        return twitterEmailFragment.obtainEmail()
                .flatMap(new Func1<String, Observable<? extends UserLoginDTO>>()
                {
                    @Override public Observable<? extends UserLoginDTO> call(String email)
                    {
                        LoginSignUpFormDTO loginSignUpFormDTO = authenticationFormBuilderProvider.get()
                                .authData(authData)
                                .email(email)
                                .build();
                        progressDialog.show();
                        return sessionServiceWrapper.signupAndLoginRx(
                                loginSignUpFormDTO.authData.getTHToken(), loginSignUpFormDTO);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new ToastOnErrorAction1())
                .retry()
                .doOnNext(new Action1<UserLoginDTO>()
                {
                    @Override public void call(UserLoginDTO userLoginDTO)
                    {
                        progressDialog.show();
                    }
                })
                .doOnCompleted(new Action0()
                {
                    @Override public void call()
                    {
                        resubscribe();
                    }
                });
    }

    private void resubscribe()
    {
        socialButtonsSubscription = authenticationObservable.subscribe(
                Actions.empty(),
                new Action1<Throwable>()
                {
                    @Override public void call(Throwable e)
                    {
                        // CancellationException
                        // Resubscribe on any type of exception for now
                        if (e instanceof Exception)
                        {
                            resubscribe();
                        }
                    }
                });
    }

    protected void trackSocialRegistration(@NonNull SocialNetworkEnum socialNetworkEnum)
    {
        THAppsFlyer.sendTrackingWithEvent(
                this,
                String.format(AppsFlyerConstants.REGISTRATION_SOCIAL,
                        socialNetworkEnum.name()));
    }
}
