package com.tradehero.common.persistence;

import android.support.annotation.Nullable;
import android.util.Pair;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BaseDTOCacheRx<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements DTOCacheRx<DTOKeyType, DTOType>
{
    @NotNull final private THLruCache<DTOKeyType, DTOType> cachedValues;
    @NotNull final private THLruCache<DTOKeyType, BehaviorSubject<Pair<DTOKeyType, DTOType>>> cachedSubjects;

    //<editor-fold desc="Constructors">
    protected BaseDTOCacheRx(int valueSize, int subjectSize)
    {
        this.cachedValues = new THLruCache<>(valueSize);
        this.cachedSubjects = new THLruCache<>(subjectSize);
    }
    //</editor-fold>

    @NotNull @Override
    public Observable<Pair<DTOKeyType, DTOType>> get(@NotNull final DTOKeyType key)
    {
        return getOrCreateBehavior(key);
    }

    @NotNull protected BehaviorSubject<Pair<DTOKeyType, DTOType>> getOrCreateBehavior(@NotNull final DTOKeyType key)
    {
        BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject = cachedSubjects.get(key);
        if (cachedSubject == null)
        {
            cachedSubject = BehaviorSubject.create();
            cachedSubjects.put(key, cachedSubject);
        }

        DTOType cachedValue = getValue(key);
        if (cachedValue != null)
        {
            cachedSubject.onNext(Pair.create(key, cachedValue));
        }

        return cachedSubject;
    }

    @Override public void onNext(DTOKeyType key, DTOType value)
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

    @Nullable protected DTOType getValue(@NotNull DTOKeyType key)
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
        cachedSubjects.remove(key);
        cachedValues.remove(key);
    }

    @Override public void invalidateAll()
    {
        cachedSubjects.evictAll();
        cachedValues.evictAll();
    }
}
