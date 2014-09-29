package com.tradehero.th.fragments.authentication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectViews;
import butterknife.OnClick;
import butterknife.Optional;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.AuthenticationButton;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.AuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observers.EmptyObserver;
import timber.log.Timber;

public class SignInOrUpFragment extends Fragment
{
    @Inject Analytics analytics;
    @Inject View.OnClickListener onAuthenticationButtonClickListener;
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

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
    }

    @Override public void onResume()
    {
        super.onResume();

        analytics.addEvent(new SimpleEvent(AnalyticsConstants.SignIn));
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.authentication_sign_in, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        for (AuthenticationButton observableView: observableViews)
        {
            ViewObservable.clicks(observableView, false)
                    .observeOn(AndroidSchedulers.mainThread())
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
                    .subscribe(new EmptyObserver<AuthData>()
                    {
                        @Override public void onCompleted()
                        {
                            super.onCompleted();
                        }

                        @Override public void onError(Throwable e)
                        {
                            Timber.e(e, "onError");
                        }

                        @Override public void onNext(AuthData authData)
                        {
                            Toast.makeText(getActivity(), "We got: " + authData.accessToken, Toast.LENGTH_LONG).show();
                            Timber.d("Received authData: " + authData);
                        }
                    });
        }
    }

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
}
