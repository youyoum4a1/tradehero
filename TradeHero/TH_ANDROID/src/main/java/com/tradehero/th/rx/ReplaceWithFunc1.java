package com.ayondo.academy.rx;

import rx.functions.Func1;

public class ReplaceWithFunc1<T1, R> implements Func1<T1, R>
{
    private final R r;

    public ReplaceWithFunc1(R r)
    {
        this.r = r;
    }

    public final R call(T1 ignored)
    {
        return r;
    }
}
