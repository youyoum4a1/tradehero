package com.tradehero.common.persistence;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;

public class BaseDTOCache<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements DTOCache<DTOKeyType, DTOType>
{
    @NotNull final private THLruCache<DTOKeyType, DTOType> lruCache;

    //<editor-fold desc="Constructors">
    protected BaseDTOCache(int maxSize)
    {
        this.lruCache = new THLruCache<>(maxSize);
    }
    //</editor-fold>

    @Override @Nullable public DTOType put(@NotNull DTOKeyType key, @NotNull DTOType value)
    {
        @Nullable DTOType previous = null;
        if (isValid(value))
        {
            previous = lruCache.put(key, value);
        }
        return previous;
    }

    @Override @Nullable public DTOType get(@NotNull DTOKeyType key)
    {
        @Nullable DTOType value = lruCache.get(key);
        if (value == null)
        {
            return null;
        }
        if (!isValid(value))
        {
            invalidate(key);
            return null;
        }
        return value;
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

    @NotNull @Override public Observable<DTOType> createObservable(@NotNull DTOKeyType key)
    {
        return Observable.just(get(key));
    }

    @Override public void invalidate(@NotNull DTOKeyType key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }
}
