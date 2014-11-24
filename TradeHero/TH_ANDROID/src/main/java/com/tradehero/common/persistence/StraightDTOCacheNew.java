package com.tradehero.common.persistence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Map;

abstract public class StraightDTOCacheNew<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends PartialDTOCacheNew<DTOKeyType, DTOType>
{
    @NonNull final private THLruCache<DTOKeyType, CacheValue<DTOKeyType, DTOType>> lruCache;

    //<editor-fold desc="Constructors">
    public StraightDTOCacheNew(int maxSize, @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(dtoCacheUtil);
        this.lruCache = new THLruCache<>(maxSize);
    }
    //</editor-fold>

    @Override @Nullable protected CacheValue<DTOKeyType, DTOType> getCacheValue(@NonNull DTOKeyType key)
    {
        return lruCache.get(key);
    }

    @Override protected void putCacheValue(@NonNull DTOKeyType key, @NonNull CacheValue<DTOKeyType, DTOType> cacheValue)
    {
        lruCache.put(key, cacheValue);
    }

    @Override public void invalidate(@NonNull DTOKeyType key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    @Override public void unregister(@Nullable Listener<DTOKeyType, DTOType> callback)
    {
        if (callback != null)
        {
            for (CacheValue<DTOKeyType, DTOType> value : lruCache.snapshot().values())
            {
                value.unregisterListener(callback);
            }
        }
    }

    @NonNull protected Map<DTOKeyType, CacheValue<DTOKeyType, DTOType>> snapshot()
    {
        return lruCache.snapshot();
    }
}
