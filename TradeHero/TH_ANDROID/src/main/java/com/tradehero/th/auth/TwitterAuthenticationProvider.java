package com.tradehero.th.auth;

import android.app.Activity;
import android.content.Context;
import com.tradehero.th.auth.operator.Twitter;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.models.user.auth.TwitterCredentialsDTO;
import java.lang.ref.WeakReference;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class TwitterAuthenticationProvider extends SocialAuthenticationProvider
{
    private final Twitter twitter;

    @Inject public TwitterAuthenticationProvider(Twitter twitter)
    {
        this.twitter = twitter;
    }

    @Override public void authenticate(final THAuthenticationProvider.THAuthenticationCallback callback)
    {
        throw new RuntimeException("Not implemented");
    }

    @Override public void deauthenticate()
    {
        twitter.setAuthToken(null);
        twitter.setAuthTokenSecret(null);
    }

    @Override public String getAuthType()
    {
        return TwitterCredentialsDTO.TWITTER_AUTH_TYPE;
    }

    @Override public String getAuthHeaderParameter()
    {
        return twitter.getAuthToken() + ":" + twitter.getAuthTokenSecret();
    }

    @Override public boolean restoreAuthentication(JSONCredentials authData)
    {
        throw new RuntimeException("Not implemented");
    }

    @Override public Observable<AuthData> logIn(Activity activity)
    {
        baseContext = new WeakReference<Context>(activity);
        return twitter.authorize(activity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}