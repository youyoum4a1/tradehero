package com.tradehero.th.persistence.user;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.DisplayNameDTO;
import com.tradehero.th.api.users.UserAvailabilityDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class UserAvailabilityCacheRx extends BaseFetchDTOCacheRx<DisplayNameDTO, UserAvailabilityDTO>
{
    public static final int DEFAULT_MAX_SIZE = 20;

    @NonNull private final UserServiceWrapper userServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public UserAvailabilityCacheRx(
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<UserAvailabilityDTO> fetch(@NonNull DisplayNameDTO key)
    {
        return userServiceWrapper.checkDisplayNameAvailableRx(key.displayName);
    }
}
