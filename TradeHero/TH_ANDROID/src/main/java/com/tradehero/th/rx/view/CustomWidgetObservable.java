package com.tradehero.th.rx.view;

import android.view.View;
import rx.Observable;

public class CustomWidgetObservable
{
    public static Observable<OnFocusChangeEvent> focus(final View input)
    {
        return focus(input, false);
    }

    public static Observable<OnFocusChangeEvent> focus(final View input, final boolean emitInitialValue)
    {
        return Observable.create(new OnSubscribeViewFocus(input, emitInitialValue));
    }
}
