package com.tradehero.th.rx.view;

import android.view.View;
import rx.Observable;
import rx.android.observables.ViewObservable;

public class ViewArrayObservable
{
    /**
     * Observes clicks on individual non-null views
     * @param views
     * @param emitInitialValue
     * @param <T>
     * @return
     */
    public static <T extends View> Observable<T> clicks(final T[] views, final boolean emitInitialValue)
    {
        return Observable.from(views)
                .filter(t -> t != null)
                .flatMap(view -> ViewObservable.clicks(view, false));
    }
}
