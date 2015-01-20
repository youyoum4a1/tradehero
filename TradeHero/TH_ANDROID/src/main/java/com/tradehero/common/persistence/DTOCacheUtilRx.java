package com.tradehero.common.persistence;

import android.support.annotation.NonNull;

public interface DTOCacheUtilRx
{
    void addCache(@NonNull DTOCacheRx dtoCacheRx);
    @SuppressWarnings("UnusedDeclaration") void clearAllCaches();
    @SuppressWarnings("UnusedDeclaration") void clearSystemCaches();
    void clearUserCaches();
}
