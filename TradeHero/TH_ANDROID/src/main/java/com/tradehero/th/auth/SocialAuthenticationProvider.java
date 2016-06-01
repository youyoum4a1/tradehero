package com.ayondo.academy.auth;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import com.ayondo.academy.api.auth.AccessTokenForm;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.network.service.SocialLinker;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import rx.Observable;
import rx.functions.Func1;

public abstract class SocialAuthenticationProvider implements AuthenticationProvider
{
    // TODO make it private when the refactor is done
    protected WeakReference<Activity> baseActivity;
    private Map<Activity, Observable<AuthData>> cachedObservables = new WeakHashMap<>();

    protected WeakReference<Context> baseContext;

    @NonNull protected final SocialLinker socialLinker;

    protected SocialAuthenticationProvider(@NonNull SocialLinker socialLinker)
    {
        this.socialLinker = socialLinker;
    }

    public SocialAuthenticationProvider with(Context context)
    {
        baseContext = new WeakReference<>(context);
        return this;
    }

    @Override
    public final Observable<AuthData> logIn(Activity activity)
    {
        // FIXME use caching
        baseActivity = new WeakReference<>(activity);
        Observable<AuthData> cachedObservable;// = cachedObservables.get(activity);
        //if (cachedObservable != null)
        //{
        //    return cachedObservable;
        //}

        cachedObservable = createAuthDataObservable(activity);
        //cachedObservables.put(activity, cachedObservable);
        return cachedObservable;
    }

    @NonNull public Observable<Boolean> canShare(@NonNull Activity activity)
    {
        return Observable.just(true);
    }

    protected abstract Observable<AuthData> createAuthDataObservable(Activity activity);

    protected void clearCachedObservables()
    {
        cachedObservables.clear();
    }

    @NonNull public Observable<UserProfileDTO> socialLink(@NonNull Activity activity)
    {
        return logIn(activity)
                .flatMap(new Func1<AuthData, Observable<? extends UserProfileDTO>>()
                {
                    @Override public Observable<? extends UserProfileDTO> call(AuthData authData)
                    {
                        return socialLinker.link(new AccessTokenForm(authData));
                    }
                });
    }
}
