package com.tradehero.common.persistence;

import android.util.Pair;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

abstract public class BaseDTOCacheRx<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements DTOCacheRx<DTOKeyType, DTOType>
{
    @NotNull final private THLruCache<DTOKeyType, DTOType> cachedValues;
    @NotNull final private THLruCache<DTOKeyType, BehaviorSubject<Pair<DTOKeyType, DTOType>>> cachedSubjects;

    //<editor-fold desc="Constructors">
    protected BaseDTOCacheRx(int maxSize)
    {
        this.cachedValues = new THLruCache<>(maxSize);
        this.cachedSubjects = new THLruCache<>(maxSize);
    }
    //</editor-fold>

    @NotNull abstract protected Observable<DTOType> fetch(@NotNull DTOKeyType key);

    @NotNull @Override
    public Observable<Pair<DTOKeyType, DTOType>> get(@NotNull DTOKeyType key)
    {
        return getOrCreateBehavior(key);
    }

    @NotNull protected Observable<Pair<DTOKeyType, DTOType>> getOrCreateBehavior(@NotNull final DTOKeyType key)
    {
        BehaviorSubject<Pair<DTOKeyType, DTOType>> cachedSubject = cachedSubjects.get(key);
        if (cachedSubject == null)
        {
            cachedSubject = BehaviorSubject.create();
            cachedSubjects.put(key, cachedSubject);
            cachedSubject.subscribe(new Observer<Pair<DTOKeyType, DTOType>>()
            {
                @Override public void onNext(Pair<DTOKeyType, DTOType> dtoKeyTypeDTOTypePair)
                {
                    cachedValues.put(key, dtoKeyTypeDTOTypePair.second);
                }

                @Override public void onCompleted()
                {
                    cachedSubjects.remove(key);
                }

                @Override public void onError(Throwable e)
                {
                    cachedSubjects.remove(key);
                }
            });
        }

        DTOType cachedValue = cachedValues.get(key);
        if (cachedValue != null && isValid(cachedValue))
        {
            cachedSubject.onNext(Pair.create(key, cachedValue));
        }

        fetch(key)
                .map(new Func1<DTOType, Pair<DTOKeyType, DTOType>>()
                {
                    @Override public Pair<DTOKeyType, DTOType> call(DTOType dtoType)
                    {
                        return Pair.create(key, dtoType);
                    }
                })
                .subscribe(cachedSubject);
        return cachedSubject;
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
