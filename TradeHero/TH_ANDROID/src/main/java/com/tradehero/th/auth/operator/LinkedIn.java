package com.tradehero.th.auth.operator;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import com.tradehero.th.auth.OAuthDialog;
import com.tradehero.th.auth.THAuthenticationProvider;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.inject.Inject;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.http.HttpParameters;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class LinkedIn extends SocialOperator
{
    private static final String REQUEST_TOKEN_URL = "https://www.linkedin.com/uas/oauth/requestToken";
    private static final String AUTHORIZE_URL = "https://www.linkedin.com/uas/oauth/authorize";
    private static final String ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth/accessToken";
    private static final String PERMISSION_SCOPE = "r_basicprofile r_emailaddress r_network r_contactinfo rw_nus w_messages";
    private static final String CALLBACK_URL = "x-oauthflow-linkedin://callback";
    private static final OAuthProvider PROVIDER =
            new CommonsHttpOAuthProvider(REQUEST_TOKEN_URL + "?scope=" + getScope(), ACCESS_TOKEN_URL, AUTHORIZE_URL);

    private LinkedInAuthTask authTask;
    private LinkedInTokenTask tokenTask;

    @Inject public LinkedIn(@ConsumerKey("LinkedIn") String consumerKey, @ConsumerSecret("LinkedIn") String consumerSecret)
    {
        super(consumerKey, consumerSecret);
    }

    public static String getScope()
    {
        try
        {
            return URLEncoder.encode(PERMISSION_SCOPE, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return null;
        }
    }

    public void onDetach()
    {
        detachAuthTask();
        detachTokenTask();
    }

    private void detachAuthTask()
    {
        if (authTask != null)
        {
            authTask.setCallback(null);
        }
        authTask = null;
    }

    private void detachTokenTask()
    {
        if (tokenTask != null)
        {
            tokenTask.setCallback(null);
        }
        tokenTask = null;
    }

    public void authorize(final Context context, final THAuthenticationProvider.THAuthenticationCallback callback)
    {
        if ((getConsumerKey() == null) || (getConsumerKey().length() == 0) || (getConsumerSecret() == null) || (getConsumerSecret().length() == 0))
        {
            throw new IllegalStateException("LinkedIn must be initialized with a consumer key and secret before authorization.");
        }

        final OAuthConsumer consumer = new CommonsHttpOAuthConsumer(getConsumerKey(), getConsumerSecret());
        detachAuthTask();
        authTask = createAuthTask(context, callback, consumer);
        authTask.execute();
    }

    private LinkedInAuthTask createAuthTask(
            final Context context,
            final THAuthenticationProvider.THAuthenticationCallback callback,
            final OAuthConsumer consumer)
    {
        return new LinkedInAuthTask(context, callback, consumer);
    }

    private OAuthDialog createOAuthDialog(
            final Context context,
            @Nullable final THAuthenticationProvider.THAuthenticationCallback callback,
            final OAuthConsumer consumer,
            String result)
    {
        return new OAuthDialog(context, result, CALLBACK_URL,
                "www.linkedin", new OAuthDialog.FlowResultHandler()
        {
            @Override public void onError(int errorCode, String description,
                    String failingUrl)
            {
                if (callback != null)
                {
                    callback.onError(new Exception(
                            String.format("Error %s, description: %s, url: %s",
                                    errorCode, description, failingUrl)));
                }
            }

            @Override public void onComplete(String callbackUrl)
            {
                CookieSyncManager.getInstance().sync();
                Uri uri = Uri.parse(callbackUrl);
                final String verifier = uri.getQueryParameter("oauth_verifier");
                if (verifier == null)
                {
                    if (callback != null)
                    {
                        callback.onCancel();
                    }
                    return;
                }

                detachTokenTask();
                tokenTask = createGetTokenTask(callback, consumer, verifier);
                tokenTask.execute();
            }

            @Override public void onCancel()
            {
                if (callback != null)
                {
                    callback.onCancel();
                }
            }
        });
    }

    private LinkedInTokenTask createGetTokenTask(
            final THAuthenticationProvider.THAuthenticationCallback callback,
            final OAuthConsumer consumer,
            final String verifier)
    {
        return new LinkedInTokenTask(callback, consumer, verifier);
    }

    protected class LinkedInAuthTask extends AsyncTask<Void, Void, String>
    {
        private Throwable error;
        private final Context context;
        private THAuthenticationProvider.THAuthenticationCallback callback;
        private final OAuthConsumer consumer;

        public LinkedInAuthTask(
                Context context,
                THAuthenticationProvider.THAuthenticationCallback callback,
                OAuthConsumer consumer)
        {
            super();
            this.context = context;
            this.callback = callback;
            this.consumer = consumer;
        }

        public void setCallback(THAuthenticationProvider.THAuthenticationCallback callback)
        {
            this.callback = callback;
        }

        @Override protected void onPreExecute()
        {
            super.onPreExecute();
            showProgress();
        }

        @Override protected String doInBackground(Void... params)
        {
            try
            {
                return LinkedIn.PROVIDER
                        .retrieveRequestToken(consumer, CALLBACK_URL);
            }
            catch (Throwable e)
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
                    if (callback != null)
                    {
                        callback.onError(this.error);
                    }
                    return;
                }
                CookieSyncManager.createInstance(context);
                OAuthDialog dialog = createOAuthDialog(context, callback, consumer, result);

                dialog.show();
            }
            catch(WindowManager.BadTokenException e) // TODO make the SocialOperator deactivatable
            {
                Timber.e(e, "Failed to show dialog");
            }
            finally
            {
                hideProgress();
            }
        }
    }

    protected class LinkedInTokenTask extends AsyncTask<Void, Void, HttpParameters>
    {
        private Throwable error;
        private THAuthenticationProvider.THAuthenticationCallback callback;
        private final OAuthConsumer consumer;
        private  final String verifier;

        public LinkedInTokenTask(THAuthenticationProvider.THAuthenticationCallback callback,
                OAuthConsumer consumer, String verifier)
        {
            this.callback = callback;
            this.consumer = consumer;
            this.verifier = verifier;
        }

        public void setCallback(THAuthenticationProvider.THAuthenticationCallback callback)
        {
            this.callback = callback;
        }

        @Override protected HttpParameters doInBackground(Void... params)
        {
            try
            {
                LinkedIn.PROVIDER
                        .retrieveAccessToken(consumer,
                                verifier);
            }
            catch (Throwable e)
            {
                this.error = e;
            }
            return LinkedIn.PROVIDER.getResponseParameters();
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
                    LinkedIn.this.setAuthToken(consumer.getToken());
                    LinkedIn.this.setAuthTokenSecret(consumer.getTokenSecret());
                }
                catch (Throwable e)
                {
                    callback.onError(e);
                    return;
                }
                callback.onSuccess(null);
            }
            finally
            {
                hideProgress();
            }
        }
    }
}
