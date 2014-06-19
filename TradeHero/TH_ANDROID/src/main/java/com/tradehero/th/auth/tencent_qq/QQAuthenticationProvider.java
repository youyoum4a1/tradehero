package com.tradehero.th.auth.tencent_qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.models.user.auth.QQCredentialsDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

/**
 * Created by Windy on 14-5-26.
 */
@Singleton
public class QQAuthenticationProvider extends SocialAuthenticationProvider
{

    private final String APP_ID = "1101331512";
    private static final String SCOPE = "all";
    private Tencent mTencent;
    private QQAppAuthData mAccessToken;

    public static final String KEY_ACCESS_TOKEN = "qq_access_token";
    public static final String KEY_OPEN_ID = "qq_openid";

    @Inject
    public QQAuthenticationProvider()
    {

    }

    @Override
    public void authenticate(THAuthenticationCallback callback)
    {
        Timber.d("windy QQ authenticate!");
        doAuthenticate(callback);
    }

    private void doAuthenticate(THAuthenticationCallback callback)
    {
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

        createQQAuth();
    }

    private void createQQAuth()
    {
        Context context = baseContext.get();
        mTencent = Tencent.createInstance(APP_ID, context.getApplicationContext());
        mTencent.logout((Activity) context);
        if (!mTencent.isSessionValid())
        {
            IUiListener listener = new BaseUiListener()
            {
                @Override
                protected void doComplete(JSONObject values)
                {
                    QQAppAuthData token = QQAppAuthData.parseAccessToken(values);
                    onAnthorizeSuccess(token);
                }
            };
            mTencent.login((Activity) context, SCOPE, listener);
        }
        else
        {
            QQAppAuthData token = new QQAppAuthData();
            token.openid = mTencent.getOpenId();
            token.access_token = mTencent.getAccessToken();
            onAnthorizeSuccess(token);
        }
    }

    private void onAnthorizeSuccess(QQAppAuthData qqData)
    {
        this.mAccessToken = qqData;
        if (checkContext() && currentOperationCallback != null)
        {
            currentOperationCallback.onSuccess(buildTokenData(qqData));
            currentOperationCallback = null;
        }
    }

    private JSONCredentials buildTokenData(QQAppAuthData data)
    {
        JSONCredentials obj = null;
        try
        {
            obj = new JSONCredentials();
            obj.put(QQAuthenticationProvider.KEY_OPEN_ID, data.openid);
            obj.put(QQAuthenticationProvider.KEY_ACCESS_TOKEN, data.access_token);
        } catch (JSONException e)
        {
        }
        return obj;
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

    private void onAuthorizeCancel()
    {
        handleCancel(currentOperationCallback);
        currentOperationCallback = null;
    }

    @Override
    public void deauthenticate()
    {

    }

    @Override
    public boolean restoreAuthentication(JSONCredentials paramJSONObject)
    {
        return false;
    }

    @Override
    public String getAuthType()
    {
        return QQCredentialsDTO.QQ_AUTH_TYPE;
    }

    @Override
    public String getAuthHeaderParameter()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(mAccessToken.openid).append(":").append(mAccessToken.access_token);
        return sb.toString();
    }

    public void authorizeCallBack(int requestCode, int resultCode, Intent data)
    {
        Timber.d("windy requestCode = " + requestCode + "resultCode = " + resultCode + "data = " + data.toString());
    }

    private class BaseUiListener implements IUiListener
    {

        protected void doComplete(JSONObject values)
        {

        }

        @Override
        public void onComplete(Object response)
        {
            Timber.d("windy BaseUiListener on Complete:" + response.toString());
            JSONObject jsonObject;
            try
            {
                jsonObject = new JSONObject(response.toString());
                doComplete(jsonObject);
            } catch (JSONException e)
            {
                Timber.e("QQ BaseUiListener " + e.toString());
            }
        }

        @Override
        public void onError(UiError e)
        {

            Timber.e("windy onError:" + "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
            onAuthorizeCancel();
        }

        @Override
        public void onCancel()
        {
            onAuthorizeCancel();
        }
    }
}
