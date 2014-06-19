package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.users.DisplayNameDTO;
import com.tradehero.th.api.users.UserAvailabilityDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class UserAvailabilityCache extends StraightDTOCacheNew<DisplayNameDTO, UserAvailabilityDTO>
{
    public static final int DEFAULT_MAX_SIZE = 20;

    private final UserServiceWrapper userServiceWrapper;

    @Inject public UserAvailabilityCache(UserServiceWrapper userServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.userServiceWrapper = userServiceWrapper;
    }

    @Override public UserAvailabilityDTO fetch(@NotNull DisplayNameDTO key) throws Throwable
    {
        return userServiceWrapper.checkDisplayNameAvailable(key.displayName);
    }

    public static interface UserAvailabilityListener
            extends DTOCacheNew.Listener<DisplayNameDTO, UserAvailabilityDTO>
    {
    }
}
