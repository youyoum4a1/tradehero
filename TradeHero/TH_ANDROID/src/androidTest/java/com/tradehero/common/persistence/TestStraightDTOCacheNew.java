package com.tradehero.common.persistence;

import android.support.annotation.NonNull;

abstract public class TestStraightDTOCacheNew<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends StraightDTOCacheNew<DTOKeyType, DTOType>
{
    //<editor-fold desc="Constructors">
    public TestStraightDTOCacheNew(int maxSize, DTOCacheUtilNew dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>

    public boolean isCacheValueNull(@NonNull DTOKeyType key)
    {
        return getCacheValue(key) == null;
    }

    public Integer getListenersCount(@NonNull DTOKeyType key)
    {
        CacheValue<DTOKeyType, DTOType> cacheValue = getCacheValue(key);
        if (cacheValue == null)
        {
            return null;
        }
        return cacheValue.getListenersCount();
    }
}
