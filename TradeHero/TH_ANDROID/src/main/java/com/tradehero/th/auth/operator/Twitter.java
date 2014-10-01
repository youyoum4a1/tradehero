package com.tradehero.th.auth.operator;

import android.content.Context;
import android.net.Uri;
import android.webkit.CookieSyncManager;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.OAuthDialog;
import java.util.concurrent.CancellationException;
import javax.inject.Inject;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class Twitter extends SocialOperator
{
    private static final String REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
    private static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
    private static final String ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
    private static final String CALLBACK_URL = "twitter-oauth://complete";

    private static final String SERVICE_URL_ID = "api.twitter";
    private static final String USER_ID_PARAM = "user_id";
    private static final String SCREEN_NAME_PARAM = "screen_name";

    private final OAuthProvider provider;
    private final CommonsHttpOAuthConsumer consumer;

    @Inject public Twitter(
            @ConsumerKey("Twitter") String consumerKey,
            @ConsumerSecret("Twitter") String consumerSecret)
    {
        super(consumerKey, consumerSecret);
        provider = new CommonsHttpOAuthProvider(
                REQUEST_TOKEN_URL,
                ACCESS_TOKEN_URL,
                AUTHORIZE_URL);

        consumer = new CommonsHttpOAuthConsumer(getConsumerKey(), getConsumerSecret());
    }

    public Observable<AuthData> authorize(final Context context)
    {
        return createRequestTokenObservable(context)
                .flatMap(new Func1<String, Observable<AuthData>>()
                {
                    @Override public Observable<AuthData> call(String s)
                    {
                        return createRetrieveTokenObservable(s);
                    }
                });
    }

    private Observable<String> createRequestTokenObservable(final Context context)
    {
        return Observable.create(new Observable.OnSubscribe<String>()
        {

            @Override public void call(Subscriber<? super String> subscriber)
            {
                try
                {
                    String requestToken = provider.retrieveRequestToken(consumer, CALLBACK_URL);
                    CookieSyncManager.createInstance(context);
                    subscriber.onNext(requestToken);
                    subscriber.onCompleted();
                } catch (Throwable e)
                {
                    subscriber.onError(e);
                }
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<String, Observable<String>>()
                {
                    @Override public Observable<String> call(final String tokenRequestUrl)
                    {
                        return Observable.create(new Observable.OnSubscribe<String>()
                        {
                            @Override public void call(final Subscriber<? super String> subscriber)
                            {
                                OAuthDialog dialog = new OAuthDialog(context, tokenRequestUrl, CALLBACK_URL, SERVICE_URL_ID, new OAuthDialog.FlowResultHandler()
                                {
                                    @Override public void onCancel()
                                    {
                                        subscriber.onError(new CancellationException());
                                    }

                                    @Override public void onError(int errorCode, String description, String failingUrl)
                                    {
                                        // TODO better exception
                                        subscriber.onError(new Exception("Authorization fail!"));
                                    }

                                    @Override public void onComplete(String callbackUrl)
                                    {
                                        CookieSyncManager.getInstance().sync();
                                        Uri uri = Uri.parse(callbackUrl);
                                        final String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
                                        if (verifier == null)
                                        {
                                            subscriber.onError(new Exception("Verifier is empty"));
                                            return;
                                        }
                                        subscriber.onNext(verifier);
                                        subscriber.onCompleted();
                                    }
                                });
                                dialog.show();
                            }
                        });
                    }
                }).observeOn(Schedulers.io());
    }

    private Observable<AuthData> createRetrieveTokenObservable(final String verifier)
    {
        return Observable.create(new Observable.OnSubscribe<AuthData>()
        {
            @Override public void call(Subscriber<? super AuthData> subscriber)
            {
                try
                {
                    Timber.d("Verifier: " + verifier);
                    provider.retrieveAccessToken(consumer, verifier);
                    // TODO lot of information can be extracted from response parameters
                    provider.getResponseParameters();
                    subscriber.onNext(new AuthData(SocialNetworkEnum.TW, null, consumer.getToken(), consumer.getTokenSecret()));
                }
                catch (Throwable e)
                {
                    subscriber.onError(e);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }
}