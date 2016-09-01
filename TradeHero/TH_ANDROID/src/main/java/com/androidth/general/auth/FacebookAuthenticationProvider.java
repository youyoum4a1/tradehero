package com.androidth.general.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidth.general.common.facebook.FacebookRequestException;
import com.androidth.general.network.share.SocialConstants;
import com.androidth.general.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookPermissionsConstants;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.SessionDefaultAudience;
//import com.facebook.TokenCachingStrategy;
import com.androidth.general.common.activities.ActivityResultRequester;
import com.androidth.general.common.facebook.FacebookRequestOperator;
import com.androidth.general.api.auth.AccessTokenForm;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.auth.operator.FacebookPermissions;
import com.androidth.general.network.service.SocialLinker;
import com.androidth.general.rx.ReplaceWithFunc1;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

@Singleton
public class FacebookAuthenticationProvider extends SocialAuthenticationProvider
        implements ActivityResultRequester
{
    public static final DateFormat PRECISE_DATE_FORMAT = new SimpleDateFormat(Constants.DATE_FORMAT_PRECISE_Z, Locale.getDefault());
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String EXPIRATION_DATE_KEY = "expiration_date";

//    private Facebook facebook;
//    private SessionDefaultAudience defaultAudience;
    private final String applicationId;
    private int activityCode;
//    @NonNull private final TokenCachingStrategy tokenCachingStrategy;
    private List<String> permissions;
    private CallbackManager callbackManager;
    @NonNull private AccessToken accessToken;
    private String PUBLISH_PERMISSION = "publish_actions";

    // TODO not use injection of Context as this instance is a singleton.
    // Use Provider<Activity> instead
    @Inject public FacebookAuthenticationProvider(
            @NonNull SocialLinker socialLinker,
            Context context,
            @FacebookPermissions List<String> permissions)
    {
        super(socialLinker);
//        this.tokenCachingStrategy = tokenCachingStrategy;
        PRECISE_DATE_FORMAT.setTimeZone(new SimpleTimeZone(0, "GMT"));

        this.baseActivity = new WeakReference<>(null);
        this.activityCode = 32665;
        this.permissions = new ArrayList<>(permissions);

        this.applicationId = SocialConstants.FACEBOOK_APP_ID;

//        if (applicationId != null)
//        {
//            this.facebook = new Facebook(applicationId);
//        }
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
//        Session activeSession = Session.getActiveSession();
//        if (activeSession != null)
//        {
//            activeSession.onActivityResult(activity, requestCode, resultCode, data);
//        }
        if(callbackManager!=null){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        switch (resultCode){
            case 0://cancelled
                break;
            case -1:
            case 1:
                break;
            default:
                break;

        }
    }

//    public Facebook getFacebook()
//    {
//        return this.facebook;
//    }

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
        return createAuthDataObservable(activity, permissions);
    }

    @NonNull @RxLogObservable public Observable<AuthData> createAuthDataObservable(Activity activity, @Nullable List<String> permissions)
    {
        final Bundle parameters = new Bundle();
        parameters.putString("fields", "id");
        
        return createAccessTokenObservable(activity, permissions)
                .flatMap(new Func1<AccessToken, Observable<? extends AuthData>>()
                {
                    @Override public Observable<? extends AuthData> call(final AccessToken accessToken)
                    {

                        return Observable.create(new FacebookRequestOperator(accessToken))
                                .map(new Func1<AccessToken, AuthData>()
                                {
                                    @Override public AuthData call(AccessToken accessToken1)
                                    {
                                        return new AuthData(
                                                SocialNetworkEnum.FB,
                                                accessToken1.getExpires(),
                                                accessToken1.getToken());
                                    }
                                });
                    }
                });
//        return Observable.create(operator)
//                .map(new Func1<AccessToken, AuthData>()
//                {
//                    @Override public AuthData call(AccessToken accessToken)
//                    {
//                        Timber.d("Response %s", accessToken.getToken());
//                        return new AuthData(
//                                SocialNetworkEnum.FB,
//                                accessToken.getExpires(),
//                                accessToken.getToken());
//                    }
//                });

//        return createSessionObservable(activity, permissions)
//                .flatMap(new Func1<AccessToken, Observable<? extends AuthData>>()
//                {
//                    @Override public Observable<? extends AuthData> call(final AccessToken session)
//                    {
//                        callbackManager = CallbackManager.Factory.create();
//                        FacebookRequestOperator operator = FacebookRequestOperator.builder(session, "me", callbackManager)
//                                .setParameters(parameters)
//                                .build();
//                        return Observable.create(operator)
//                                .map(new Func1<LoginResult, AuthData>()
//                                {
//                                    @Override public AuthData call(LoginResult loginResult)
//                                    {
//                                        Timber.d("Response %s", loginResult.getAccessToken());
//                                        return new AuthData(
//                                                SocialNetworkEnum.FB,
//                                                loginResult.getAccessToken().getExpires(),
//                                                loginResult.getAccessToken().getToken());
//                                    }
//                                });
//                    }
//                });
    }

    @NonNull public Observable<AccessToken> createAccessTokenObservable(@NonNull Activity activity)
    {
        return createAccessTokenObservable(activity, permissions);
    }

    @NonNull @RxLogObservable public Observable<AccessToken> createAccessTokenObservable(@NonNull Activity activity, @Nullable List<String> permissions)
    {
        return Observable.create(new FacebookAuthenticationSubscribe(activity, permissions));
    }

    @NonNull @Override public Observable<UserProfileDTO> socialLink(@NonNull Activity activity)
    {
        List<String> permissions = new ArrayList<>(this.permissions);
        permissions.add(FacebookPermissionsConstants.PUBLISH_ACTIONS);
        return logIn(activity)
                .flatMap(new ReplaceWithFunc1<AuthData, Observable<AuthData>>(createAuthDataObservable(activity, permissions)))
                .flatMap(new Func1<AuthData, Observable<? extends UserProfileDTO>>()
                {
                    @Override public Observable<? extends UserProfileDTO> call(AuthData authData)
                    {
                        return socialLinker.link(new AccessTokenForm(authData));
                    }
                });

    }

    @NonNull @Override public Observable<Boolean> canShare(@NonNull Activity activity)
    {
//        return createSessionObservable(activity, permissions)
//                .map(new Func1<AccessToken, Boolean>()
//                {
//                    @Override public Boolean call(AccessToken accessToken)
//                    {
//                        return accessToken.getPermissions().contains(FacebookPermissionsConstants.PUBLISH_ACTIONS);
//                    }
//                });
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken!=null){
            return Observable.just(AccessToken.getCurrentAccessToken()
                    .getPermissions().contains(FacebookPermissionsConstants.PUBLISH_ACTIONS));
        }else{
            return Observable.just(false);
        }

    }

    @Override public void logout()
    {
//        Session session = Session.getActiveSession();
//        if (session != null && !session.isClosed())
//        {
//            session.closeAndClearTokenInformation();
//        }
//
//        tokenCachingStrategy.clear();
//        LoginManager.getInstance().logOut();//this will renew the FB access token
    }

    private class FacebookAuthenticationSubscribe implements Observable.OnSubscribe<AccessToken> {
        private final Activity activity;
        private final List<String> permissions;

        public FacebookAuthenticationSubscribe(Activity activity, List<String> permissions)
        {
            this.activity = activity;
            this.permissions = permissions;
        }

        @Override public void call(final Subscriber<? super AccessToken> subscriber){

            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try{
                                        String  email= object.getString("email");
                                        Timber.d("user email ", email);
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            });
                            request.executeAsync();

                            if(loginResult.getAccessToken().getPermissions().contains(PUBLISH_PERMISSION)){
                                Timber.d("Current access with publish");
                                subscriber.onNext(loginResult.getAccessToken());
                                subscriber.onCompleted();
                            }else{
                                Timber.d("Current access without publish");
                                LoginManager.getInstance().logInWithPublishPermissions(activity, Arrays.asList(PUBLISH_PERMISSION));
                            }
                        }

                        @Override
                        public void onCancel() {
                            Timber.d("Current access token cancelled");
                            AccessToken accessToken = AccessToken.getCurrentAccessToken();
                            if(accessToken!=null){
                                subscriber.onNext(accessToken);
                                subscriber.onCompleted();
                            }else{
                                subscriber.onError(new Exception("Login cancelled"));
                            }
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                            Timber.d("Current access onerror: "+exception.getMessage());
                            subscriber.onError(new FacebookRequestException(exception));
                        }
                    });

            LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email", "user_friends"));
        }
    }

//    private class FacebookAuthenticationSubscribe implements Observable.OnSubscribe<AccessToken>
//    {
//        private final Activity activity;
//        private final List<String> permissions;
//
//        public FacebookAuthenticationSubscribe(Activity activity, List<String> permissions)
//        {
//            this.activity = activity;
//            this.permissions = permissions;
//        }
//
//        @Override public void call(final Subscriber<? super AccessToken> subscriber)
//        {
//            Assertions.assertUiThread();
//            final Session.StatusCallback statusCallback = new SubscriberCallback(subscriber);
//
//            Session activeSession = Session.getActiveSession();
//
//            if (activeSession != null && activeSession.isOpened())
//            {
//                // if requesting permissions is just subset of activeSession.getPermissions() - existing one
//                //if (isSubsetPermissions(activeSession.getPermissions(), permissions))
//                List<String> currentPermissions = activeSession.getPermissions();
//                if ((permissions == null)
//                        || ((currentPermissions != null) && (currentPermissions.containsAll(permissions)))
//                   )
//                {
//                    subscriber.onNext(activeSession);
//                    subscriber.onCompleted();
//                    return;
//                }
//            }
//
//            activeSession = new Session.Builder(activity)
//                    .setApplicationId(applicationId)
//                    .setTokenCachingStrategy(tokenCachingStrategy)
//                    .build();
//            Session.OpenRequest openRequest = new Session.OpenRequest(activity);
//            openRequest.setRequestCode(activityCode);
//            if (this.permissions != null)
//            {
//                openRequest.setPermissions(new ArrayList<>(this.permissions));
//            }
//            Session.setActiveSession(activeSession);
//            openRequest.setCallback(statusCallback);
//            // TODO change to read and request for publish on demand
//            activeSession.openForPublish(openRequest);
//
//            Subscription subscription = AndroidSubscriptions.unsubscribeInUiThread(new Action0()
//            {
//                @Override public void call()
//                {
//                    Session activeSession = Session.getActiveSession();
//                    if (activeSession != null)
//                    {
//                        activeSession.removeCallback(statusCallback);
//                    }
//
//                }
//            });
//
//            subscriber.add(subscription);
//        }
//    }
}