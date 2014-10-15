package com.tradehero.th.auth.tencent_qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tencent.tauth.Tencent;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.network.service.SocialLinker;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class QQAuthenticationProvider extends SocialAuthenticationProvider
{
    private static final String SCOPE = "all";
    @NotNull private final Context context;
    @NotNull private final Tencent tencent;

    //<editor-fold desc="Constructors">
    @Inject public QQAuthenticationProvider(
            @NotNull SocialLinker socialLinker,
            @NotNull Context context,
            @NotNull Tencent tencent)
    {
        super(socialLinker);
        this.context = context;
        this.tencent = tencent;
    }
    //</editor-fold>

    @Override protected Observable<AuthData> createAuthDataObservable(Activity activity)
    {
        tencent.logout(activity);

        return Observable.create(new OperatorTencent(tencent, activity, SCOPE))
            .map(new Func1<QQAppAuthData, AuthData>()
            {
                @Override public AuthData call(@NotNull QQAppAuthData qqAppAuthData)
                {
                    return new AuthData(
                            SocialNetworkEnum.QQ,
                            null, // FIXME TODO expiration
                            qqAppAuthData.accessToken,
                            qqAppAuthData.openId);
                }
            });
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // do nothing
    }

    @Override public void logout()
    {
        tencent.logout(context);
    }
}
