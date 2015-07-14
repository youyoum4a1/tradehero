package com.tradehero.th.auth.weibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.auth.operator.ForWeiboAppAuthData;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.models.user.auth.WeiboCredentialsDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

@Singleton
public class WeiboAuthenticationProvider extends SocialAuthenticationProvider
{
    public static final String KEY_UID = "uid";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_EXPIRES_IN = "expires_in";
    private static final String WEIBO_PACKAGE = "com.sina.weibo";

    @NotNull Context context;
    @NotNull private final WeiboAppAuthData mAuthData;

    private Oauth2AccessToken mAccessToken;
    private AuthInfo mWeiboAuth;
    private SsoHandler mSsoHandler;

    //<editor-fold desc="Constructors">
    @Inject public WeiboAuthenticationProvider(
            @NotNull Context context,
            @NotNull @ForWeiboAppAuthData WeiboAppAuthData authData)
    {
        this.context = context;
        this.mAuthData = authData;
    }
    //</editor-fold>

    @Override
    public void authenticate(THAuthenticationCallback callback)
    {
        //this.mCallback = callback;
        doAuthenticate(callback);
    }

    @Override
    public void deauthenticate()
    {
        doDeauthenticate();
    }

    @Override
    public boolean restoreAuthentication(JSONCredentials paramJSONObject)
    {
        if (paramJSONObject == null)
        {
            deauthenticate();
            return false;
        }
        boolean valid = isTokenValid(paramJSONObject);
        if (!valid)
        {
            deauthenticate();
        }
        return valid;
    }

    @Override
    public String getAuthType() {
        return WeiboCredentialsDTO.WEIBO_AUTH_TYPE;
    }

    @Override public String getAuthHeader()
    {
        return getAuthType() + " " + getAuthHeaderParameter();
    }

    @Override public String getAuthHeaderParameter()
    {
        return this.mAccessToken.getToken();
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */

    private void doAuthenticate(THAuthenticationCallback callback)
    {
        boolean contextValid = checkContext();
        if (!contextValid)
        {
            context = Application.context();
            if(context==null){
                throw new RuntimeException("Context is null or is not Activity");
            }
        }

        if (callback == null)
        {
            return;
        }
        if (currentOperationCallback != null)
        {
            onAuthorizeCancel();
        }
        currentOperationCallback = callback;

        callback.onStart();

        createWeiboAuth();

        authorizeViaClent();
    }

    private void doFakeAuthenticate()
    {
        String token = "2.00nVl6mCyY87OC5f79cc54cbm2BkEB";
        Oauth2AccessToken o = new Oauth2AccessToken();
        o.setToken(token);
        o.setExpiresTime(10000);
        onAnthorizeSuccess(o);
    }

    private void createWeiboAuth()
    {
        Context context = baseContext.get();
        String appId = mAuthData.appId;
        String redirectUrl = mAuthData.redirectUrl;
        String scope = mAuthData.scope;
        // 创建微博实例(create instance)
        mWeiboAuth = new AuthInfo(context, appId, redirectUrl, scope);
    }

    private boolean checkContext()
    {
        if (baseContext != null && baseContext.get() != null)
        {
            Context ctx = baseContext.get();
            if (ctx instanceof Activity)
            {
                return !((Activity) ctx).isFinishing();
            }
        }
        return false;
    }


    private void authorizeViaClent()
    {
        Activity activity = (Activity) baseContext.get();
        mSsoHandler = new SsoHandler(activity, mWeiboAuth);
        mSsoHandler.authorize(new AuthListener());
    }

    public void authorizeCallBack(int requestCode, int resultCode, Intent data)
    {
        if (mSsoHandler != null)
        {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private void doDeauthenticate()
    {
    }

    private void onAnthorizeSuccess(Oauth2AccessToken token)
    {
        this.mAccessToken = token;
        if (checkContext() && currentOperationCallback != null)
        {
            currentOperationCallback.onSuccess(buildTokenData(token));
            currentOperationCallback = null;
        }
    }

    private void onAuthorizeStart()
    {
        if (checkContext() && currentOperationCallback != null)
        {
            currentOperationCallback.onStart();
        }
    }

    private void onAuthorizeError(Exception e)
    {
        if (checkContext() && currentOperationCallback != null)
        {
            currentOperationCallback.onError(e);
            currentOperationCallback = null;
        }
    }

    private void onAuthorizeCancel()
    {
        handleCancel(currentOperationCallback);
        currentOperationCallback = null;
    }

    private JSONCredentials buildTokenData(Oauth2AccessToken token)
    {
        JSONCredentials obj = null;
        try
        {
            obj = new JSONCredentials();
            obj.put(KEY_UID, token.getUid());
            obj.put(KEY_ACCESS_TOKEN, token.getToken());
            obj.put(KEY_EXPIRES_IN, token.getExpiresTime());
        }
        catch (JSONException e)
        {
        }
        return obj;
    }

    private boolean isTokenValid(JSONCredentials obj)
    {
        try
        {
            String uid = obj.getString(KEY_UID);
            String accessToken = obj.getString(KEY_ACCESS_TOKEN);
            long expiresIn = obj.getLong(KEY_EXPIRES_IN);

            Oauth2AccessToken token = new Oauth2AccessToken();
            token.setUid(uid);
            token.setToken(accessToken);
            token.setExpiresTime(expiresIn);
            return token.isSessionValid();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack}
     * 后， 该回调才会被执行。 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
     * SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener
    {

        @Override
        public void onComplete(Bundle values)
        {
            // 从 Bundle 中解析 Token
            Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(values);
            if (token.isSessionValid())
            {
                onAnthorizeSuccess(token);
                AccessTokenKeeper.writeAccessToken(context, token);
            }
            else
            {
                onAuthorizeError(new RuntimeException("Token is invalid"));
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
            }
        }

        @Override
        public void onCancel()
        {
            onAuthorizeCancel();
        }

        @Override
        public void onWeiboException(WeiboException e)
        {
            onAuthorizeError(e);
        }
    }
}
