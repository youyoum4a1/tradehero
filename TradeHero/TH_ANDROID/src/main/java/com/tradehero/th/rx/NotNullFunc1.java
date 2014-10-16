package com.tradehero.th.rx;

import rx.functions.Func1;

public class NotNullFunc1<T> implements Func1<T, Boolean>
{
    @Override public Boolean call(T object)
    {
        return object != null;
    }
}
