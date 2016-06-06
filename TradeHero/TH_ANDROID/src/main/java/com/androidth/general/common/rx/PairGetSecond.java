package com.androidth.general.common.rx;

import android.util.Pair;
import rx.functions.Func1;

/**
 * Will help reduce the number of methods in the project
 */
public class PairGetSecond<S, T> implements Func1<Pair<S, T>, T>
{
    @Override public T call(Pair<S, T> pair)
    {
        return pair.second;
    }
}
