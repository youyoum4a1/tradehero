package com.androidth.general.auth.operator;

import android.content.Context;
import android.net.Uri;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.androidth.general.R;
import com.androidth.general.auth.OAuthDialog;
import com.androidth.general.rx.view.DismissDialogAction0;
import java.util.concurrent.CancellationException;
import oauth.signpost.OAuth;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.AndroidSubscriptions;

public class OperatorOAuthDialog implements Observable.OnSubscribe<String>
{
    private final Context context;
    private final String tokenRequestUrl;
    private final String callbackUrl;
    private final String serviceUrlId;

    public OperatorOAuthDialog(Context context, String tokenRequestUrl, String callbackUrl, String serviceUrlId)
    {
        this.context = context;
        this.tokenRequestUrl = tokenRequestUrl;
        this.callbackUrl = callbackUrl;
        this.serviceUrlId = serviceUrlId;
    }

    @Override public void call(final Subscriber<? super String> subscriber)
    {
        final OAuthDialog dialog = new OAuthDialog(context, tokenRequestUrl, callbackUrl, serviceUrlId, new OAuthDialog.FlowResultHandler()
        {
            @Override public void onCancel()
            {
                CookieManager.getInstance().removeAllCookie();
                subscriber.onError(new CancellationException(context.getString(R.string.error_canceled)));
            }

            @Override public void onError(int errorCode, String description, String failingUrl)
            {
                // TODO better exception
                subscriber.onError(new CancellationException("Authorization fail!"));
            }

            @Override public void onComplete(String callbackUrl)
            {
                CookieSyncManager.getInstance().sync();
                Uri uri = Uri.parse(callbackUrl);
                final String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
                if (verifier == null)
                {
                    onCancel();
                    return;
                }
                subscriber.onNext(verifier);
                subscriber.onCompleted();
            }
        });
        dialog.show();

        Subscription subscription = AndroidSubscriptions.unsubscribeInUiThread(new DismissDialogAction0(dialog));

        subscriber.add(subscription);
    }
}