package com.tradehero.common.persistence;

import android.support.annotation.NonNull;

public interface DTOCacheUtilRx
{
    void addCache(@NonNull DTOCacheRx dtoCacheRx);
    void clearAllCaches();
    void clearSystemCaches();
    void clearUserCaches();
}
