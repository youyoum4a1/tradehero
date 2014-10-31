package com.tradehero.common.persistence;

import android.support.annotation.Nullable;
import android.util.Pair;
import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BaseDTOCacheRx<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements DTOCacheRx<DTOKeyType, DTOType>
{
    @NotNull final private THLruCache<DTOKeyType, DTOType> cachedValues;
    @NotNull final private THLruCache<DTOKeyType, BehaviorSubject<Pair<DTOKeyType, DTOType>>> cachedSubjects;

    //<editor-fold desc="Constructors">
    protected BaseDTOCacheRx(int valueSize, int subjectSize,
            @NotNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        this.cachedValues = new THLruCache<>(valueSize);
        this.cachedSubjects = new THLruCache<>(subjectSize);
        dtoCacheUtilRx.addCache(this);
    }
    //</editor-fold>

    @NotNull @Override
    public Observable<Pair<DTOKeyType, DTOType>> get(@NotNull final DTOKeyType key)
    {
        return getOrCreateBehavior(key).asObservable();
    }

    @NotNull protected BehaviorSubject<Pair<DTOKeyType, DTOType>> getOrCreateBehavior(@NotNull final DTOKeyType key)
    {
        BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject = cachedSubjects.get(key);
        if (cachedSubject == null)
        {
            DTOType cachedValue = getValue(key);
            if (cachedValue != null)
            {
                cachedSubject = BehaviorSubject.create(Pair.create(key, cachedValue));
            }
            else
            {
                cachedSubject = BehaviorSubject.create();
            }
            cachedSubjects.put(key, cachedSubject);
        }

        return cachedSubject;
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

    @Override public void onError(@NotNull DTOKeyType key, @NotNull Throwable error)
    {
        BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject = cachedSubjects.remove(key);
        if (cachedSubject != null)
        {
            cachedSubject.onError(error);
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
        Collection<BehaviorSubject<Pair<DTOKeyType, DTOType>>> subjects = cachedSubjects.snapshot().values();
        cachedSubjects.evictAll();
        for (BehaviorSubject<Pair<DTOKeyType, DTOType>> subject : subjects)
        {
            subject.onCompleted();
        }
    }

    @NotNull protected Map<DTOKeyType, DTOType> snapshot()
    {
        return cachedValues.snapshot();
    }
}
