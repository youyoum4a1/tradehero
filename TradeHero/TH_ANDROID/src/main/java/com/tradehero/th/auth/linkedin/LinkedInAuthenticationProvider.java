package com.tradehero.th.auth.linkedin;

import android.app.Activity;
import android.content.Context;
import android.webkit.CookieSyncManager;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.auth.operator.ConsumerKey;
import com.tradehero.th.auth.operator.ConsumerSecret;
import com.tradehero.th.auth.operator.LinkedIn;
import com.tradehero.th.auth.operator.OperatorOAuthDialog;
import com.tradehero.th.base.JSONCredentials;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.inject.Inject;
import javax.inject.Singleton;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
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
    private static final String PERMISSION_SCOPE = "r_basicprofile r_emailaddress r_network r_contactinfo rw_nus w_messages";
    private static final String CALLBACK_URL = "x-oauthflow-linkedin://callback";
    private static final String SERVICE_URL_ID = "www.linkedin";

    private final LinkedIn linkedIn;

    @NotNull final String consumerKey;
    @NotNull final String consumerSecret;
    @NotNull final CommonsHttpOAuthProvider oAuthProvider;
    @NotNull final CommonsHttpOAuthConsumer oAuthConsumer;

    @Inject public LinkedInAuthenticationProvider(
            LinkedIn linkedIn,
            @ConsumerKey("LinkedIn") @NotNull String consumerKey,
            @ConsumerSecret("LinkedIn") @NotNull String consumerSecret)
    {
        this.linkedIn = linkedIn;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.oAuthProvider = createOAuthProvider();
        this.oAuthConsumer = createOAuthConsumer();
    }

    @NotNull private CommonsHttpOAuthProvider createOAuthProvider()
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

    @NotNull private CommonsHttpOAuthConsumer createOAuthConsumer()
    {
        return new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
    }

    @Override public void authenticate(final THAuthenticationCallback callback)
    {
        if (currentOperationCallback != null)
        {
            cancel();
        }
        currentOperationCallback = callback;

        Context context = baseContext == null ? null : baseContext.get();
        if (context == null)
        {
            throw new IllegalStateException("Context must be non-null for LinkedIn authentication to proceed.");
        }
        callback.onStart();

        linkedIn.authorize(context, new THAuthenticationCallback()
        {
            @Override public void onCancel()
            {
                LinkedInAuthenticationProvider.this.handleCancel(callback);
            }

            @Override public void onError(Throwable error)
            {
                if (LinkedInAuthenticationProvider.this.currentOperationCallback != callback)
                {
                    return;
                }
                try
                {
                    callback.onError(error);
                }
                finally
                {
                    LinkedInAuthenticationProvider.this.currentOperationCallback = null;
                }
            }

            @Override
            public void onStart()
            {
                callback.onStart();
            }

            @Override
            public void onSuccess(JSONCredentials result)
            {
                if (LinkedInAuthenticationProvider.this.currentOperationCallback != callback)
                {
                    return;
                }
                try
                {
                    JSONCredentials authData;
                    try
                    {
                        authData = LinkedInAuthenticationProvider.this.getAuthData(
                                LinkedInAuthenticationProvider.this.linkedIn.getAuthToken(),
                                LinkedInAuthenticationProvider.this.linkedIn.getAuthTokenSecret());
                    }
                    catch (JSONException e)
                    {
                        callback.onError(e);
                        return;
                    }
                    callback.onSuccess(authData);
                }
                finally
                {
                    LinkedInAuthenticationProvider.this.currentOperationCallback = null;
                }
            }
        });
    }

    @Override public void deauthenticate()
    {
        linkedIn.setAuthToken(null);
        linkedIn.setAuthTokenSecret(null);
    }

    @Override public boolean restoreAuthentication(JSONCredentials authData)
    {
        if (authData == null)
        {
            deauthenticate();
            return true;
        }
        try
        {
            linkedIn.setAuthToken(authData.getString(AUTH_TOKEN_KEY));
            linkedIn.setAuthTokenSecret(authData.getString(AUTH_TOKEN_SECRET_KEY));

            return true;
        }
        catch (Exception e)
        {
        }
        return false;
    }

    public JSONCredentials getAuthData(String authToken, String authTokenSecret)
            throws JSONException
    {
        JSONCredentials authData = new JSONCredentials();
        authData.put(AUTH_TOKEN_KEY, authToken);
        authData.put(AUTH_TOKEN_SECRET_KEY, authTokenSecret);
        authData.put(CONSUMER_KEY_KEY, linkedIn.getConsumerKey());
        authData.put(CONSUMER_SECRET_KEY, linkedIn.getConsumerSecret());
        return authData;
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
}
