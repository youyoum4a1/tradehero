package com.tradehero.common.persistence;

import android.support.annotation.NonNull;

public interface DTOCacheUtilNew
{
    void addCache(@NonNull DTOCacheNew dtoCacheNew);
    void clearAllCaches();
    void clearSystemCaches();
    void clearUserCaches();
}
