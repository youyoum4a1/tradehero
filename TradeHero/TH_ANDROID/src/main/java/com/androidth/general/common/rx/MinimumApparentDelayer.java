package com.androidth.general.common.rx;

import android.support.annotation.NonNull;
import android.util.Pair;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class MinimumApparentDelayer<T> implements Func1<Pair<Long, TimeUnit>, Observable<T>>
{
    private final T object;
    @NonNull private final Pair<Long, TimeUnit> minimumApparentDelay;

    //<editor-fold desc="Constructors">
    public MinimumApparentDelayer(T object, @NonNull Pair<Long, TimeUnit> minimumApparentDelay)
    {
        this.object = object;
        this.minimumApparentDelay = minimumApparentDelay;
    }
    //</editor-fold>

    @Override public Observable<T> call(Pair<Long, TimeUnit> durationAlreadySpent)
    {
        long durationRemaining = Math.max(0, minimumApparentDelay.first - durationAlreadySpent.first);
        return Observable.just(object)
                .delay(
                        durationRemaining,
                        durationAlreadySpent.second,
                        AndroidSchedulers.mainThread());
    }
}
