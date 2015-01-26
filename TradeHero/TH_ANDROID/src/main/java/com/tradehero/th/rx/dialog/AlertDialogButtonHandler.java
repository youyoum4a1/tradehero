package com.tradehero.th.rx.dialog;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Pair;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

public class AlertDialogButtonHandler implements Func1<Pair<DialogInterface, Integer>, Observable<Pair<DialogInterface, Integer>>>
{
    private final int buttonIndex;
    @NonNull private final Action0 action;

    public AlertDialogButtonHandler(int buttonIndex, @NonNull Action0 action)
    {
        this.buttonIndex = buttonIndex;
        this.action = action;
    }

    @Override public Observable<Pair<DialogInterface, Integer>> call(Pair<DialogInterface, Integer> pair)
    {
        if (pair.second.equals(buttonIndex))
        {
            action.call();
            return Observable.empty();
        }
        return Observable.just(pair);
    }
}
