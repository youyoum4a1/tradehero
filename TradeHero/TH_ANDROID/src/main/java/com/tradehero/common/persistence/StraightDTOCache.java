package com.tradehero.common.persistence;

import java.util.Map;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

abstract public class StraightDTOCache<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends PartialDTOCache<DTOKeyType, DTOType>
{
    private THLruCache<DTOKeyType, DTOType> lruCache;

    public StraightDTOCache(int maxSize)
    {
        super();
        this.lruCache = new THLruCache<>(maxSize);
    }

    @Contract("null -> null")
    @Nullable
    @Override public DTOType get(@Nullable DTOKeyType key)
    {
        if (key == null)
        {
            return null;
        }
        return this.lruCache.get(key);
    }

    @Nullable
    @Override public DTOType put(DTOKeyType key, DTOType value)
    {
        return this.lruCache.put(key, value);
    }

    @Override public void invalidate(DTOKeyType key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    protected Map<DTOKeyType, DTOType> snapshot()
    {
        return lruCache.snapshot();
    }
}
