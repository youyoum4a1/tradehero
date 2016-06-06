package com.androidth.general.common.facebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.facebook.FacebookException;
import com.facebook.widget.WebDialog;
import rx.Observer;

public class SubscriberOnCompleteListener implements WebDialog.OnCompleteListener
{
    @NonNull private Observer<? super Bundle> observer;

    //<editor-fold desc="Constructors">
    public SubscriberOnCompleteListener(@NonNull Observer<? super Bundle> observer)
    {
        this.observer = observer;
    }
    //</editor-fold>

    @Override public void onComplete(@Nullable Bundle values, @Nullable FacebookException error)
    {
        if (error != null)
        {
            observer.onError(error);
        }
        else if (values != null)
        {
            observer.onNext(values);
        }
        else
        {
            observer.onError(new NullPointerException("Both values and error were null"));
        }
    }
}
