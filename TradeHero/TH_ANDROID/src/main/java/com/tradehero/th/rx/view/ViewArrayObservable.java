package com.tradehero.th.rx.view;

import android.view.View;
import rx.Observable;
import rx.android.view.ViewObservable;

public class ViewArrayObservable
{
    /**
     * Observes clicks on individual non-null views
     * @param views
     * @param emitInitialValue
     * @param <T>
     * @return
     */
    @Deprecated // TODO review the casting
    public static <T extends View> Observable<T> clicks(final T[] views, final boolean emitInitialValue)
    {
        return Observable.from(views)
                .filter(t -> t != null)
                .flatMap(view -> ViewObservable.clicks(view, false))
                .map(event -> (T) event.view());
    }
}
