package com.androidth.general.common.facebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.facebook.FacebookException;
import com.facebook.widget.WebDialog;
import rx.Observable;
import rx.Subscriber;
import rx.android.internal.Assertions;

public class FacebookWebDialogOperator implements Observable.OnSubscribe<Bundle>
{
    @NonNull private final WebDialog.RequestsDialogBuilder dialogBuilder;

    //<editor-fold desc="Constructors">
    public FacebookWebDialogOperator(@NonNull WebDialog.RequestsDialogBuilder dialogBuilder)
    {
        this.dialogBuilder = dialogBuilder;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super Bundle> subscriber)
    {
        Assertions.assertUiThread();
        dialogBuilder.setOnCompleteListener(
                new WebDialog.OnCompleteListener()
                {
                    @Override public void onComplete(Bundle values, FacebookException error)
                    {
                        if (error != null)
                        {
                            subscriber.onError(error);
                        }
                        else
                        {
                            subscriber.onNext(values);
                            subscriber.onCompleted();
                        }
                    }
                })
                .build()
                .show();
    }
}
