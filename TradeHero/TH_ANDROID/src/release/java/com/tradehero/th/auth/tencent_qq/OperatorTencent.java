package com.androidth.general.auth.tencent_qq;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import java.io.IOException;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

class OperatorTencent implements Observable.OnSubscribe<QQAppAuthData>
{
    @NonNull private final Tencent mTencent;
    @NonNull private final Activity activity;
    @NonNull private final ObjectMapper objectMapper;
    @NonNull private final String scope;

    //<editor-fold desc="Constructors">
    public OperatorTencent(
            @NonNull Tencent mTencent,
            @NonNull Activity activity,
            @NonNull ObjectMapper objectMapper,
            @NonNull String scope)
    {
        this.mTencent = mTencent;
        this.activity = activity;
        this.objectMapper = objectMapper;
        this.scope = scope;
    }
    //</editor-fold>

    @Override public void call(@NonNull final Subscriber<? super QQAppAuthData> subscriber)
    {
        mTencent.login(
                activity,
                scope, new IUiListener()
                {
                    @Override public void onComplete(Object response)
                    {
                        Timber.d("OperatorTencent onComplete:" + response.toString());
                        try
                        {
                            subscriber.onNext(objectMapper.readValue(response.toString(), QQAppAuthData.class));
                            subscriber.onCompleted();
                        }
                        catch (IOException e)
                        {
                            subscriber.onError(e);
                            Timber.e("QQ OperatorTencent " + e.toString());
                        }
                    }

                    @Override public void onError(UiError uiError)
                    {
                        subscriber.onError(new QQUiErrorException(uiError));
                    }

                    @Override public void onCancel()
                    {
                        subscriber.onError(new QQCancelException());
                    }
                });
    }
}
