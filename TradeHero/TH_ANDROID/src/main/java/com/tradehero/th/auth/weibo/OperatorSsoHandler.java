package com.tradehero.th.auth.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Subscriber;

public class OperatorSsoHandler implements Observable.OnSubscribe<Bundle>
{
    @NotNull private SsoHandler ssoHandler;

    //<editor-fold desc="Constructors">
    public OperatorSsoHandler(@NotNull Activity activity, @NotNull WeiboAuth weiboAuth)
    {
        this.ssoHandler = new SsoHandler(activity, weiboAuth);
    }
    //</editor-fold>

    @Override public void call(@NotNull final Subscriber<? super Bundle> subscriber)
    {
        ssoHandler.authorize(new WeiboAuthListener()
        {
            @Override public void onComplete(Bundle bundle)
            {
                subscriber.onNext(bundle);
                subscriber.onCompleted();
            }

            @Override public void onWeiboException(WeiboException e)
            {
                subscriber.onError(e);
            }

            @Override public void onCancel()
            {
                subscriber.onError(new WeiboCancelledException());
            }
        });
    }

    void authorizeCallBack(int requestCode, int resultCode, Intent data)
    {
        ssoHandler.authorizeCallBack(requestCode, resultCode, data);
    }
}
