package com.tradehero.common.persistence;

import java.util.Map;

abstract public class StraightDTOCache<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends PartialDTOCache<DTOKeyType, DTOType>
{
    private THLruCache<DTOKeyType, DTOType> lruCache;

    public StraightDTOCache(int maxSize)
    {
        super();
        this.lruCache = new THLruCache<>(maxSize);
    }

    @Override public DTOType get(DTOKeyType key)
    {
        if (key == null)
        {
            return null;
        }
        return this.lruCache.get(key);
    }

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
