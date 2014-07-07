package com.tradehero.th.auth.weibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.auth.operator.ForWeiboAppAuthData;
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

    @NotNull final Context context;
    @NotNull private final WeiboAppAuthData mAuthData;
    //private THAuthenticationCallback mCallback;

    private Oauth2AccessToken mAccessToken;
    private WeiboAuth mWeiboAuth;
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
            throw new RuntimeException("Context is null or is not Activity");
        }
        //        if (true) {
        //            doFakeAuthenti    cate();
        //            return;
        //        }

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

        //        if (isWeiboInstalled() && false) {
        //            authorizeViaClent();
        //        }else {
        //            authorizeViaWeb();
        //        }
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
        mWeiboAuth = new WeiboAuth(context, appId, redirectUrl, scope);
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

    private void authorizeViaWeb()
    {
        mWeiboAuth.anthorize(new AuthListener());
        // 或者使用：mWeiboAuth.authorize(new AuthListener(), Weibo.OBTAIN_AUTH_TOKEN);
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

    private boolean isWeiboInstalled()
    {
        Context context = baseContext.get();
        try
        {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(WEIBO_PACKAGE,
                            PackageManager.GET_ACTIVITIES | PackageManager.GET_SIGNATURES);
            String versionName = info.versionName;
            if (checkWeiboVersion(versionName) && checkSign(info.signatures))
            {
                return true;
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkWeiboVersion(String versionName)
    {
        try
        {
            String[] codeArr = versionName.split("\\.");
            //System.out.println(Arrays.toString(codeArr));

            int bigVer = Integer.parseInt(codeArr[0]);
            if (bigVer > 3)
            {
                return true;
            }
            else if (bigVer < 3)
            {
                return false;
            }
            if (Integer.parseInt(codeArr[1]) > 0)
            {
                return true;
            }
            if (Integer.parseInt(codeArr[2]) > 0)
            {
                return true;
            }
        }
        catch (Exception e)
        {
        }
        return false;
    }

    private boolean checkSign(Signature[] signatures)
    {
        int len = signatures.length;
        for (int j = 0; j < len; j++)
        {
            String str = signatures[j].toCharsString();
            if (str.equals(
                    "30820295308201fea00302010202044b4ef1bf300d06092a864886f70d010105050030818d310b300906035504061302434e3110300e060355040813074265694a696e673110300e060355040713074265694a696e67312c302a060355040a132353696e612e436f6d20546563686e6f6c6f677920284368696e612920436f2e204c7464312c302a060355040b132353696e612e436f6d20546563686e6f6c6f677920284368696e612920436f2e204c74643020170d3130303131343130323831355a180f32303630303130323130323831355a30818d310b300906035504061302434e3110300e060355040813074265694a696e673110300e060355040713074265694a696e67312c302a060355040a132353696e612e436f6d20546563686e6f6c6f677920284368696e612920436f2e204c7464312c302a060355040b132353696e612e436f6d20546563686e6f6c6f677920284368696e612920436f2e204c746430819f300d06092a864886f70d010101050003818d00308189028181009d367115bc206c86c237bb56c8e9033111889b5691f051b28d1aa8e42b66b7413657635b44786ea7e85d451a12a82a331fced99c48717922170b7fc9bc1040753c0d38b4cf2b22094b1df7c55705b0989441e75913a1a8bd2bc591aa729a1013c277c01c98cbec7da5ad7778b2fad62b85ac29ca28ced588638c98d6b7df5a130203010001300d06092a864886f70d0101050500038181000ad4b4c4dec800bd8fd2991adfd70676fce8ba9692ae50475f60ec468d1b758a665e961a3aedbece9fd4d7ce9295cd83f5f19dc441a065689d9820faedbb7c4a4c4635f5ba1293f6da4b72ed32fb8795f736a20c95cda776402099054fccefb4a1a558664ab8d637288feceba9508aa907fc1fe2b1ae5a0dec954ed831c0bea4"))
            {
                return true;
            }
        }
        return false;
    }

    //    private boolean readExistedToken() {
    //        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
    //        // 第一次启动本应用，AccessToken 不可用
    //        Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
    //        if (token != null && token.isSessionValid()) {
    //            mAccessToken = token;
    //        }
    //    }

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
        //        if (checkContext() && mCallback != null) {
        //            handleCancel(mCallback);
        //            mCallback.onCancel();
        //        }
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
        /**
         * THAuthenticationCallback callback;
         *
         * AuthListener(THAuthenticationCallback callback) { this.callback = callback; }
         */

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
