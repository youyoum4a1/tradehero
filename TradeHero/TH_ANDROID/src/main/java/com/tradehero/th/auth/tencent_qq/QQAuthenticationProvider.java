package com.tradehero.th.auth.tencent_qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.network.service.SocialLinker;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
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
    @Inject public QQAuthenticationProvider(@NotNull SocialLinker socialLinker, Tencent tencent)
    {
        super(socialLinker);
        this.mTencent = tencent;
    }
    //</editor-fold>

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
