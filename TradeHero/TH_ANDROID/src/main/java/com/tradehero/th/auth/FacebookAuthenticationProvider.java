package com.tradehero.th.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.TokenCachingStrategy;
import com.facebook.android.Facebook;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.facebook.SubscriberCallback;
import com.tradehero.th.auth.operator.FacebookAppId;
import com.tradehero.th.auth.operator.FacebookPermissions;
import com.tradehero.th.network.service.SocialLinker;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.Assertions;
import rx.android.subscriptions.AndroidSubscriptions;
import rx.functions.Action0;
import rx.functions.Func1;

@Singleton
public class FacebookAuthenticationProvider extends SocialAuthenticationProvider
{
    public static final DateFormat PRECISE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    public static final String ACCESS_TOKEN_KEY =  "access_token";
    public static final String EXPIRATION_DATE_KEY = "expiration_date";

    private Facebook facebook;
    private Session session;
    private SessionDefaultAudience defaultAudience;
    private final String applicationId;
    private int activityCode;
    private Context applicationContext;
    @NotNull private final TokenCachingStrategy tokenCachingStrategy;
    private List<String> permissions;
    private THAuthenticationProvider.THAuthenticationCallback currentOperationCallback;
    private String userId;

    // TODO not use injection of Context as this instance is a singleton.
    // Use Provider<Activity> instead
    @Inject public FacebookAuthenticationProvider(
            @NotNull SocialLinker socialLinker,
            Context context,
            @NotNull TokenCachingStrategy tokenCachingStrategy,
            @FacebookAppId String applicationId,
            @FacebookPermissions List<String> permissions)
    {
        super(socialLinker);
        this.tokenCachingStrategy = tokenCachingStrategy;
        PRECISE_DATE_FORMAT.setTimeZone(new SimpleTimeZone(0, "GMT"));
        
        this.baseActivity = new WeakReference<>(null);
        this.activityCode = 32665;
        this.permissions = permissions;

        this.applicationId = applicationId;
        if (context != null)
        {
            this.applicationContext = context.getApplicationContext();
        }

        if (applicationId != null)
        {
            this.facebook = new Facebook(applicationId);
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Activity activity = this.baseActivity.get();
        Session activeSession = Session.getActiveSession();
        if (activity != null && activeSession != null)
        {
            activeSession.onActivityResult(activity, requestCode, resultCode, data);
        }
    }

    @Override public synchronized void cancel()
    {
        handleCancel();
    }

    public int getActivityCode()
    {
        return this.activityCode;
    }

    public Facebook getFacebook()
    {
        return this.facebook;
    }

    public Session getSession()
    {
        return this.session;
    }

    private void handleCancel()
    {
        if (this.currentOperationCallback == null)
        {
            return;
        }
        try
        {
            this.currentOperationCallback.onCancel();
        }
        finally
        {
            this.currentOperationCallback = null;
        }
    }

    private void handleError(Throwable error)
    {
        if (this.currentOperationCallback == null)
        {
            return;
        }
        try
        {
            this.currentOperationCallback.onError(error);
        }
        finally
        {
            this.currentOperationCallback = null;
        }
    }

    public synchronized void setActivity(Activity activity)
    {
        this.baseActivity = new WeakReference<>(activity);
    }

    public synchronized void setActivityCode(int activityCode)
    {
        this.activityCode = activityCode;
    }

    public synchronized void setPermissions(@Nullable List<String> permissions)
    {
        this.permissions = permissions;
        clearCachedObservables();
    }

    public void addPermission(@NotNull String permission)
    {
        this.permissions.add(permission);
        clearCachedObservables();
    }

    @Override public Observable<AuthData> createAuthDataObservable(Activity activity)
    {
        return createSessionObservable(activity)
                .flatMap(new Func1<Session, Observable<AuthData>>()
                {
                    @Override public Observable<AuthData> call(Session session)
                    {
                        return Observable.create(new MeRequestSubscribe(session));
                    }
                });
    }

    public Observable<Session> createSessionObservable(Activity activity)
    {
        return Observable.create(new FacebookAuthenticationSubscribe(activity, permissions));
    }

    @Override public void logout()
    {
        Session session = Session.getActiveSession();
        if (session != null && !session.isClosed())
        {
            session.closeAndClearTokenInformation();
        }

        tokenCachingStrategy.clear();
    }

    private class FacebookAuthenticationSubscribe implements Observable.OnSubscribe<Session>
    {
        private final Activity activity;
        private final List<String> permissions;

        public FacebookAuthenticationSubscribe(Activity activity, List<String> permissions)
        {
            this.activity = activity;
            this.permissions = permissions;
        }

        @Override public void call(final Subscriber<? super Session> subscriber)
        {
            Assertions.assertUiThread();
            final Session.StatusCallback statusCallback = new SubscriberCallback(subscriber);

            Session activeSession = Session.getActiveSession();

            if (activeSession != null && activeSession.isOpened())
            {
                // if requesting permissions is just subset of activeSession.getPermissions() - existing one
                if (isSubsetPermissions(activeSession.getPermissions(), permissions))
                {
                    subscriber.onNext(activeSession);
                    subscriber.onCompleted();
                    return;
                }
            }

            activeSession = new Session.Builder(activity)
                    .setApplicationId(applicationId)
                    .setTokenCachingStrategy(tokenCachingStrategy)
                    .build();
            Session.OpenRequest openRequest = new Session.OpenRequest(activity);
            openRequest.setRequestCode(activityCode);
            if (this.permissions != null)
            {
                openRequest.setPermissions(new ArrayList<>(this.permissions));
            }
            Session.setActiveSession(activeSession);
            openRequest.setCallback(statusCallback);
            activeSession.openForRead(openRequest);

            Subscription subscription = AndroidSubscriptions.unsubscribeInUiThread(new Action0()
            {
                @Override public void call()
                {
                    Session activeSession = Session.getActiveSession();
                    if (activeSession != null)
                    {
                        activeSession.removeCallback(statusCallback);
                    }
                }
            });

            subscriber.add(subscription);
        }
    }

    private static boolean isSubsetPermissions(List<String> permissions, List<String> newPermissions)
    {
        if (newPermissions == null)
        {
            return true;
        }
        if (permissions == null)
        {
            return false;
        }

        List<String> temp = new ArrayList<>(permissions);
        for (String permission: newPermissions)
        {
            if (!temp.remove(permission))
            {
                return false;
            }
        }
        return true;
    }

    private class MeRequestSubscribe implements Observable.OnSubscribe<AuthData>
    {
        private final Session session;

        public MeRequestSubscribe(Session session)
        {
            this.session = session;
        }

        @Override public void call(final Subscriber<? super AuthData> subscriber)
        {
            // TODO never create a new request twice
            Request meRequest = Request.newGraphPathRequest(session, "me", new Request.Callback()
            {
                @Override public void onCompleted(Response response)
                {
                    if (response.getError() != null)
                    {
                        subscriber.onError(response.getError().getException());
                    }
                    else
                    {
                        AuthData authData = new AuthData(SocialNetworkEnum.FB, session.getExpirationDate(), session.getAccessToken());
                        subscriber.onNext(authData);
                        subscriber.onCompleted();
                    }
                }
            });
            meRequest.getParameters().putString("fields", "id");
            meRequest.executeAsync();
        }
    }
}