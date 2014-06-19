package com.tradehero.common.persistence;

import java.util.Map;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class StraightDTOCacheNew<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends PartialDTOCacheNew<DTOKeyType, DTOType>
{
    final private THLruCache<DTOKeyType, CacheValue<DTOKeyType, DTOType>> lruCache;

    //<editor-fold desc="Constructors">
    public StraightDTOCacheNew(int maxSize)
    {
        super();
        this.lruCache = new THLruCache<>(maxSize);
    }
    //</editor-fold>

    @Contract("null -> null; !null -> _")
    @Nullable
    @Override public DTOType get(@NotNull DTOKeyType key)
    {
        CacheValue<DTOKeyType, DTOType> cacheValue = getCacheValue(key);
        if (cacheValue == null)
        {
            return null;
        }
        DTOType value = cacheValue.getValue();
        if (value instanceof HasExpiration && ((HasExpiration) value).getExpiresInSeconds() <= 0)
        {
            return null;
        }
        return value;
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
