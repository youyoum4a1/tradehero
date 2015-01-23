package com.tradehero.th.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.TokenCachingStrategy;
import com.facebook.android.Facebook;
import com.tradehero.common.social.facebook.FacebookRequestOperator;
import com.tradehero.common.social.facebook.SubscriberCallback;
import com.tradehero.th.activities.ActivityResultRequester;
import com.tradehero.th.api.social.SocialNetworkEnum;
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
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.Assertions;
import rx.android.subscriptions.AndroidSubscriptions;
import rx.functions.Action0;
import timber.log.Timber;

@Singleton
public class FacebookAuthenticationProvider extends SocialAuthenticationProvider
        implements ActivityResultRequester
{
    public static final DateFormat PRECISE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String EXPIRATION_DATE_KEY = "expiration_date";

    private Facebook facebook;
    private SessionDefaultAudience defaultAudience;
    private final String applicationId;
    private int activityCode;
    @NonNull private final TokenCachingStrategy tokenCachingStrategy;
    private List<String> permissions;

    // TODO not use injection of Context as this instance is a singleton.
    // Use Provider<Activity> instead
    @Inject public FacebookAuthenticationProvider(
            @NonNull SocialLinker socialLinker,
            Context context,
            @NonNull TokenCachingStrategy tokenCachingStrategy,
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

    public Facebook getFacebook()
    {
        return this.facebook;
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

    public void addPermission(@NonNull String permission)
    {
        this.permissions.add(permission);
        clearCachedObservables();
    }

    @Override public Observable<AuthData> createAuthDataObservable(Activity activity)
    {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id");
        return createSessionObservable(activity)
                .flatMap(session -> {
                    FacebookRequestOperator operator = FacebookRequestOperator.builder(session, "me")
                            .setParameters(parameters)
                            .build();
                    return Observable.create(operator)
                            .doOnNext(response -> Timber.d("Response %s", response.getRawResponse()))
                            .map(response -> new AuthData(
                                    SocialNetworkEnum.FB,
                                    session.getExpirationDate(),
                                    session.getAccessToken()));
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
            // TODO change to read and request for publish on demand
            activeSession.openForPublish(openRequest);

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
        for (String permission : newPermissions)
        {
            if (!temp.remove(permission))
            {
                return false;
            }
        }
        return true;
    }
}