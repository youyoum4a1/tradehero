package com.ayondo.academy.rx.view.list;

import rx.Subscriber;
import rx.functions.Func0;

public class ListViewObservable
{
    public static <T> NearEndScrollOperator<T> createNearEndScrollOperator(Subscriber<? super T> subscriber, Func0<? extends T> func)
    {
        return new NearEndScrollOperator<>(subscriber, func);
    }
}
