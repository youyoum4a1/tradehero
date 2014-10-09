package com.tradehero.th.auth;

import android.app.Activity;
import com.tradehero.th.auth.operator.Twitter;
import com.tradehero.th.network.service.SocialLinker;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class TwitterAuthenticationProvider extends SocialAuthenticationProvider
{
    private final Twitter twitter;

    @Inject public TwitterAuthenticationProvider(@NotNull SocialLinker socialLinker, Twitter twitter)
    {
        super(socialLinker);
        this.twitter = twitter;
    }

    @Override protected Observable<AuthData> createAuthDataObservable(Activity activity)
    {
        return twitter.authorize(activity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}