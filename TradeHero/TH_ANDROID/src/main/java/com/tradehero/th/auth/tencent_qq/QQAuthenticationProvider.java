package com.tradehero.th.auth.tencent_qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.base.JSONCredentials;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class QQAuthenticationProvider extends SocialAuthenticationProvider
{
    private static final String SCOPE = "all";
    private final Tencent mTencent;
    private QQAppAuthData mAccessToken;

    public static final String KEY_ACCESS_TOKEN = "qq_access_token";
    public static final String KEY_OPEN_ID = "qq_openid";

    //<editor-fold desc="Constructors">
    @Inject public QQAuthenticationProvider(Tencent tencent)
    {
        this.mTencent = tencent;
    }
    //</editor-fold>

    @Override
    public void authenticate(THAuthenticationCallback callback)
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

        Context context = baseContext.get();
        mTencent.logout(context);
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
        }
        catch (JSONException e)
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

    @Override public void deauthenticate()
    {
    }

    @Override
    public boolean restoreAuthentication(JSONCredentials paramJSONObject)
    {
        return false;
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

    @Override protected Observable<AuthData> createAuthDataObservable(Activity activity)
    {
        throw new RuntimeException("Not implemented");
    }
}
