package com.tradehero.th.auth.facebook;

import android.os.Bundle;
import com.facebook.FacebookException;
import com.facebook.widget.WebDialog;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Subscriber;

public class WebDialogCompleteListenerOnSubscribe implements Observable.OnSubscribe<Bundle>
{
    @NotNull private WebDialog webDialog;

    //<editor-fold desc="Constructors">
    public WebDialogCompleteListenerOnSubscribe(@NotNull WebDialog webDialog)
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
