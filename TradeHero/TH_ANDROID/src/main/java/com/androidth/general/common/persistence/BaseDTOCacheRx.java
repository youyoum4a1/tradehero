package com.androidth.general.common.persistence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import rx.Observable;
import rx.functions.Action0;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class BaseDTOCacheRx<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements DTOCacheRx<DTOKeyType, DTOType>
{
    @NonNull final private Lock cachedValuesLock;
    @NonNull final private THLruCache<DTOKeyType, DTOType> cachedValues;
    @NonNull final private Map<DTOKeyType, BehaviorSubject<Pair<DTOKeyType, DTOType>>> cachedSubjects;
    @NonNull final private Map<DTOKeyType, Observable<Pair<DTOKeyType, DTOType>>> cachedObservables;

    //<editor-fold desc="Constructors">
    protected BaseDTOCacheRx(int valueSize, @NonNull DTOCacheUtilRx dtoCacheUtilRx)
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
            final BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject;
            DTOType cachedValue = getCachedValue(key);
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
                    .doOnSubscribe(new Action0()
                    {
                        @Override public void call()
                        {
                            counter.plusOne();
                        }
                    })
                    .doOnUnsubscribe(new Action0()
                    {
                        @Override public void call()
                        {
                            counter.minusOne();
                            BaseDTOCacheRx.this.removeConditional(key, counter, cachedSubject);
                        }
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

    @Nullable public DTOType getCachedValue(@NonNull DTOKeyType key)
    {
        DTOType cachedValue;
        try
        {
            cachedValuesLock.lock();
            cachedValue = cachedValues.get(key);
        } catch(Exception e){
            e.printStackTrace();
            cachedValue = null;

        }finally
        {
            cachedValuesLock.unlock();
        }
        if (cachedValue != null && !isValid(cachedValue))
        {
            cachedValue = null;
        }
        return cachedValue;
    }

    @NonNull public Observable<Pair<DTOKeyType, DTOType>> getOne(@NonNull DTOKeyType key)
    {
        DTOType cached = getCachedValue(key);
        if (cached != null)
        {
            return Observable.just(Pair.create(key, cached));
        }
        return get(key).take(1);
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
        } catch (IllegalStateException e)
        {
            // HACK because cannot find the reason of
            // https://crashlytics.com/tradehero/android/apps/com.tradehero.th/issues/547fe02d65f8dfea153e0fa5
            Timber.e("on cache %s", getClass(), e);
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
