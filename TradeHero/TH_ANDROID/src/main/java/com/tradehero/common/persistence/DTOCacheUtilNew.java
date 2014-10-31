package com.tradehero.common.persistence;

import org.jetbrains.annotations.NotNull;

public interface DTOCacheUtilNew
{
    void addCache(@NotNull DTOCacheNew dtoCacheNew);
    void clearAllCaches();
    void clearSystemCaches();
    void clearUserCaches();
}
