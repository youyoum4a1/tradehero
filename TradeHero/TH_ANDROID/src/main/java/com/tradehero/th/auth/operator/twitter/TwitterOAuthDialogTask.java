package com.tradehero.th.auth.operator.twitter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.webkit.CookieSyncManager;
import com.tradehero.th.auth.OAuthDialog;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

abstract public class TwitterOAuthDialogTask extends AsyncTask<Void, Void, String>
{
    @NotNull private final OAuthProvider provider;
    @NotNull private final Context context;
    @NotNull private final OAuthConsumer consumer;
    @Nullable private Throwable error;

    //<editor-fold desc="Constructors">
    public TwitterOAuthDialogTask(
            @NotNull OAuthProvider provider,
            @NotNull Context context,
            @NotNull OAuthConsumer consumer)
    {
        this.provider = provider;
        this.context = context;
        this.consumer = consumer;
    }
    //</editor-fold>

    @Override protected String doInBackground(Void... params)
    {
        try
        {
            return provider.retrieveRequestToken(consumer, TwitterConstants.CALLBACK_URL);
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
        if (error != null)
        {
            onPostExecuteError(error);
        }
        else
        {
            CookieSyncManager.createInstance(context);
            OAuthDialog dialog = createTaskOAuthDialog(context, result, consumer);
            if (context instanceof FragmentActivity && !((FragmentActivity) context).isFinishing())
            {
                dialog.show();
            }
            else if (!(context instanceof FragmentActivity))
            {
                Timber.e(new ClassCastException(
                        "context is not Fragment Activity " + context.getClass().getName()), "");
            }
            else
            {
                Timber.e(new IllegalStateException("context is finishing"), "");
            }
        }
    }

    abstract protected OAuthDialog createTaskOAuthDialog(
            @NotNull Context context,
            @NotNull String result,
            @NotNull OAuthConsumer consumer);
    abstract protected void onPostExecuteError(@NotNull Throwable error);
}
