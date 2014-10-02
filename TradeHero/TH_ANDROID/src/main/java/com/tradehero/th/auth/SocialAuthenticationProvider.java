package com.tradehero.th.auth;

import android.app.Activity;
import android.content.Context;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import rx.Observable;

public abstract class SocialAuthenticationProvider implements THAuthenticationProvider
{
    public static final String SCREEN_NAME_KEY = "screen_name";
    public static final String ID_KEY = "id";
    public static final String EMAIL_KEY = "email";
    public static final String AUTH_TOKEN_SECRET_KEY = "auth_token_secret";
    public static final String AUTH_TOKEN_KEY = "auth_token";
    public static final String CONSUMER_KEY_KEY = "consumer_key";
    public static final String CONSUMER_SECRET_KEY = "consumer_secret";

    // TODO make it private when the refactor is done
    protected WeakReference<Activity> baseActivity;
    private Map<Activity, Observable<AuthData>> cachedObservables = new WeakHashMap<>();

    protected WeakReference<Context> baseContext;
    protected THAuthenticationProvider.THAuthenticationCallback currentOperationCallback;

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

    @Override public String getAuthHeader()
    {
        return getAuthType() + " " + getAuthHeaderParameter();
    }

    @Override
    public final Observable<AuthData> logIn(Activity activity)
    {
        baseActivity = new WeakReference<>(activity);
        Observable<AuthData> cachedObservable = cachedObservables.get(activity);
        if (cachedObservable != null)
        {
            return cachedObservable;
        }

        return cachedObservables.put(activity, createAuthDataObservable(activity));
    }

    protected abstract Observable<AuthData> createAuthDataObservable(Activity activity);
}
