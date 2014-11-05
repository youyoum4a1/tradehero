package com.tradehero.common.persistence;

import android.support.annotation.Nullable;
import android.util.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BaseDTOCacheRx<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements DTOCacheRx<DTOKeyType, DTOType>
{
    @NotNull final private THLruCache<DTOKeyType, DTOType> cachedValues;
    @NotNull final private Map<DTOKeyType, BehaviorSubject<Pair<DTOKeyType, DTOType>>> cachedSubjects;
    @NotNull final private Map<DTOKeyType, Observable<Pair<DTOKeyType, DTOType>>> cachedObservables;

    //<editor-fold desc="Constructors">
    protected BaseDTOCacheRx(int valueSize, int subjectSize,
            @NotNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        this.cachedValues = new THLruCache<>(valueSize);
        this.cachedSubjects = new HashMap<>();
        this.cachedObservables = new HashMap<>();
        dtoCacheUtilRx.addCache(this);
    }
    //</editor-fold>

    @NotNull @Override
    public Observable<Pair<DTOKeyType, DTOType>> get(@NotNull final DTOKeyType key)
    {
        return getOrCreateObservable(key).asObservable();
    }

    @NotNull protected Observable<Pair<DTOKeyType, DTOType>> getOrCreateObservable(@NotNull final DTOKeyType key)
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

    @Nullable protected BehaviorSubject<Pair<DTOKeyType, DTOType>> getBehavior(@NotNull final DTOKeyType key)
    {
        return cachedSubjects.get(key);
    }

    @Nullable protected Observable<Pair<DTOKeyType, DTOType>> getObservable(@NotNull final DTOKeyType key)
    {
        return cachedObservables.get(key);
    }

    @NotNull public Observable<Pair<DTOKeyType, DTOType>> getFirstOrEmpty(@NotNull final  DTOKeyType key)
    {
        DTOType value = getValue(key);
        if (value == null)
        {
            return Observable.empty();
        }
        return Observable.just(Pair.create(key, value));
    }

    @Override public void onNext(@NotNull DTOKeyType key, @NotNull DTOType value)
    {
        putValue(key, value);
        BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject = cachedSubjects.get(key);
        if (cachedSubject != null)
        {
            cachedSubject.onNext(Pair.create(key, value));
        }
    }

    protected DTOType putValue(@NotNull DTOKeyType key, @NotNull DTOType value)
    {
        return cachedValues.put(key, value);
    }

    // TODO make it protected when all is cleaned up
    @Deprecated
    @Nullable public DTOType getValue(@NotNull DTOKeyType key)
    {
        DTOType cachedValue = cachedValues.get(key);
        if (cachedValue != null && !isValid(cachedValue))
        {
            cachedValue = null;
        }
        return cachedValue;
    }

    protected boolean isValid(@NotNull DTOType value)
    {
        //noinspection RedundantIfStatement
        if (value instanceof HasExpiration && ((HasExpiration) value).getExpiresInSeconds() <= 0)
        {
            return false;
        }
        return true;
    }

    @Override public void invalidate(@NotNull DTOKeyType key)
    {
        cachedValues.remove(key);
        BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject = cachedSubjects.remove(key);
        if (cachedSubject != null)
        {
            cachedSubject.onCompleted();
        }
    }

    @Override public void invalidateAll()
    {
        cachedValues.evictAll();
        Collection<BehaviorSubject<Pair<DTOKeyType, DTOType>>> subjects = cachedSubjects.values();
        cachedSubjects.clear();
        cachedObservables.clear();
        for (BehaviorSubject<Pair<DTOKeyType, DTOType>> subject : subjects)
        {
            subject.onCompleted();
        }
    }

    /**
     * Removes the observable and cached subject when the counter is 0
     * @param key
     * @param counter
     * @param cachedSubject
     */
    private void removeConditional(
            @NotNull DTOKeyType key,
            @NotNull RefCounter counter,
            @NotNull BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject)
    {
        if (counter.get() == 0 && cachedSubjects.get(key) == cachedSubject)
        {
            cachedObservables.remove(key);
            cachedSubjects.remove(key);
        }
    }

    @NotNull protected Map<DTOKeyType, DTOType> snapshot()
    {
        return cachedValues.snapshot();
    }

    private static class RefCounter
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
