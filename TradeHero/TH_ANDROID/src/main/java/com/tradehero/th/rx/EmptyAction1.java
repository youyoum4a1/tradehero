package com.tradehero.th.rx;

import rx.functions.Action1;

public class EmptyAction1<T> implements Action1<T>
{
    public void call(T t)
    {
    }
}
