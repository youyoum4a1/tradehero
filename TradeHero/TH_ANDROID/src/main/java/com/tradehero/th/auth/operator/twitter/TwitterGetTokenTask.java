package com.tradehero.th.auth.operator.twitter;

import android.os.AsyncTask;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.http.HttpParameters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class TwitterGetTokenTask extends AsyncTask<Void, Void, HttpParameters>
{
    @NotNull private final OAuthProvider provider;
    @NotNull private final OAuthConsumer consumer;
    @NotNull private final String verifier;
    @Nullable private Throwable error;

    //<editor-fold desc="Constructors">
    public TwitterGetTokenTask(
            @NotNull OAuthProvider provider,
            @NotNull OAuthConsumer consumer,
            @NotNull String verifier)
    {
        this.provider = provider;
        this.consumer = consumer;
        this.verifier = verifier;
    }
    //</editor-fold>

    @Nullable public Throwable getError()
    {
        return error;
    }

    @Override protected HttpParameters doInBackground(Void... params)
    {
        try
        {
            provider.retrieveAccessToken(consumer, verifier);
        }
        catch (Throwable e)
        {
            this.error = e;
        }
        return provider.getResponseParameters();
    }

    @Override protected void onPostExecute(HttpParameters result)
    {
        super.onPostExecute(result);
        if (error != null)
        {
            onPostExecuteError(error);
        }
        else
        {
            try
            {
                onPostExecuteResult(
                        consumer.getToken(),
                        consumer.getTokenSecret(),
                        result.getFirst(TwitterConstants.SCREEN_NAME_PARAM),
                        result.getFirst(TwitterConstants.USER_ID_PARAM));
            }
            catch (Throwable e)
            {
                onPostExecuteError(e);
            }
        }
    }

    abstract protected void onPostExecuteError(@NotNull Throwable error);
    abstract protected void onPostExecuteResult(
            String token,
            String tokenSecret,
            String screenName,
            String userId);
}
