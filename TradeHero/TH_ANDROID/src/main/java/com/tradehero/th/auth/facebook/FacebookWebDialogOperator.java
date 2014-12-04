package com.tradehero.th.auth.facebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.facebook.widget.WebDialog;
import rx.Observable;
import rx.Subscriber;

public class FacebookWebDialogOperator implements Observable.OnSubscribe<Bundle>
{
    @NonNull private final WebDialog.RequestsDialogBuilder dialogBuilder;

    //<editor-fold desc="Constructors">
    public FacebookWebDialogOperator(@NonNull WebDialog.RequestsDialogBuilder dialogBuilder)
    {
        this.dialogBuilder = dialogBuilder;
    }
    //</editor-fold>

    @Override public void call(Subscriber<? super Bundle> subscriber)
    {
        dialogBuilder.setOnCompleteListener(
                (values, error) -> {
                    if (error != null)
                    {
                        subscriber.onError(error);
                    }
                    else
                    {
                        subscriber.onNext(values);
                        subscriber.onCompleted();
                    }
                })
                .build()
                .show();
    }
}
