package com.tradehero.th.fragments.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectViews;
import butterknife.OnClick;
import butterknife.Optional;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.AuthenticationButton;
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
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.appsflyer.AppsFlyerConstants;
import com.tradehero.th.utils.metrics.appsflyer.THAppsFlyer;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Subscription;
import rx.android.internal.Assertions;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SignInOrUpFragment extends Fragment
{
    @Inject Analytics analytics;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject SessionServiceWrapper sessionServiceWrapper;
    @Inject Provider<LoginSignUpFormDTO.Builder2> authenticationFormBuilderProvider;
    @Inject @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> enumToAuthProviderMap;
    @Inject Provider<AuthDataAccountAction> authDataAccountSaveActionProvider;
    @Inject THAppsFlyer thAppsFlyer;

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.authentication_email_sign_up_link)
    void handleSignUpButtonClick()
    {
        navigator.get().pushFragment(EmailSignUpFragment.class);
        socialButtonsSubscription.unsubscribe();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.authentication_email_sign_in_link)
    void handleEmailSignInButtonClick()
    {
        navigator.get().pushFragment(EmailSignInFragment.class);
        socialButtonsSubscription.unsubscribe();
    }

    @Optional @InjectViews({
            R.id.btn_facebook_signin,
            R.id.btn_twitter_signin,
            R.id.btn_linkedin_signin,
            R.id.btn_weibo_signin,
            R.id.btn_qq_signin
    })
    AuthenticationButton[] observableViews;

    private Subscription socialButtonsSubscription;
    private Observable<Pair<AuthData, UserProfileDTO>> authenticationObservable;

    @SuppressWarnings("UnusedDeclaration")
    @OnClick({
            R.id.txt_term_of_service_signin,
            R.id.txt_term_of_service_termsofuse
    }) void handleTermOfServiceClick(View view)
    {
        String url = null;
        switch (view.getId())
        {
            case R.id.txt_term_of_service_signin:
                url = Constants.PRIVACY_TERMS_OF_SERVICE;
                break;
            case R.id.txt_term_of_service_termsofuse:
                url = Constants.PRIVACY_TERMS_OF_USE;
                break;
        }

        openWebPage(url);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.authentication_sign_in, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        authenticationObservable = getClickedSocialNetwork()
                .flatMap(new Func1<SocialNetworkEnum, Observable<? extends Pair<AuthData, UserProfileDTO>>>()
                {
                    @Override public Observable<? extends Pair<AuthData, UserProfileDTO>> call(SocialNetworkEnum socialNetworkEnum)
                    {
                        return SignInOrUpFragment.this.handleConnectionRequest(socialNetworkEnum);
                    }
                });
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.SignIn));

        if (socialButtonsSubscription == null || socialButtonsSubscription.isUnsubscribed())
        {
            resubscribe();
        }
    }

    @Override public void onDestroy()
    {
        if (socialButtonsSubscription != null)
        {
            // We unsubscribe only onDestroy because we need to account for the fact that we may push the TwitterEmailFragment
            socialButtonsSubscription.unsubscribe();
        }
        ButterKnife.reset(this);
        super.onDestroy();
    }

    @NonNull protected Observable<SocialNetworkEnum> getClickedSocialNetwork()
    {
        return Observable.from(observableViews)
                .flatMap(new Func1<AuthenticationButton, Observable<? extends OnClickEvent>>()
                {
                    @Override public Observable<? extends OnClickEvent> call(AuthenticationButton authenticationButton)
                    {
                        if (authenticationButton != null)
                        {
                            return ViewObservable.clicks(authenticationButton, false);
                        }
                        return Observable.empty();
                    }
                })
                .map(new Func1<OnClickEvent, SocialNetworkEnum>()
                {
                    @Override public SocialNetworkEnum call(OnClickEvent event)
                    {
                        return ((AuthenticationButton) event.view()).getType();
                    }
                });
    }

    @NonNull protected Observable<Pair<AuthData, UserProfileDTO>> handleConnectionRequest(final SocialNetworkEnum socialNetworkEnum)
    {
        final ProgressDialog progressDialog = ProgressDialog.show(
                getActivity(),
                getString(R.string.alert_dialog_please_wait),
                getString(R.string.authentication_connecting_to, socialNetworkEnum.getName()),
                true);
        return enumToAuthProviderMap.get(socialNetworkEnum)
                .logIn(SignInOrUpFragment.this.getActivity())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<AuthData, Observable<? extends Pair<AuthData, UserProfileDTO>>>()
                {
                    @Override public Observable<? extends Pair<AuthData, UserProfileDTO>> call(AuthData authData)
                    {
                        progressDialog.setMessage(
                                getString(R.string.authentication_connecting_tradehero, authData.socialNetworkEnum.getName()));
                        LoginSignUpFormDTO loginForm = SignInOrUpFragment.this.createLoginFormFromAuthData(authData);
                        return SignInOrUpFragment.this.safeSignUpAndLogin(loginForm, progressDialog);
                    }
                })
                .doOnError(new ToastAndLogOnErrorAction("Error on logging in"))
                .doOnNext(new Action1<Pair<AuthData, UserProfileDTO>>()
                {
                    @Override public void call(Pair<AuthData, UserProfileDTO> pair)
                    {
                        SignInOrUpFragment.this.trackSocialRegistration(socialNetworkEnum);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog))
                .doOnNext(authDataAccountSaveActionProvider.get())
                .doOnNext(new OpenDashboardAction(getActivity()))
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

    protected void trackSocialRegistration(@NonNull SocialNetworkEnum socialNetworkEnum)
    {
        thAppsFlyer.sendTrackingWithEvent(
                String.format(AppsFlyerConstants.REGISTRATION_SOCIAL, socialNetworkEnum.name()));
    }

    // TODO better with Observable#retry() ?
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
                            SignInOrUpFragment.this.resubscribe();
                        }
                    }
                });
    }

    private void openWebPage(String url)
    {
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        try
        {
            startActivity(it);
        } catch (android.content.ActivityNotFoundException e)
        {
            THToast.show("Unable to open url: " + uri);
        }
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
                        navigator.get().pushFragment(TwitterEmailFragment.class),
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
                        SignInOrUpFragment.this.resubscribe();
                    }
                });
    }
}
