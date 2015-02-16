package com.tradehero.th.rx;

import rx.functions.Func1;

public class ReplaceWith<T1, R> implements Func1<T1, R>
{
    private final R r;

    public ReplaceWith(R r)
    {
        this.r = r;
    }

    public R call(T1 t1)
    {
        return r;
    }
}
