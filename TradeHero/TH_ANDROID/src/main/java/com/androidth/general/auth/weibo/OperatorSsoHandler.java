package com.androidth.general.auth.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import rx.Observable;
import rx.Subscriber;

public class OperatorSsoHandler implements Observable.OnSubscribe<Bundle>
{
    @NonNull private SsoHandler ssoHandler;

    //<editor-fold desc="Constructors">
    public OperatorSsoHandler(@NonNull Activity activity, @NonNull WeiboAuth weiboAuth)
    {
        this.ssoHandler = new SsoHandler(activity, weiboAuth);
    }
    //</editor-fold>

    @Override public void call(@NonNull final Subscriber<? super Bundle> subscriber)
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
