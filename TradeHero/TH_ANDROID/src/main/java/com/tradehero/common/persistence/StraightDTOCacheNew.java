package com.tradehero.common.persistence;

import java.util.Map;

abstract public class StraightDTOCacheNew<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends PartialDTOCacheNew<DTOKeyType, DTOType>
{
    private THLruCache<DTOKeyType, CacheValue<DTOKeyType, DTOType>> lruCache;

    public StraightDTOCacheNew(int maxSize)
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
        return this.lruCache.get(key).getValue();
    }

    @Override protected CacheValue<DTOKeyType, DTOType> getCacheValue(DTOKeyType key)
    {
        return lruCache.get(key);
    }

    @Override protected void putCacheValue(DTOKeyType key, CacheValue<DTOKeyType, DTOType> cacheValue)
    {
        lruCache.put(key, cacheValue);
    }

    @Override public void invalidate(DTOKeyType key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    @Override public void unregister(Listener<DTOKeyType, DTOType> callback)
    {
        for (CacheValue<DTOKeyType, DTOType> value : lruCache.snapshot().values())
        {
            if (value != null)
            {
                value.unregisterListener(callback);
            }
        }
    }

    protected Map<DTOKeyType, CacheValue<DTOKeyType, DTOType>> snapshot()
    {
        return lruCache.snapshot();
    }
}
