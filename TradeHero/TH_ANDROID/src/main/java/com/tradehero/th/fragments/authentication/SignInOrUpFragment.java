package com.tradehero.th.fragments.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.AuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.ViewObservable;
import rx.functions.Func1;
import rx.observers.EmptyObserver;

import static com.tradehero.th.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;
import static com.tradehero.th.utils.Constants.Auth.PARAM_AUTHTOKEN_TYPE;

public class SignInOrUpFragment extends Fragment
{
    // TODO not to fake this :)
    private static final String FAKE_EMAIL = "thont@live.com";
    @Inject Analytics analytics;
    @Inject AccountManager accountManager;
    @Inject @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> enumToAuthProviderMap;

    @Optional @InjectViews({
            R.id.btn_facebook_signin,
            R.id.btn_twitter_signin,
            R.id.btn_linkedin_signin,
            R.id.btn_weibo_signin,
            R.id.btn_qq_signin,
            R.id.authentication_email_sign_in_link,
            R.id.authentication_email_sign_up_link
    })
    AuthenticationButton[] observableViews;

    private Subscription subscription;

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

        subscription = Observable.from(observableViews)
                .filter(new Func1<AuthenticationButton, Boolean>()
                {
                    @Override public Boolean call(AuthenticationButton authenticationButton)
                    {
                        return authenticationButton != null;
                    }
                })
                .subscribe(new EmptyObserver<AuthenticationButton>()
                {
                    @Override public void onNext(AuthenticationButton authenticationButton)
                    {
                        ViewObservable.clicks(authenticationButton, false)
                                .map(new Func1<AuthenticationButton, SocialNetworkEnum>()
                                {
                                    @Override public SocialNetworkEnum call(AuthenticationButton view)
                                    {
                                        return view.getType();
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
                                .subscribe(new AuthDataObserver());
                    }
                });
    }

    @Override public void onDestroyView()
    {
        subscription.unsubscribe();
        super.onDestroyView();
    }

    @Override public void onResume()
    {
        super.onResume();

        analytics.addEvent(new SimpleEvent(AnalyticsConstants.SignIn));
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

    private class AuthDataObserver implements rx.Observer<AuthData>
    {
        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(new THException(e));
        }

        @Override public void onNext(AuthData authData)
        {
            Account account = getOrAddAccount(authData);
            accountManager.setAuthToken(account, PARAM_AUTHTOKEN_TYPE, authData.getTHToken());
            finishAuthentication(authData);
        }
    }

    private Account getOrAddAccount(AuthData authData)
    {
        Account[] accounts = accountManager.getAccountsByType(PARAM_AUTHTOKEN_TYPE);
        Account account = accounts.length != 0 ? accounts[0] :
                new Account(FAKE_EMAIL, PARAM_ACCOUNT_TYPE);

        if (accounts.length == 0) {
            accountManager.addAccountExplicitly(account, authData.password, null);
        } else {
            accountManager.setPassword(accounts[0], authData.password);
        }
        return account;
    }

    private void finishAuthentication(AuthData authData)
    {
        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, FAKE_EMAIL);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, authData.getTHToken());
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, PARAM_ACCOUNT_TYPE);

        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();

        startActivity(new Intent(getActivity(), DashboardActivity.class));
    }
}
