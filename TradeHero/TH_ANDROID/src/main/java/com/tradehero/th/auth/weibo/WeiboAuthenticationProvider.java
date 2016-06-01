package com.ayondo.academy.auth.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.tradehero.common.activities.ActivityResultRequester;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.auth.AuthData;
import com.ayondo.academy.auth.SocialAuthenticationProvider;
import com.ayondo.academy.network.service.SocialLinker;
import com.ayondo.academy.network.share.SocialConstants;
import com.ayondo.academy.utils.Constants;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

@Singleton
public class WeiboAuthenticationProvider extends SocialAuthenticationProvider
    implements ActivityResultRequester
{
    public static final String KEY_UID = "uid";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_EXPIRES_IN = "expires_in";
    private static final String WEIBO_PACKAGE = "com.sina.weibo";

    @NonNull private final WeiboAppAuthData appAuthData;
    @Nullable private OperatorSsoHandler operatorSsoHandler;

    //<editor-fold desc="Constructors">
    @Inject public WeiboAuthenticationProvider(
            @NonNull SocialLinker socialLinker)
    {
        super(socialLinker);
        this.appAuthData = SocialConstants.weiboAppAuthData;
    }
    //</editor-fold>

    @Override public void logout()
    {
        // FIXME Nothing to do?
        operatorSsoHandler = null;
    }

    @Override protected Observable<AuthData> createAuthDataObservable(final Activity activity)
    {
        if (!Constants.RELEASE)
        {
            return Observable.just(new AuthData(
                    SocialNetworkEnum.WB,
                    null,
                    "2.00ZJSolF0ZGozS0f48c83cf8syklrD"));
        }
        else
        {
            operatorSsoHandler = new OperatorSsoHandler(
                    activity,
                    new WeiboAuth(activity, appAuthData.appId, appAuthData.redirectUrl, appAuthData.scope));
            return Observable.create(operatorSsoHandler)
                    .map(new Func1<Bundle, AuthData>()
                    {
                        @Override public AuthData call(Bundle bundle)
                        {
                            operatorSsoHandler = null;
                            Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(bundle);
                            if (token.isSessionValid())
                            {
                                return new AuthData(
                                        SocialNetworkEnum.WB,
                                        null, // TODO expiry
                                        token.getToken());
                            }
                            Timber.e(new Exception(), "Token is not valid");
                            // 以下几种情况，您会收到 Code：
                            // 1. 当您未在平台上注册的应用程序的包名与签名时；
                            // 2. 当您注册的应用程序包名与签名不正确时；
                            // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                            throw new RuntimeException("Token is invalid");
                        }
                    });
        }
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Request code is always 32973 ? SsoHandler.REQUEST_CODE_SSO_AUTH
        if (operatorSsoHandler != null)
        {
            operatorSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
