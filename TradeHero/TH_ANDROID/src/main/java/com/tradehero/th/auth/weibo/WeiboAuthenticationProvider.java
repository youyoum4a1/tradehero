package com.tradehero.th.auth.weibo;

import android.app.Activity;
import android.os.Bundle;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.auth.operator.ForWeiboAppAuthData;
import com.tradehero.th.network.service.SocialLinker;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

@Singleton
public class WeiboAuthenticationProvider extends SocialAuthenticationProvider
{
    public static final String KEY_UID = "uid";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_EXPIRES_IN = "expires_in";
    private static final String WEIBO_PACKAGE = "com.sina.weibo";

    @NotNull private final WeiboAppAuthData mAuthData;

    //<editor-fold desc="Constructors">
    @Inject public WeiboAuthenticationProvider(
            @NotNull SocialLinker socialLinker,
            @NotNull @ForWeiboAppAuthData WeiboAppAuthData authData)
    {
        super(socialLinker);
        this.mAuthData = authData;
    }
    //</editor-fold>

    @Override public void logout()
    {
        // FIXME Nothing to do?
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
            return Observable.create(new OperatorSsoHandler(
                    activity,
                    new WeiboAuth(activity, mAuthData.appId, mAuthData.redirectUrl, mAuthData.scope)))
                    .map(new Func1<Bundle, AuthData>()
                    {
                        @Override public AuthData call(Bundle bundle)
                        {
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
}
