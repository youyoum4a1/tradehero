package com.tradehero.th.rx.view;

import android.view.View;
import com.tradehero.th.rx.NotNullFunc1;
import rx.Observable;
import rx.android.observables.ViewObservable;
import rx.functions.Func1;

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
                .filter(new NotNullFunc1<T>())
                .flatMap(new Func1<T, Observable<T>>()
                {
                    @Override public Observable<T> call(T view)
                    {
                        return ViewObservable.clicks(view, false);
                    }
                });
    }
}
