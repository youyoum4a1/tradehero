package com.androidth.general.persistence.user;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileCompactDTO;
import com.androidth.general.api.users.UserProfileDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class UserProfileCompactCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, UserProfileCompactDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    @NonNull private final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileCompactCacheRx(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<UserProfileCompactDTO> fetch(@NonNull UserBaseKey key)
    {
        return userProfileCache.get(key)
                .map(new PairGetSecond<UserBaseKey, UserProfileDTO>())
                .cast(UserProfileCompactDTO.class);
    }
}
