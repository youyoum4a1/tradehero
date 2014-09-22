package com.tradehero.common.persistence;

import org.jetbrains.annotations.NotNull;

abstract public class TestStraightDTOCacheNew<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends StraightDTOCacheNew<DTOKeyType, DTOType>
{
    //<editor-fold desc="Constructors">
    public TestStraightDTOCacheNew(int maxSize)
    {
        super(maxSize);
    }
    //</editor-fold>

    public boolean isCacheValueNull(@NotNull DTOKeyType key)
    {
        return getCacheValue(key) == null;
    }

    public Integer getListenersCount(@NotNull DTOKeyType key)
    {
        CacheValue<DTOKeyType, DTOType> cacheValue = getCacheValue(key);
        if (cacheValue == null)
        {
            return null;
        }
        return cacheValue.getListenersCount();
    }
}
