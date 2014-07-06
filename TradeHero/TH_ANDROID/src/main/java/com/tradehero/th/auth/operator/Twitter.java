package com.tradehero.th.auth.operator;

import android.content.Context;
import android.os.AsyncTask;
import com.tradehero.th.auth.OAuthDialog;
import com.tradehero.th.auth.THAuthenticationProvider;
import com.tradehero.th.auth.operator.twitter.TwitterConstants;
import com.tradehero.th.auth.operator.twitter.TwitterFlowResultHandler;
import com.tradehero.th.auth.operator.twitter.TwitterGetTokenTask;
import com.tradehero.th.auth.operator.twitter.TwitterOAuthDialog;
import com.tradehero.th.auth.operator.twitter.TwitterOAuthDialogTask;
import javax.inject.Inject;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.http.HttpParameters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Twitter extends SocialOperator
{
    @NotNull private final OAuthProvider provider;
    @Nullable private String userId;
    @Nullable private String screenName;

    @Inject public Twitter(
            @ConsumerKey("Twitter") String consumerKey,
            @ConsumerSecret("Twitter") String consumerSecret)
    {
        super(consumerKey, consumerSecret);
        provider = new CommonsHttpOAuthProvider(
                TwitterConstants.REQUEST_TOKEN_URL,
                TwitterConstants.ACCESS_TOKEN_URL,
                TwitterConstants.AUTHORIZE_URL);
    }

    //<editor-fold desc="Accessors">
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
    //</editor-fold>

    public void authorize(
            @NotNull final Context context,
            @NotNull final THAuthenticationProvider.THAuthenticationCallback callback)
    {
        if (getConsumerKey().isEmpty() || getConsumerSecret().isEmpty())
        {
            throw new IllegalStateException(
                    "Twitter must be initialized with a consumer key and secret before authorization.");
        }
        final OAuthConsumer consumer = new CommonsHttpOAuthConsumer(getConsumerKey(), getConsumerSecret());
        AsyncTask<Void, Void, String> task = createOAuthDialogTask(context, callback, consumer);
        task.execute();
    }

    private AsyncTask<Void, Void, String> createOAuthDialogTask(
            @NotNull final Context context,
            @NotNull final THAuthenticationProvider.THAuthenticationCallback callback,
            @NotNull final OAuthConsumer consumer)
    {
        return new TwitterOAuthDialogTask(provider, context, consumer)
        {
            @Override protected void onPreExecute()
            {
                super.onPreExecute();
                showProgress();
            }

            @Override protected OAuthDialog createTaskOAuthDialog(
                    @NotNull Context context,
                    @NotNull String result,
                    @NotNull OAuthConsumer consumer)
            {
                return createOAuthDialog(context, callback, result, consumer);
            }

            @Override protected void onPostExecute(String result)
            {
                hideProgress();
                super.onPostExecute(result);
            }

            @Override protected void onPostExecuteError(@NotNull Throwable error)
            {
                callback.onError(error);
            }
        };
    }

    @NotNull private OAuthDialog createOAuthDialog(
            @NotNull final Context context,
            @NotNull final THAuthenticationProvider.THAuthenticationCallback callback,
            @NotNull String result,
            @NotNull final OAuthConsumer consumer)
    {
        return new TwitterOAuthDialog(
                context,
                result,
                createTwitterFlowResultHandler(
                        callback,
                        consumer));
    }

    @NotNull private TwitterFlowResultHandler createTwitterFlowResultHandler(
            @NotNull THAuthenticationProvider.THAuthenticationCallback callback,
            @NotNull OAuthConsumer consumer)
    {
        return new TwitterFlowResultHandler(callback, consumer)
        {
            @Override @NotNull protected AsyncTask<Void, Void, HttpParameters> createFlowGetTokenTask(
                    @NotNull OAuthConsumer consumer,
                    @NotNull String verifier,
                    @NotNull THAuthenticationProvider.THAuthenticationCallback callback)
            {
                return createGetTokenTask(consumer, verifier, callback);
            }
        };
    }

    @NotNull private TwitterGetTokenTask createGetTokenTask(
            @NotNull final OAuthConsumer consumer,
            @NotNull final String verifier,
            @NotNull final THAuthenticationProvider.THAuthenticationCallback callback)
    {
        return new TwitterGetTokenTask(provider, consumer, verifier)
        {
            @Override protected void onPreExecute()
            {
                super.onPreExecute();
                showProgress();
            }

            @Override protected void onPostExecute(HttpParameters result)
            {
                super.onPostExecute(result);
                hideProgress();
                // noinspection ThrowableResultOfMethodCallIgnored
                if (getError() == null)
                {
                    // since all parameters is stored as field of
                    // twitter object, json is not needed
                    callback.onSuccess(null);
                }
            }

            @Override protected void onPostExecuteError(@NotNull Throwable error)
            {
                callback.onError(error);
            }

            @Override protected void onPostExecuteResult(
                    String token,
                    String tokenSecret,
                    String screenName,
                    String userId)
            {
                Twitter.this.setAuthToken(token);
                Twitter.this.setAuthTokenSecret(tokenSecret);
                Twitter.this.setScreenName(screenName);
                Twitter.this.setUserId(userId);
            }
        };
    }
}