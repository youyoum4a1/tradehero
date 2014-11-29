package com.tradehero.th.auth.tencent_qq;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import org.json.JSONException;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

class OperatorTencent implements Observable.OnSubscribe<QQAppAuthData>
{
    @NonNull private final Tencent mTencent;
    @NonNull private final Activity activity;
    @NonNull private final String scope;

    //<editor-fold desc="Constructors">
    public OperatorTencent(@NonNull Tencent mTencent,
            @NonNull Activity activity,
            @NonNull String scope)
    {
        this.mTencent = mTencent;
        this.activity = activity;
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
                            subscriber.onNext(new QQAppAuthData(response));
                            subscriber.onCompleted();
                        }
                        catch (JSONException e)
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
