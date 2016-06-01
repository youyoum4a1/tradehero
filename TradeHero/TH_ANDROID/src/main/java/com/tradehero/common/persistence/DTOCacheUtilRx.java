package com.tradehero.common.persistence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.users.UserProfileDTO;

public interface DTOCacheUtilRx
{
    void addCache(@NonNull DTOCacheRx dtoCacheRx);
    void clearUserCaches();
    void clearSystemCaches();
    void anonymousPrefetches();
    void initialPrefetches();
    void prefetchesUponLogin(@Nullable final UserProfileDTO profile);
}
