package com.ayondo.academy.rx.view;

import android.support.annotation.NonNull;
import android.view.View;
import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Func1;

public class ViewArrayObservable
{
    /**
     * Observes clicks on individual non-null views
     * @param views
     * @param emitInitialValue
     * @return
     */
    @NonNull public static Observable<OnClickEvent> clicks(@NonNull View[] views, final boolean emitInitialValue)
    {
        return Observable.from(views)
                .flatMap(new Func1<View, Observable<? extends OnClickEvent>>()
                {
                    @Override public Observable<? extends OnClickEvent> call(View view)
                    {
                        if (view != null)
                        {
                            return ViewObservable.clicks(view, emitInitialValue);
                        }
                        return Observable.empty();
                    }
                });
    }

    @NonNull public static Observable<OnClickEvent> clicks(@NonNull Iterable<View> views, final boolean emitInitialValue)
    {
        return Observable.from(views)
                .flatMap(new Func1<View, Observable<? extends OnClickEvent>>()
                {
                    @Override public Observable<? extends OnClickEvent> call(View view)
                    {
                        if (view != null)
                        {
                            return ViewObservable.clicks(view, emitInitialValue);
                        }
                        return Observable.empty();
                    }
                });
    }
}
