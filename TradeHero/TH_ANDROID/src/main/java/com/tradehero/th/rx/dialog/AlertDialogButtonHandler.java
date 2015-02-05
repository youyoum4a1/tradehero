package com.tradehero.th.rx.dialog;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Pair;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

public class AlertDialogButtonHandler implements Func1<OnDialogClickEvent, Observable<OnDialogClickEvent>>
{
    private final int buttonIndex;
    @NonNull private final Action0 action;

    public AlertDialogButtonHandler(int buttonIndex, @NonNull Action0 action)
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
