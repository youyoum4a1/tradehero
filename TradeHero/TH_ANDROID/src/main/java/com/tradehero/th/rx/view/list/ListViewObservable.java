package com.tradehero.th.rx.view.list;

import rx.Subscriber;
import rx.functions.Func0;

public class ListViewObservable
{
    public static <T> NearEndScrollOperator<T> createNearEndScrollOperator(Subscriber<T> subscriber, Func0<T> func)
    {
        return new NearEndScrollOperator<>(subscriber, func);
    }
}
