package com.androidth.general.common.rx;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.functions.Func1;

public class DurationMeasurer<T> implements Func1<T, Observable<Pair<Long, TimeUnit>>>
{
    @NonNull final Action1<T> action;
    @NonNull final TimeUnit timeUnit;
    @Nullable final Scheduler scheduler;

    //<editor-fold desc="Constructors">
    public DurationMeasurer(@NonNull Action1<T> action, @Nullable Scheduler scheduler)
    {
        this(action, TimeUnit.NANOSECONDS, scheduler);
    }

    public DurationMeasurer(@NonNull Action1<T> action, @NonNull TimeUnit timeUnit, @Nullable Scheduler scheduler)
    {
        this.action = action;
        this.timeUnit = timeUnit;
        this.scheduler = scheduler;
    }
    //</editor-fold>

    @Override public Observable<Pair<Long, TimeUnit>> call(T t)
    {
        final long nanoStartTime = System.nanoTime();
        if (scheduler == null)
        {
            action.call(t);
            return Observable.just(getFrom(nanoStartTime));
        }
        return Observable.just(t)
                .subscribeOn(scheduler)
                .doOnNext(action)
                .map(new Func1<T, Pair<Long, TimeUnit>>()
                {
                    @Override public Pair<Long, TimeUnit> call(T ignored)
                    {
                        return DurationMeasurer.this.getFrom(nanoStartTime);
                    }
                });
    }

    @NonNull protected Pair<Long, TimeUnit> getFrom(long nanoStartTime)
    {
        return Pair.create(timeUnit.convert(System.nanoTime() - nanoStartTime, TimeUnit.NANOSECONDS), timeUnit);
    }

}
