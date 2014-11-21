package com.tradehero.th.auth.facebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.facebook.FacebookException;
import com.facebook.widget.WebDialog;
import rx.Observable;
import rx.Subscriber;

public class WebDialogCompleteListenerOnSubscribe implements Observable.OnSubscribe<Bundle>
{
    @NonNull private WebDialog webDialog;

    //<editor-fold desc="Constructors">
    public WebDialogCompleteListenerOnSubscribe(@NonNull WebDialog webDialog)
    {
        this.webDialog = webDialog;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super Bundle> subscriber)
    {
        webDialog.setOnCompleteListener(new WebDialog.OnCompleteListener()
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
                }

            }
        });
    }
}
