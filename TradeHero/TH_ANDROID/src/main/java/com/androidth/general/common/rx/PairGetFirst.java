package com.androidth.general.common.rx;

import android.util.Pair;
import rx.functions.Func1;

/**
 * Will help reduce the number of methods in the project
 */
public class PairGetFirst<S, T> implements Func1<Pair<S, T>, S>
{
    @Override public S call(Pair<S, T> pair)
    {
        return pair.first;
    }
}
