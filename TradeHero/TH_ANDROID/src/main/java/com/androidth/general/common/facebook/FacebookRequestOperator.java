package com.androidth.general.common.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookPermissionsConstants;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import rx.Observable;
import rx.Subscriber;
import rx.android.internal.Assertions;
import timber.log.Timber;

/**
 * It should be noted that only one Facebook Request can run at a time
 */
public class FacebookRequestOperator implements Observable.OnSubscribe<AccessToken>
{
//    @NonNull private final Session session;
    @NonNull private AccessToken accessToken;
    @NonNull private final String graphPath;
    @Nullable private final Bundle parameters;
    @Nullable private final HttpMethod httpMethod;
    @Nullable private final String version;
    @NonNull private final CallbackManager callbackManager;
    @NonNull private final Activity activity;
    private String PUBLISH_PERMISSION = "publish_actions";

    //<editor-fold desc="Constructors">
    private FacebookRequestOperator(@NonNull Builder builder)
    {
//        this.session = builder.session;
        this.activity = builder.activity;
        this.callbackManager = builder.callbackManager;
        this.accessToken = builder.accessToken;
        this.graphPath = builder.graphPath;
        this.parameters = builder.parameters;
        this.httpMethod = builder.httpMethod;
        this.version = builder.version;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super AccessToken> subscriber)
    {
        Assertions.assertUiThread();
//        Request request = new Request(
//                session,
//                graphPath,
//                parameters,
//                httpMethod,
//                new Request.Callback()
//                {
//                    @Override public void onCompleted(Response response)
//                    {
//                        FacebookRequestError e = response.getError();
//                        if (e != null)
//                        {
//                            subscriber.onError(new FacebookRequestException(e));
//                        }
//                        else
//                        {
//                            subscriber.onNext(response);
//                            subscriber.onCompleted();
//                        }
//                    }
//                },
//                version);
//        Timber.d("Facebook request version %s", request.getVersion());
//        Request.executeBatchAsync(request);

        Timber.d("Current access token "+AccessToken.getCurrentAccessToken());

        if(AccessToken.getCurrentAccessToken()!=null){
            subscriber.onNext(AccessToken.getCurrentAccessToken());
            subscriber.onCompleted();
        }else{
            setupLoginByFacebook(subscriber);
        }
    }

    public void setupLoginByFacebook(Subscriber subscriber){

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
                            subscriber.onNext(loginResult.getAccessToken());
                            subscriber.onCompleted();
                        }else{
                            LoginManager.getInstance().logInWithPublishPermissions(activity, Arrays.asList(PUBLISH_PERMISSION));
                        }
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        subscriber.onError(new FacebookRequestException(exception));
                    }
                });

        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email", "user_friends"));
    }

    public static class Builder
    {
//        @NonNull private final Session session;
        @NonNull private final AccessToken accessToken;
        @NonNull private final CallbackManager callbackManager;
        @NonNull private final Activity activity;
        @NonNull private String graphPath;
        @Nullable private Bundle parameters;
        @Nullable private HttpMethod httpMethod;
        @Nullable private String version;

        //<editor-fold desc="Constructors">
        private Builder(@NonNull Activity activity,
                @NonNull AccessToken accessToken,
                        @NonNull String graphPath,
                        @NonNull CallbackManager callbackManager)
        {
            this.activity = activity;
            this.callbackManager = callbackManager;
            this.accessToken = accessToken;
            this.graphPath = graphPath;
        }
        //</editor-fold>

        @NonNull public Builder setParameters(@NonNull Bundle parameters)
        {
            this.parameters = parameters;
            return this;
        }

        @NonNull public Builder setHttpMethod(@NonNull HttpMethod httpMethod)
        {
            this.httpMethod = httpMethod;
            return this;
        }

        @NonNull public Builder setVersion(@NonNull String version)
        {
            this.version = version;
            return this;
        }

        @NonNull public FacebookRequestOperator build()
        {
            return new FacebookRequestOperator(this);
        }
    }

    @NonNull public static Builder builder(@NonNull Activity activity, @NonNull AccessToken accessToken, @NonNull String graphPath, @NonNull CallbackManager callbackManager)
    {
        return new Builder(activity, accessToken, graphPath, callbackManager);
    }
}
