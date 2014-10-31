package com.tradehero.common.persistence;

import org.jetbrains.annotations.NotNull;

public interface DTOCacheUtilRx
{
    void addCache(@NotNull DTOCacheRx dtoCacheRx);
    void clearAllCaches();
    void clearSystemCaches();
    void clearUserCaches();
}
