package com.tradehero.th.rx.view.list;

import android.widget.AbsListView;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
import rx.subjects.PublishSubject;

public class ListViewObservable
{
    public static Observable<Object> itemClicks(final AbsListView absListView)
    {
        PublishSubject<Object> subject = PublishSubject.create();
        absListView.setOnItemClickListener((parent, view, position, id) -> subject.onNext(parent.getItemAtPosition(position)));
        return subject.asObservable();
    }

    public static <T> NearEndScrollOperator<T> createNearEndScrollOperator(Subscriber<T> subscriber, Func0<T> func)
    {
        return new NearEndScrollOperator<>(subscriber, func);
    }
}
