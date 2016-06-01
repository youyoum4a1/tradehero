package com.ayondo.academy.rx.dialog;

import android.support.annotation.NonNull;
import com.tradehero.common.annotation.DialogButton;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

public class AlertDialogButtonHandler implements Func1<OnDialogClickEvent, Observable<OnDialogClickEvent>>
{
    @DialogButton private final int buttonIndex;
    @NonNull private final Action0 action;

    public AlertDialogButtonHandler(@DialogButton int buttonIndex, @NonNull Action0 action)
    {
        this.buttonIndex = buttonIndex;
        this.action = action;
    }

    @Override public Observable<OnDialogClickEvent> call(OnDialogClickEvent event)
    {
        if (event.which == buttonIndex)
        {
            action.call();
            return Observable.empty();
        }
        return Observable.just(event);
    }
}
