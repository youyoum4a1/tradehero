package com.tradehero.th.auth.twitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.CookieSyncManager;
import com.tradehero.th.auth.THAuthenticationProvider;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.http.HttpParameters;
import org.apache.http.client.methods.HttpUriRequest;

public class Twitter
{
    static final String REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
    static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
    static final String ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
    private static final String VERIFIER_PARAM = "oauth_verifier";
    private static final String USER_ID_PARAM = "user_id";
    private static final String SCREEN_NAME_PARAM = "screen_name";
    private static final OAuthProvider PROVIDER =
            new CommonsHttpOAuthProvider("https://api.twitter.com/oauth/request_token",
                    "https://api.twitter.com/oauth/access_token",
                    "https://api.twitter.com/oauth/authorize");
    private static final String CALLBACK_URL = "twitter-oauth://complete";
    private String consumerKey;
    private String consumerSecret;
    private String authToken;
    private String authTokenSecret;
    private String userId;
    private String screenName;

    public Twitter(String consumerKey, String consumerSecret)
    {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public String getConsumerKey()
    {
        return this.consumerKey;
    }

    public void setConsumerKey(String consumerKey)
    {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret()
    {
        return this.consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret)
    {
        this.consumerSecret = consumerSecret;
    }

    public String getAuthToken()
    {
        return this.authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public String getAuthTokenSecret()
    {
        return this.authTokenSecret;
    }

    public void setAuthTokenSecret(String authTokenSecret)
    {
        this.authTokenSecret = authTokenSecret;
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

    public void signRequest(HttpUriRequest request)
    {
        OAuthConsumer consumer =
                new CommonsHttpOAuthConsumer(getConsumerKey(), getConsumerSecret());
        consumer.setTokenWithSecret(getAuthToken(), getAuthTokenSecret());
        try
        {
            consumer.sign(request);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void authorize(final Context context,
            final THAuthenticationProvider.THAuthenticationCallback callback)
    {
        if ((getConsumerKey() == null) || (getConsumerKey().length() == 0) || (getConsumerSecret()
                == null) ||
                (getConsumerSecret().length() == 0))
        {
            throw new IllegalStateException(
                    "Twitter must be initialized with a consumer key and secret before authorization.");
        }
        final OAuthConsumer consumer = new CommonsHttpOAuthConsumer(getConsumerKey(),
                getConsumerSecret());
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Loading...");
        AsyncTask task = new AsyncTask()
        {
            private Throwable error;

            protected void onPostExecute(String result)
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
                    OAuthDialog dialog =
                            new OAuthDialog(context, result, "twitter-oauth://complete",
                                    "api.twitter", new OAuthDialog.FlowResultHandler()
                            {
                                public void onError(int errorCode, String description,
                                        String failingUrl)
                                {
                                    callback.onError(new Exception(
                                            String.format("Error {0}, description: {1}, url: {2}",
                                                    errorCode, description, failingUrl)));
                                }

                                public void onComplete(String callbackUrl)
                                {
                                    CookieSyncManager.getInstance().sync();
                                    Uri uri = Uri.parse(callbackUrl);
                                    final String verifier = uri.getQueryParameter("oauth_verifier");
                                    if (verifier == null)
                                    {
                                        callback.onCancel();
                                        return;
                                    }
                                    AsyncTask getTokenTask = new AsyncTask()
                                    {
                                        private Throwable error;

                                        protected HttpParameters doInBackground(Object... params)
                                        {
                                            try
                                            {
                                                Twitter.PROVIDER
                                                        .retrieveAccessToken(consumer,
                                                                verifier);
                                            }
                                            catch (Throwable e)
                                            {
                                                this.error = e;
                                            }
                                            return Twitter.PROVIDER.getResponseParameters();
                                        }

                                        protected void onPreExecute()
                                        {
                                            super.onPreExecute();
                                            progress.show();
                                        }

                                        protected void onPostExecute(HttpParameters result)
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
                                                    Twitter.this.setAuthToken(
                                                            consumer.getToken());
                                                    Twitter.this.setAuthTokenSecret(
                                                            consumer.getTokenSecret());
                                                    Twitter.this.setScreenName(
                                                            result.getFirst("screen_name"));
                                                    Twitter.this.setUserId(
                                                            result.getFirst("user_id"));
                                                }
                                                catch (Throwable e)
                                                {
                                                    callback.onError(e);
                                                    return;
                                                }
                                                // TODO FIX IT
                                                callback.onSuccess(null);
                                            }
                                            finally
                                            {
                                                progress.dismiss();
                                            }
                                            progress.dismiss();
                                        }
                                    };
                                    getTokenTask.execute();
                                }

                                public void onCancel()
                                {
                                    callback.onCancel();
                                }
                            });
                    dialog.show();
                }
                finally
                {
                    progress.dismiss();
                }
                progress.dismiss();
            }

            protected void onPreExecute()
            {
                super.onPreExecute();
                progress.show();
            }

            protected String doInBackground(Object... params)
            {
                try
                {
                    return Twitter.PROVIDER
                            .retrieveRequestToken(consumer, "twitter-oauth://th");
                }
                catch (Throwable e)
                {
                    this.error = e;
                }
                return null;
            }
        };
        task.execute();
    }
}