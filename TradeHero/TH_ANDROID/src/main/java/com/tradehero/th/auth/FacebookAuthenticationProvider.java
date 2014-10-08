package com.tradehero.th.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.SharedPreferencesTokenCachingStrategy;
import com.facebook.TokenCachingStrategy;
import com.facebook.android.Facebook;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.operator.FacebookAppId;
import com.tradehero.th.auth.operator.FacebookPermissions;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.models.user.auth.FacebookCredentialsDTO;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.Assertions;
import rx.android.subscriptions.AndroidSubscriptions;
import rx.functions.Action0;
import rx.functions.Func1;
import timber.log.Timber;

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
    private List<String> permissions;
    private THAuthenticationProvider.THAuthenticationCallback currentOperationCallback;
    private String userId;

    // TODO not use injection of Context as this instance is a singleton.
    // Use Provider<Activity> instead
    @Inject public FacebookAuthenticationProvider(
            Context context,
            @FacebookAppId String applicationId,
            @FacebookPermissions List<String> permissions)
    {
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

    @Override public synchronized void authenticate(THAuthenticationProvider.THAuthenticationCallback callback)
    {
        if (this.currentOperationCallback != null)
        {
            cancel();
        }
        this.currentOperationCallback = callback;
        final Activity activity = this.baseActivity == null ? null : this.baseActivity.get();
        if (activity == null)
        {
            throw new IllegalStateException(
                    "Activity must be non-null for Facebook authentication to proceed.");
        }
        int activityCode = this.activityCode;
        this.session = new Session.Builder(activity)
                .setApplicationId(this.applicationId)
                .setTokenCachingStrategy(new SharedPreferencesTokenCachingStrategy(activity))
                .build();

        callback.onStart();
        Session.OpenRequest openRequest = new Session.OpenRequest(activity);
        openRequest.setRequestCode(activityCode);
        if (this.defaultAudience != null)
        {
            openRequest.setDefaultAudience(this.defaultAudience);
        }
        if (this.permissions != null)
        {
            openRequest.setPermissions(new ArrayList<>(this.permissions));
        }
        openRequest.setCallback(new Session.StatusCallback()
        {
            @Override public void call(Session session, SessionState state, Exception exception)
            {
                if (state == SessionState.OPENING)
                {
                    return;
                }
                if (state.isOpened())
                {
                    if (FacebookAuthenticationProvider.this.currentOperationCallback == null)
                    {
                        return;
                    }
                    createAndExecuteMeRequest(session);
                }
                else if (exception != null)
                {
                    FacebookAuthenticationProvider.this.handleError(exception);
                }
                else
                {
                    FacebookAuthenticationProvider.this.handleCancel();
                }
            }
        });
        this.session.openForRead(openRequest);
    }

    private void createAndExecuteMeRequest(Session session)
    {
        Request meRequest = Request.newGraphPathRequest(session, "me", new Request.Callback()
        {
            @Override public void onCompleted(Response response)
            {
                if (response.getError() != null)
                {
                    if (response.getError().getException() != null)
                    {
                        FacebookAuthenticationProvider.this.handleError(response.getError().getException());
                    }
                    else
                    {
                        FacebookAuthenticationProvider.this.handleError(
                                new Exception("An error occurred while fetching the Facebook user's identity."));
                    }
                }
                else
                {
                    FacebookAuthenticationProvider.this.handleSuccess(
                            (String) response.getGraphObject()
                                    .getProperty("id"));
                }
            }
        });
        meRequest.getParameters().putString("fields", "id");
        meRequest.executeAsync();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
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

    @Override public String getAuthType()
    {
        return FacebookCredentialsDTO.FACEBOOK_AUTH_TYPE;
    }

    @Override public String getAuthHeader()
    {
        return getAuthType() + " " + getAuthHeaderParameter ();
    }

    @Override public String getAuthHeaderParameter()
    {
        return this.session.getAccessToken();
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

    public JSONCredentials getAuthData(String id, String accessToken, Date expiration) throws JSONException
    {
        JSONCredentials authData = new JSONCredentials();
        authData.put(SocialAuthenticationProvider.ID_KEY, id);
        authData.put(ACCESS_TOKEN_KEY, accessToken);
        authData.put(EXPIRATION_DATE_KEY, PRECISE_DATE_FORMAT.format(expiration));
        return authData;
    }

    private void handleSuccess(String userId)
    {
        if (this.currentOperationCallback == null)
        {
            return;
        }

        this.userId = userId;
        JSONCredentials authData = null;
        try
        {
            authData = getAuthData(userId, this.session.getAccessToken(), this.session.getExpirationDate());
        }
        catch (JSONException e)
        {
            handleError(e);
            return;
        }
        try
        {
            this.currentOperationCallback.onSuccess(authData);
            restoreAuthentication(authData);
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

    @Override public boolean restoreAuthentication(JSONCredentials authData)
    {
        if (authData == null)
        {
            if (this.facebook != null)
            {
                this.facebook.setAccessExpires(0L);
                this.facebook.setAccessToken(null);
            }
            this.session = null;
            return true;
        }
        try
        {
            String accessToken = authData.getString(ACCESS_TOKEN_KEY);
            Date expirationDate = PRECISE_DATE_FORMAT.parse(authData.getString(EXPIRATION_DATE_KEY));

            if (this.facebook != null)
            {
                this.facebook.setAccessToken(accessToken);
                this.facebook.setAccessExpires(expirationDate.getTime());
            }
            TokenCachingStrategy tcs = new SharedPreferencesTokenCachingStrategy(this.applicationContext);
            Bundle data = tcs.load();
            TokenCachingStrategy.putToken(data, authData.getString(ACCESS_TOKEN_KEY));
            TokenCachingStrategy.putExpirationDate(data, expirationDate);
            tcs.save(data);

            Session newSession = new Session.Builder(this.applicationContext)
                    .setApplicationId(this.applicationId).setTokenCachingStrategy(tcs).build();
            if (newSession.getState() == SessionState.CREATED_TOKEN_LOADED)
            {
                newSession.openForRead(null);
                this.session = newSession;
                Session.setActiveSession(this.session);
            }
            else
            {
                this.session = null;
            }
            return true;
        }
        catch (Exception e)
        {
            Timber.e("Unable to restore authentication", e);
        }
        return false;
    }

    @Override public void deauthenticate()
    {
        JSONCredentials authData = null;
        try
        {
            authData = getAuthData("", "", new Date());
        }
        catch (JSONException e)
        {
            Timber.e("Unable to deauthenticate", e);
        }
        restoreAuthentication(authData);
    }

    @Override public Observable<AuthData> createAuthDataObservable(Activity activity)
    {
        return Observable.create(new FacebookAuthenticationSubscribe(activity, permissions))
                .flatMap(new Func1<Session, Observable<AuthData>>()
                {
                    @Override public Observable<AuthData> call(Session session)
                    {
                        return Observable.create(new MeRequestSubscribe(session));
                    }
                });
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
            final Session.StatusCallback statusCallback = new Session.StatusCallback()
            {
                @Override public void call(Session session, SessionState state, Exception exception)
                {
                    if (state == SessionState.OPENING)
                    {
                        return;
                    }
                    if (state.isOpened())
                    {
                        subscriber.onNext(session);
                    }
                    else if (exception != null)
                    {
                        subscriber.onError(exception);
                    }
                    else
                    {
                        subscriber.onError(new FacebookOperationCanceledException("Action has been canceled"));
                    }
                }
            };

            Session activeSession = Session.getActiveSession();

            if (activeSession != null && activeSession.isOpened())
            {
                // if requesting permissions is just subset of activeSession.getPermissions() - existing one
                if (isSubsetPermissions(activeSession.getPermissions(), permissions))
                {
                    subscriber.onNext(activeSession);
                    return;
                }
            }

            activeSession = new Session.Builder(activity)
                    .setApplicationId(applicationId)
                    .setTokenCachingStrategy(new SharedPreferencesTokenCachingStrategy(activity))
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
                    }
                }
            });
            meRequest.getParameters().putString("fields", "id");
            meRequest.executeAsync();
        }
    }
}