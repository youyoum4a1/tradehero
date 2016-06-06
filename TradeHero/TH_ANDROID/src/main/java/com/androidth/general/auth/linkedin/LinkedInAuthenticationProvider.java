package com.androidth.general.auth.linkedin;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.webkit.CookieSyncManager;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.auth.AuthData;
import com.androidth.general.auth.SocialAuthenticationProvider;
import com.androidth.general.auth.operator.OperatorOAuthDialog;
import com.androidth.general.network.service.SocialLinker;
import com.androidth.general.network.share.SocialConstants;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.inject.Inject;
import javax.inject.Singleton;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class LinkedInAuthenticationProvider extends SocialAuthenticationProvider
{
    public static final String AUTH_TOKEN_SECRET_KEY = "auth_token_secret";
    public static final String AUTH_TOKEN_KEY = "auth_token";
    public static final String CONSUMER_KEY_KEY = "consumer_key";
    public static final String CONSUMER_SECRET_KEY = "consumer_secret";

    private static final String REQUEST_TOKEN_URL = "https://www.linkedin.com/uas/oauth/requestToken";
    private static final String ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth/accessToken";
    private static final String AUTHORIZE_URL = "https://www.linkedin.com/uas/oauth/authorize";
    private static final String PERMISSION_SCOPE = "r_basicprofile r_emailaddress w_share";
    // OK r_basicprofile r_emailaddress
    // Not OK w_messages r_network
    // Unavailable to us: r_contactinfo
    // Deprecated rw_nus, replaced with w_share
    private static final String CALLBACK_URL = "x-oauthflow-linkedin://callback";
    private static final String SERVICE_URL_ID = "www.linkedin";

    @NonNull final CommonsHttpOAuthProvider oAuthProvider;
    @NonNull final CommonsHttpOAuthConsumer oAuthConsumer;

    @Inject public LinkedInAuthenticationProvider(
            @NonNull SocialLinker socialLinker)
    {
        super(socialLinker);
        this.oAuthProvider = createOAuthProvider();
        this.oAuthConsumer = createOAuthConsumer();
    }

    @NonNull private CommonsHttpOAuthProvider createOAuthProvider()
    {
        String scope = null;
        try
        {
            scope = URLEncoder.encode(PERMISSION_SCOPE, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            Timber.e(e, "Failed to encode %s", PERMISSION_SCOPE);
        }

        return new CommonsHttpOAuthProvider(
                REQUEST_TOKEN_URL + "?scope=" + scope,
                ACCESS_TOKEN_URL,
                AUTHORIZE_URL);
    }

    @NonNull private CommonsHttpOAuthConsumer createOAuthConsumer()
    {
        return new CommonsHttpOAuthConsumer(SocialConstants.LINKEDIN_CONSUMER_KEY, SocialConstants.LINKEDIN_CONSUMER_SECRET);
    }

    @Override public Observable<AuthData> createAuthDataObservable(final Activity activity)
    {
        return Observable.just(1)
                .observeOn(Schedulers.io())
                .map(new Func1<Integer, String>()
                {
                    @Override public String call(Integer network)
                    {
                        try
                        {
                            String requestToken = oAuthProvider.retrieveRequestToken(oAuthConsumer, CALLBACK_URL);
                            CookieSyncManager.createInstance(activity);
                            return requestToken;
                        }
                        catch (Throwable e)
                        {
                            throw new LinkedInRetrieveRequestTokenException(e);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<String, Observable<String>>()
                {
                    @Override public Observable<String> call(final String tokenRequestUrl)
                    {
                        return Observable.create(
                                new OperatorOAuthDialog(
                                        activity,
                                        tokenRequestUrl,
                                        CALLBACK_URL,
                                        SERVICE_URL_ID)
                        );
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Func1<String, AuthData>()
                {
                    @Override public AuthData call(String verifier)
                    {
                        try
                        {
                            Timber.d("Verifier: " + verifier);
                            oAuthProvider.retrieveAccessToken(oAuthConsumer, verifier);
                            // TODO lot of information can be extracted from response parameters
                            oAuthProvider.getResponseParameters();
                            return new AuthData(
                                    SocialNetworkEnum.LN,
                                    null,
                                    oAuthConsumer.getToken(),
                                    oAuthConsumer.getTokenSecret());
                        }
                        catch (Throwable e)
                        {
                            throw new LinkedInRetrieveAccessTokenException(e);
                        }
                    }
                });
    }

    @Override public void logout()
    {
        oAuthConsumer.setTokenWithSecret(null, null);
    }
}
