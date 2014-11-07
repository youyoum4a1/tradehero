package com.tradehero.th.fragments.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.Constants;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.Assertions;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observers.EmptyObserver;
import rx.schedulers.Schedulers;

public class SignInOrUpFragment extends Fragment
{
    @Inject Analytics analytics;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject SessionServiceWrapper sessionServiceWrapper;
    @Inject Provider<LoginSignUpFormDTO.Builder2> authenticationFormBuilderProvider;
    @Inject @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> enumToAuthProviderMap;
    @Inject Provider<AuthDataAction> authDataActionProvider;

    @OnClick(R.id.authentication_email_sign_up_link) void handleSignUpButtonClick()
    {
        navigator.get().pushFragment(EmailSignUpFragment.class);
        subscription.unsubscribe();
    }

    @OnClick(R.id.authentication_email_sign_in_link) void handleEmailSignInButtonClick()
    {
        navigator.get().pushFragment(EmailSignInFragment.class);
        subscription.unsubscribe();
    }

    @Optional @InjectViews({
            R.id.btn_facebook_signin,
            R.id.btn_twitter_signin,
            R.id.btn_linkedin_signin,
            R.id.btn_weibo_signin,
            R.id.btn_qq_signin
    })
    AuthenticationButton[] observableViews;

    private Subscription subscription;
    private Observable<Pair<AuthData, UserProfileDTO>> authenticationObservable;
    private ProgressDialog progressDialog;
    @Inject Provider<ToastOnErrorAction> toastOnErrorActionProvider;

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

        authenticationObservable = Observable.from(observableViews)
                .filter(authenticationButton -> authenticationButton != null)
                .flatMap(authenticationButton -> ViewObservable.clicks(authenticationButton, false))
                .map(AuthenticationButton::getType)
                .doOnNext(socialNetworkEnum -> progressDialog = ProgressDialog.show(getActivity(), getString(R.string.alert_dialog_please_wait),
                        getString(R.string.authentication_connecting_to, socialNetworkEnum.getName()),
                        true))
                .map(enumToAuthProviderMap::get)
                .flatMap(authenticationProvider -> authenticationProvider.logIn(getActivity()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(authData -> {
                    if (progressDialog != null)
                    {
                        progressDialog.setMessage(getString(R.string.authentication_connecting_tradehero, authData.socialNetworkEnum.getName()));
                    }
                })
                .map(authData -> authenticationFormBuilderProvider.get()
                        .authData(authData)
                        .build())
                .flatMap(loginSignUpFormDTO -> {
                    AuthData authData = loginSignUpFormDTO.authData;
                    Observable<UserProfileDTO> userLoginDTOObservable = sessionServiceWrapper.signupAndLoginRx(
                            authData.getTHToken(), loginSignUpFormDTO)
                            .retry((integer, throwable) -> {
                                THException thException = new THException(throwable);
                                if (thException.getCode() == THException.ExceptionCode.RenewSocialToken)
                                {
                                    try
                                    {
                                        sessionServiceWrapper.updateAuthorizationTokens(loginSignUpFormDTO);
                                        return true;
                                    }
                                    catch (Exception ignored)
                                    {
                                        return false;
                                    }
                                }
                                return false;
                            })
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorResumeNext(new OperatorSignUpAndLoginFallback(authData))
                            .map(userLoginDTO -> userLoginDTO.profileDTO);

                    return Observable.zip(Observable.just(authData), userLoginDTOObservable, Pair::create)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                })
                .doOnError(toastOnErrorActionProvider.get())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnUnsubscribe(() -> {
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }
                })
                .doOnNext(authDataActionProvider.get())
                .doOnNext(new OpenDashboardAction(getActivity()))
        ;
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.SignIn));

        if (subscription == null || subscription.isUnsubscribed())
        {
            resubscribe();
        }
    }

    @Override public void onDestroy()
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
        ButterKnife.reset(this);
        super.onDestroy();
    }

    // TODO better with Observable#retry() ?
    private void resubscribe()
    {
        subscription = authenticationObservable.subscribe(new EmptyObserver<Pair<AuthData, UserProfileDTO>>()
        {
            @Override public void onError(Throwable e)
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

    private void openWebPage(String url)
    {
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        try
        {
            startActivity(it);
        }
        catch (android.content.ActivityNotFoundException e)
        {
            THToast.show("Unable to open url: " + uri);
        }
    }

    private class OperatorSignUpAndLoginFallback implements Func1<Throwable, Observable<UserLoginDTO>>
    {
        private final AuthData authData;

        public OperatorSignUpAndLoginFallback(AuthData authData)
        {
            this.authData = authData;
        }

        @Override public Observable<UserLoginDTO> call(Throwable throwable)
        {
            Assertions.assertUiThread();

            if (authData.socialNetworkEnum == SocialNetworkEnum.TW)
            {
                TwitterEmailFragment twitterEmailFragment = navigator.get().pushFragment(TwitterEmailFragment.class);
                if (progressDialog != null)
                {
                    progressDialog.hide();
                }
                return twitterEmailFragment.obtainEmail()
                        .map(email -> authenticationFormBuilderProvider.get()
                                .authData(authData)
                                .email(email)
                                .build())
                        .doOnNext(loginSignUpFormDTO -> {
                            if (progressDialog != null)
                            {
                                progressDialog.show();
                            }
                        })
                        .flatMap(loginSignUpFormDTO -> sessionServiceWrapper.signupAndLoginRx(
                                loginSignUpFormDTO.authData.getTHToken(), loginSignUpFormDTO))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(toastOnErrorActionProvider.get())
                        .retry()
                        .doOnNext(userLoginDTO -> progressDialog.show())
                        .doOnCompleted(() -> resubscribe());
            }

            throw new RuntimeException(throwable);
        }
    }
}
