package com.tradehero.common.persistence;

import java.util.ArrayList;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import rx.Observer;
import rx.Subscriber;

public class ObserverSet<T> extends HashSet<Subscriber<T>>
    implements Observer<T>
{
    //<editor-fold desc="Constructors">
    public ObserverSet()
    {
        super();
    }
    //</editor-fold>

    @Override public void onCompleted()
    {
        for (@NotNull Subscriber<T> subscriber : new ArrayList<>(this))
        {
            if (!subscriber.isUnsubscribed())
            {
                subscriber.onCompleted();
            }
            remove(subscriber);
        }
    }

    @Override public void onError(Throwable e)
    {
        for (@NotNull Subscriber<T> subscriber : new ArrayList<>(this))
        {
            if (!subscriber.isUnsubscribed())
            {
                subscriber.onError(e);
            }
            remove(subscriber);
        }
    }

    @Override public void onNext(T t)
    {
        for (@NotNull Subscriber<T> subscriber : new ArrayList<>(this))
        {
            if (!subscriber.isUnsubscribed())
            {
                subscriber.onNext(t);
            }
        }
    }
}
