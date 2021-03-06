package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import rx.Observable;

@Singleton @UserCache
public class UserProfileCompactCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, UserProfileCompactDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NonNull private final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileCompactCacheRx(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<UserProfileCompactDTO> fetch(@NonNull UserBaseKey key)
    {
        return userProfileCache.get(key)
                .map(value -> value.second);
    }
}
