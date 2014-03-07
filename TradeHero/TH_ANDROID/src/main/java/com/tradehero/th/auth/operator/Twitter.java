package com.tradehero.th.auth.operator;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.webkit.CookieSyncManager;
import com.tradehero.th.auth.OAuthDialog;
import com.tradehero.th.auth.THAuthenticationProvider;
import javax.inject.Inject;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.http.HttpParameters;
import timber.log.Timber;

public class Twitter extends SocialOperator
{
    static final String REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
    static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
    static final String ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
    private static final String USER_ID_PARAM = "user_id";
    private static final String SCREEN_NAME_PARAM = "screen_name";
    private static final OAuthProvider PROVIDER = new CommonsHttpOAuthProvider(REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTHORIZE_URL);
    private static final String CALLBACK_URL = "twitter-oauth://complete";

    private String userId;
    private String screenName;

    @Inject public Twitter(@ConsumerKey("Twitter") String consumerKey, @ConsumerSecret("Twitter") String consumerSecret)
    {
        super(consumerKey, consumerSecret);
    }

    public String getUserId()
    {
        return this.userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getScreenName()
    {
        return this.screenName;
    }

    public void setScreenName(String screenName)
    {
        this.screenName = screenName;
    }

    public void authorize(final Context context, final THAuthenticationProvider.THAuthenticationCallback callback)
    {
        if ((getConsumerKey() == null) || (getConsumerKey().length() == 0) || (getConsumerSecret() == null) || (getConsumerSecret().length() == 0))
        {
            throw new IllegalStateException(
                    "Twitter must be initialized with a consumer key and secret before authorization.");
        }
        final OAuthConsumer consumer = new CommonsHttpOAuthConsumer(getConsumerKey(), getConsumerSecret());
        AsyncTask<Void, Void, String> task = createOAuthDialogTask(context, callback, consumer);
        task.execute();
    }

    private AsyncTask<Void, Void, String> createOAuthDialogTask(
            final Context context,
            final THAuthenticationProvider.THAuthenticationCallback callback,
            final OAuthConsumer consumer)
    {
        return new AsyncTask<Void, Void, String>()
        {
            private Throwable error;

            @Override protected void onPreExecute()
            {
                super.onPreExecute();
                showProgress();
            }

            @Override protected String doInBackground(Void... params)
            {
                try
                {
                    return Twitter.PROVIDER.retrieveRequestToken(consumer, CALLBACK_URL);
                }
                catch (Exception e)
                {
                    this.error = e;
                }
                return null;
            }

            @Override protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                try
                {
                    if (this.error != null)
                    {
                        callback.onError(this.error);
                        return;
                    }
                    CookieSyncManager.createInstance(context);
                    OAuthDialog dialog = createOAuthDialog(context, callback, result, consumer);
                    if (context instanceof FragmentActivity && !((FragmentActivity) context).isFinishing())
                    {
                        dialog.show();
                    }
                    else if (!(context instanceof FragmentActivity))
                    {
                        Timber.e(new ClassCastException("context is not Fragment Activity " + (context == null ? "null" : context.getClass().getName())), "");
                    }
                    else
                    {
                        Timber.e(new IllegalStateException("context is finishing"), "");
                    }
                }
                finally
                {
                    hideProgress();
                }
            }
        };
    }

    private OAuthDialog createOAuthDialog(final Context context, final THAuthenticationProvider.THAuthenticationCallback callback, String result, final OAuthConsumer consumer)
    {
        return new OAuthDialog(context, result, CALLBACK_URL, "api.twitter", new OAuthDialog.FlowResultHandler()
        {
            @Override public void onError(int errorCode, String description,
                    String failingUrl)
            {
                callback.onError(new Exception(
                        String.format("Error {0}, description: {1}, url: {2}",
                                errorCode, description, failingUrl)));
            }

            @Override public void onComplete(String callbackUrl)
            {
                CookieSyncManager.getInstance().sync();
                Uri uri = Uri.parse(callbackUrl);
                final String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
                if (verifier == null)
                {
                    callback.onCancel();
                    return;
                }
                AsyncTask<Void, Void, HttpParameters> getTokenTask = createGetTokenTask(consumer, verifier, callback);
                getTokenTask.execute();
            }

            @Override public void onCancel()
            {
                callback.onCancel();
            }
        });
    }

    private AsyncTask<Void, Void, HttpParameters> createGetTokenTask(
            final OAuthConsumer consumer,
            final String verifier,
            final THAuthenticationProvider.THAuthenticationCallback callback)
    {
        return new AsyncTask<Void, Void, HttpParameters>()
        {
            private Throwable error;

            @Override protected HttpParameters doInBackground(
                    Void... params)
            {
                try
                {
                    Twitter.PROVIDER.retrieveAccessToken(consumer, verifier);
                }
                catch (Throwable e)
                {
                    this.error = e;
                }
                return Twitter.PROVIDER.getResponseParameters();
            }

            @Override protected void onPreExecute()
            {
                super.onPreExecute();
                showProgress();
            }

            @Override protected void onPostExecute(HttpParameters result)
            {
                super.onPostExecute(result);
                try
                {
                    if (this.error != null)
                    {
                        callback.onError(this.error);
                        return;
                    }
                    try
                    {
                        Twitter.this.setAuthToken(consumer.getToken());
                        Twitter.this.setAuthTokenSecret(consumer.getTokenSecret());
                        Twitter.this.setScreenName(result.getFirst(SCREEN_NAME_PARAM));
                        Twitter.this.setUserId(result.getFirst(USER_ID_PARAM));
                    }
                    catch (Throwable e)
                    {
                        callback.onError(e);
                        return;
                    }
                    // since all parameters is stored as field of
                    // twitter object, json is not needed
                    callback.onSuccess(null);
                }
                finally
                {
                    hideProgress();
                }
            }
        };
    }
}