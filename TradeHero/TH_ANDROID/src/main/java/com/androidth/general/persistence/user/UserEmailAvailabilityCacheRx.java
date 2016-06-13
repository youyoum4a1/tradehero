package com.androidth.general.persistence.user;

import android.support.annotation.NonNull;

import com.androidth.general.api.users.EmailDTO;
import com.androidth.general.api.users.UserAvailabilityDTO;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.network.service.UserServiceWrapper;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by ayushnvijay on 6/13/16.
 */
@Singleton
@UserCache
public class UserEmailAvailabilityCacheRx extends BaseFetchDTOCacheRx<EmailDTO, UserAvailabilityDTO> {

    public static final int DEFAULT_MAX_SIZE = 20;
    @NonNull private final UserServiceWrapper userServiceWrapper;

    @Inject protected UserEmailAvailabilityCacheRx(@NonNull UserServiceWrapper userServiceWrapper, @NonNull DTOCacheUtilRx dtoCacheUtil) {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
    }

    @NonNull
    @Override
    protected Observable<UserAvailabilityDTO> fetch(@NonNull EmailDTO key) {
        return userServiceWrapper.checkEmailAvailableRx(key.email);
    }
}
