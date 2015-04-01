package com.tradehero.th.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.Window;
import com.tradehero.common.activities.ActivityResultRequester;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.AuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.AuthDataAccountAction;
import com.tradehero.th.fragments.authentication.GuideAuthenticationFragment;
import com.tradehero.th.fragments.authentication.OpenDashboardAction;
import com.tradehero.th.fragments.authentication.TwitterEmailFragment;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.appsflyer.AppsFlyerConstants;
import com.tradehero.th.utils.metrics.appsflyer.THAppsFlyer;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Module;
import dagger.Provides;
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
    @Inject Analytics analytics;
    @Inject DTOCacheUtilImpl dtoCacheUtilImpl;
    @Inject @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> enumToAuthProviderMap;
    @Inject @SocialAuth Set<ActivityResultRequester> activityResultRequesters;
    @Inject Provider<LoginSignUpFormDTO.Builder2> authenticationFormBuilderProvider;
    @Inject SessionServiceWrapper sessionServiceWrapper;
    @Inject THAppsFlyer thAppsFlyer;
    @Inject Provider<AuthDataAccountAction> authDataAccountSaveActionProvider;

    private DashboardNavigator navigator;
    private Subscription socialButtonsSubscription;
    private PublishSubject<SocialNetworkEnum> selectedSocialNetworkSubject;
    private Observable<Pair<AuthData, UserProfileDTO>> authenticationObservable;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_layout);

        dtoCacheUtilImpl.clearUserCaches();

        selectedSocialNetworkSubject = PublishSubject.create();
        authenticationObservable = selectedSocialNetworkSubject
                .flatMap(new Func1<SocialNetworkEnum, Observable<? extends Pair<AuthData, UserProfileDTO>>>()
                {
                    @Override public Observable<? extends Pair<AuthData, UserProfileDTO>> call(SocialNetworkEnum socialNetworkEnum)
                    {
                        return handleConnectionRequest(socialNetworkEnum);
                    }
                });

        navigator = new DashboardNavigator(this, R.id.fragment_content, GuideAuthenticationFragment.class, 0);
    }

    @Override protected void onResume()
    {
        super.onResume();
        analytics.tagScreen(AnalyticsConstants.Login_Register);
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.LoginRegisterScreen));

        if (socialButtonsSubscription == null || socialButtonsSubscription.isUnsubscribed())
        {
            resubscribe();
        }
    }

    @NonNull @Override protected List<Object> getModules()
    {
        List<Object> superModules = new ArrayList<>(super.getModules());
        superModules.add(new AuthenticationActivityModule());
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
                requester.onActivityResult(requestCode, resultCode, data);
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
                .doOnError(new ToastAndLogOnErrorAction("Error on logging in"))
                .doOnNext(new Action1<Pair<AuthData, UserProfileDTO>>()
                {
                    @Override public void call(Pair<AuthData, UserProfileDTO> pair)
                    {
                        trackSocialRegistration(socialNetworkEnum);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog))
                .doOnNext(authDataAccountSaveActionProvider.get())
                .doOnNext(new OpenDashboardAction(this))
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
                        navigator.pushFragment(TwitterEmailFragment.class),
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
                .doOnError(new ToastOnErrorAction())
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
        thAppsFlyer.sendTrackingWithEvent(
                String.format(AppsFlyerConstants.REGISTRATION_SOCIAL, socialNetworkEnum.name()));
    }

    @Module(
            addsTo = AppModule.class,
            library = true,
            complete = false,
            overrides = true
    )
    public class AuthenticationActivityModule
    {
        @Provides DashboardNavigator provideDashboardNavigator()
        {
            return navigator;
        }
    }
}
