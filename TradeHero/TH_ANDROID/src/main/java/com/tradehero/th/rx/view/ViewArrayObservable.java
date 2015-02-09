package com.tradehero.th.rx.view;

import android.view.View;
import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;

public class ViewArrayObservable
{
    /**
     * Observes clicks on individual non-null views
     * @param views
     * @param emitInitialValue
     * @return
     */
    public static Observable<OnClickEvent> clicks(final View[] views, final boolean emitInitialValue)
    {
        return Observable.from(views)
                .filter(t -> t != null)
                .flatMap(view -> ViewObservable.clicks(view, emitInitialValue));
    }
}