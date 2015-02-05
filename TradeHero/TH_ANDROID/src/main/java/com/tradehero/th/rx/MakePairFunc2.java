package com.tradehero.th.rx;

import android.util.Pair;
import rx.functions.Func2;

@Deprecated
public class MakePairFunc2<T1, T2> implements Func2<T1, T2, Pair<T1, T2>>
{
    @Override public Pair<T1, T2> call(T1 t1, T2 t2)
    {
        return Pair.create(t1, t2);
    }
}
