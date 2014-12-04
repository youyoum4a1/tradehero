package com.tradehero.common.persistence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BaseDTOCacheRx<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements DTOCacheRx<DTOKeyType, DTOType>
{
    @NonNull final private Lock cachedValuesLock;
    @NonNull final private THLruCache<DTOKeyType, DTOType> cachedValues;
    @NonNull final private Map<DTOKeyType, BehaviorSubject<Pair<DTOKeyType, DTOType>>> cachedSubjects;
    @NonNull final private Map<DTOKeyType, Observable<Pair<DTOKeyType, DTOType>>> cachedObservables;

    //<editor-fold desc="Constructors">
    protected BaseDTOCacheRx(int valueSize, int subjectSize,
            @NonNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        this.cachedValuesLock = new ReentrantLock();
        this.cachedValues = new THLruCache<>(valueSize);
        this.cachedSubjects = new HashMap<>();
        this.cachedObservables = new HashMap<>();
        dtoCacheUtilRx.addCache(this);
    }
    //</editor-fold>

    @NonNull @Override
    public Observable<Pair<DTOKeyType, DTOType>> get(@NonNull final DTOKeyType key)
    {
        return getOrCreateObservable(key).asObservable();
    }

    @NonNull protected Observable<Pair<DTOKeyType, DTOType>> getOrCreateObservable(@NonNull final DTOKeyType key)
    {
        Observable<Pair<DTOKeyType, DTOType>> cachedObservable = getObservable(key);
        if (cachedObservable == null)
        {
            BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject;
            DTOType cachedValue = getValue(key);
            if (cachedValue != null)
            {
                cachedSubject = BehaviorSubject.create(Pair.create(key, cachedValue));
            }
            else
            {
                cachedSubject = BehaviorSubject.create();
            }
            final RefCounter counter = new RefCounter(); // HACK This helps the cachedSubject act as a refCount.
            cachedObservable = cachedSubject
                    .doOnSubscribe(counter::plusOne)
                    .doOnUnsubscribe(() -> {
                        counter.minusOne();
                        removeConditional(key, counter, cachedSubject);
                    });
            cachedSubjects.put(key, cachedSubject);
            cachedObservables.put(key, cachedObservable);
        }

        return cachedObservable;
    }

    @Nullable protected BehaviorSubject<Pair<DTOKeyType, DTOType>> getBehavior(@NonNull final DTOKeyType key)
    {
        return cachedSubjects.get(key);
    }

    @Nullable protected Observable<Pair<DTOKeyType, DTOType>> getObservable(@NonNull final DTOKeyType key)
    {
        return cachedObservables.get(key);
    }

    @NonNull public Observable<Pair<DTOKeyType, DTOType>> getFirstOrEmpty(@NonNull final DTOKeyType key)
    {
        DTOType value = getValue(key);
        if (value == null)
        {
            return Observable.empty();
        }
        return Observable.just(Pair.create(key, value));
    }

    @Override public void onNext(@NonNull DTOKeyType key, @NonNull DTOType value)
    {
        putValue(key, value);
        BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject = cachedSubjects.get(key);
        if (cachedSubject != null)
        {
            cachedSubject.onNext(Pair.create(key, value));
        }
    }

    @Nullable protected DTOType putValue(@NonNull DTOKeyType key, @NonNull DTOType value)
    {
        DTOType previous;
        try
        {
            cachedValuesLock.lock();
            previous = cachedValues.put(key, value);
        } finally
        {
            cachedValuesLock.unlock();
        }
        return previous;
    }

    // TODO make it protected when all is cleaned up
    @Deprecated
    @Nullable public DTOType getValue(@NonNull DTOKeyType key)
    {
        DTOType cachedValue;
        try
        {
            cachedValuesLock.lock();
            cachedValue = cachedValues.get(key);
        } finally
        {
            cachedValuesLock.unlock();
        }
        if (cachedValue != null && !isValid(cachedValue))
        {
            cachedValue = null;
        }
        return cachedValue;
    }

    protected boolean isValid(@NonNull DTOType value)
    {
        //noinspection RedundantIfStatement
        if (value instanceof HasExpiration && ((HasExpiration) value).getExpiresInSeconds() <= 0)
        {
            return false;
        }
        return true;
    }

    @Override public void invalidate(@NonNull DTOKeyType key)
    {
        try
        {
            cachedValuesLock.lock();
            cachedValues.remove(key);
        } finally
        {
            cachedValuesLock.unlock();
        }
    }

    @Override public void invalidateAll()
    {
        Collection<BehaviorSubject<Pair<DTOKeyType, DTOType>>> subjects = cachedSubjects.values();
        cachedSubjects.clear();
        cachedObservables.clear();
        for (BehaviorSubject<Pair<DTOKeyType, DTOType>> subject : subjects)
        {
            subject.onCompleted();
        }
        try
        {
            cachedValuesLock.lock();
            cachedValues.evictAll();
        } finally
        {
            cachedValuesLock.unlock();
        }
    }

    /**
     * Removes the observable and cached subject when the counter is 0
     */
    protected void removeConditional(
            @NonNull DTOKeyType key,
            @NonNull RefCounter counter,
            @NonNull BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject)
    {
        if (counter.get() == 0 && cachedSubjects.get(key) == cachedSubject)
        {
            cachedObservables.remove(key);
            try
            {
                cachedValuesLock.lock();
                cachedSubjects.remove(key);
            } finally
            {
                cachedValuesLock.unlock();
            }
        }
    }

    @NonNull protected Map<DTOKeyType, DTOType> snapshot()
    {
        try
        {
            cachedValuesLock.lock();
            return cachedValues.snapshot();
        } finally
        {
            cachedValuesLock.unlock();
        }
    }

    protected static class RefCounter
    {
        private int subscriberCount = 0;

        synchronized void plusOne()
        {
            subscriberCount++;
        }

        synchronized void minusOne()
        {
            subscriberCount--;
        }

        synchronized int get()
        {
            return subscriberCount;
        }
    }
}
