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
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import java.util.Map;
import java.util.concurrent.CancellationException;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.Assertions;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observers.EmptyObserver;
import rx.schedulers.Schedulers;

public class SignInOrUpFragment extends Fragment
{
    @Inject Analytics analytics;
    @Inject DashboardNavigator dashboardNavigator;
    @Inject SessionServiceWrapper sessionServiceWrapper;
    @Inject Provider<LoginSignUpFormDTO.Builder> authenticationFormBuilderProvider;
    @Inject @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> enumToAuthProviderMap;
    @Inject Provider<AuthDataAction> authDataActionProvider;

    @OnClick(R.id.authentication_email_sign_up_link) void handleSignUpButtonClick()
    {
        dashboardNavigator.pushFragment(EmailSignUpFragment.class);
    }

    @Optional @InjectViews({
            R.id.btn_facebook_signin,
            R.id.btn_twitter_signin,
            R.id.btn_linkedin_signin,
            R.id.btn_weibo_signin,
            R.id.btn_qq_signin,
            R.id.authentication_email_sign_in_link
    })
    AuthenticationButton[] observableViews;

    private Subscription subscription;
    private Observable<Pair<AuthData, UserProfileDTO>> authenticationObservable;
    private ProgressDialog progressDialog;
    @Inject Provider<ToastOnErrorAction> toastOnErrorActionProvider;

    @OnClick({
            R.id.txt_term_of_service_signin,
            R.id.txt_term_of_service_termsofuse
    })
    void handleTermOfServiceClick(View view)
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
                .filter(new Func1<AuthenticationButton, Boolean>()
                {
                    @Override public Boolean call(AuthenticationButton authenticationButton)
                    {
                        return authenticationButton != null;
                    }
                })
                .flatMap(new Func1<AuthenticationButton, Observable<AuthenticationButton>>()
                {
                    @Override public Observable<AuthenticationButton> call(AuthenticationButton authenticationButton)
                    {
                        return ViewObservable.clicks(authenticationButton, false);
                    }
                })
                .map(new Func1<AuthenticationButton, SocialNetworkEnum>()
                {
                    @Override public SocialNetworkEnum call(AuthenticationButton view)
                    {
                        return view.getType();
                    }
                })
                .doOnNext(new Action1<SocialNetworkEnum>()
                {
                    @Override public void call(SocialNetworkEnum socialNetworkEnum)
                    {
                        if (socialNetworkEnum != SocialNetworkEnum.TH)
                        {
                            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.alert_dialog_please_wait),
                                    getString(R.string.authentication_connecting_to, socialNetworkEnum.getName()), true);
                        }
                    }
                })
                .map(new Func1<SocialNetworkEnum, AuthenticationProvider>()
                {
                    @Override public AuthenticationProvider call(SocialNetworkEnum socialNetworkEnum)
                    {
                        return enumToAuthProviderMap.get(socialNetworkEnum);
                    }
                })
                .flatMap(new Func1<AuthenticationProvider, Observable<AuthData>>()
                {
                    @Override public Observable<AuthData> call(AuthenticationProvider authenticationProvider)
                    {
                        return authenticationProvider.logIn(getActivity());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<AuthData>()
                {
                    @Override public void call(AuthData authData)
                    {
                        if (progressDialog != null)
                        {
                            progressDialog.setMessage(getString(R.string.authentication_connecting_tradehero, authData.socialNetworkEnum.getName()));
                        }
                    }
                })
                .map(new Func1<AuthData, LoginSignUpFormDTO>()
                {
                    @Override public LoginSignUpFormDTO call(AuthData authData)
                    {
                        return authenticationFormBuilderProvider.get()
                                .authData(authData)
                                .build();
                    }
                })
                .flatMap(new Func1<LoginSignUpFormDTO, Observable<Pair<AuthData, UserProfileDTO>>>()
                {
                    @Override public Observable<Pair<AuthData, UserProfileDTO>> call(LoginSignUpFormDTO loginSignUpFormDTO)
                    {
                        AuthData authData = loginSignUpFormDTO.authData;
                        Observable<UserProfileDTO> userLoginDTOObservable = sessionServiceWrapper.signupAndLoginRx(
                                authData.getTHToken(), loginSignUpFormDTO)
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .onErrorResumeNext(new OperatorSignUpAndLoginFallback(authData))
                                .map(new Func1<UserLoginDTO, UserProfileDTO>()
                                {
                                    @Override public UserProfileDTO call(UserLoginDTO userLoginDTO)
                                    {
                                        return userLoginDTO.profileDTO;
                                    }
                                });

                        return Observable.zip(Observable.just(authData), userLoginDTOObservable,
                                new Func2<AuthData, UserProfileDTO, Pair<AuthData, UserProfileDTO>>()
                                {
                                    @Override public Pair<AuthData, UserProfileDTO> call(AuthData authData, UserProfileDTO userLoginDTO)
                                    {
                                        return Pair.create(authData, userLoginDTO);
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .doOnError(toastOnErrorActionProvider.get())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnUnsubscribe(new Action0()
                {
                    @Override public void call()
                    {
                        if (progressDialog != null)
                        {
                            progressDialog.dismiss();
                        }
                    }
                })
                .doOnNext(authDataActionProvider.get());
    }

    @Override public void onDestroy()
    {
        subscription.unsubscribe();
        ButterKnife.reset(this);
        super.onDestroy();
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

    // TODO better with Observable#retry() ?
    private void resubscribe()
    {
        subscription = authenticationObservable.subscribe(new EmptyObserver<Pair<AuthData, UserProfileDTO>>()
        {
            @Override public void onError(Throwable e)
            {
                if (e instanceof CancellationException)
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
                TwitterEmailFragment twitterEmailFragment = dashboardNavigator.pushFragment(TwitterEmailFragment.class);
                if (progressDialog != null)
                {
                    progressDialog.hide();
                }
                return twitterEmailFragment.obtainEmail()
                        .map(new Func1<String, LoginSignUpFormDTO>()
                        {
                            @Override public LoginSignUpFormDTO call(String email)
                            {
                                return authenticationFormBuilderProvider.get()
                                        .authData(authData)
                                        .email(email)
                                        .build();
                            }
                        })
                        .flatMap(new Func1<LoginSignUpFormDTO, Observable<UserLoginDTO>>()
                        {
                            @Override public Observable<UserLoginDTO> call(LoginSignUpFormDTO loginSignUpFormDTO)
                            {
                                return sessionServiceWrapper.signupAndLoginRx(
                                        loginSignUpFormDTO.authData.getTHToken(), loginSignUpFormDTO);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(toastOnErrorActionProvider.get())
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

            throw new RuntimeException(throwable);
        }
    }
}
