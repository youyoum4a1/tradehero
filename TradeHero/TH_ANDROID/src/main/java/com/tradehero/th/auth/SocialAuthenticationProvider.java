package com.tradehero.th.auth;

import android.app.Activity;
import android.content.Context;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.SocialLinker;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.functions.Func1;

public abstract class SocialAuthenticationProvider implements THAuthenticationProvider
{
    // TODO make it private when the refactor is done
    protected WeakReference<Activity> baseActivity;
    private Map<Activity, Observable<AuthData>> cachedObservables = new WeakHashMap<>();

    protected WeakReference<Context> baseContext;
    protected THAuthenticationProvider.THAuthenticationCallback currentOperationCallback;

    @NotNull protected final SocialLinker socialLinker;

    protected SocialAuthenticationProvider(@NotNull SocialLinker socialLinker)
    {
        this.socialLinker = socialLinker;
    }

    public SocialAuthenticationProvider with(Context context)
    {
        baseContext = new WeakReference<>(context);
        return this;
    }

    @Override public void cancel()
    {
        handleCancel(this.currentOperationCallback);
    }

    protected void handleCancel(THAuthenticationProvider.THAuthenticationCallback callback)
    {
        if ((currentOperationCallback != callback) || (callback == null))
        {
            return;
        }
        try
        {
            callback.onCancel();
        }
        finally
        {
            currentOperationCallback = null;
        }
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

    protected abstract Observable<AuthData> createAuthDataObservable(Activity activity);

    protected void clearCachedObservables()
    {
        cachedObservables.clear();
    }

    @NotNull public Observable<UserProfileDTO> socialLink(
            @NotNull Activity activity)
    {
        return logIn(activity)
                .flatMap(new Func1<AuthData, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(AuthData authData)
                    {
                        return socialLinker.link(new AccessTokenForm(authData));
                    }
                });
    }
}
