package com.tradehero.th.auth.operator.twitter;

import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.CookieSyncManager;
import com.tradehero.th.auth.OAuthDialog;
import com.tradehero.th.auth.THAuthenticationProvider;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.http.HttpParameters;
import org.jetbrains.annotations.NotNull;

abstract public class TwitterFlowResultHandler implements OAuthDialog.FlowResultHandler
{
    @NotNull private final THAuthenticationProvider.THAuthenticationCallback callback;
    @NotNull private final OAuthConsumer consumer;

    //<editor-fold desc="Constructors">
    public TwitterFlowResultHandler(
            @NotNull THAuthenticationProvider.THAuthenticationCallback callback,
            @NotNull OAuthConsumer consumer)
    {
        this.callback = callback;
        this.consumer = consumer;
    }
    //</editor-fold>

    @Override public void onError(
            int errorCode,
            String description,
            String failingUrl)
    {
        callback.onError(new Exception(
                String.format("Error {0}, description: {1}, url: {2}",
                        errorCode, description, failingUrl)));
    }

    @Override public void onComplete(@NotNull String callbackUrl)
    {
        CookieSyncManager.getInstance().sync();
        Uri uri = Uri.parse(callbackUrl);
        final String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
        if (verifier == null)
        {
            callback.onCancel();
            return;
        }
        AsyncTask<Void, Void, HttpParameters> getTokenTask = createFlowGetTokenTask(consumer, verifier, callback);
        getTokenTask.execute();
    }

    @Override public void onCancel()
    {
        callback.onCancel();
    }

    @NotNull abstract protected AsyncTask<Void, Void, HttpParameters> createFlowGetTokenTask(
            @NotNull OAuthConsumer consumer,
            @NotNull String verifier,
            @NotNull THAuthenticationProvider.THAuthenticationCallback callback);
}
