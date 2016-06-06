package com.androidth.general.persistence.social;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.social.FollowerSummaryDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.network.service.FollowerServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class FollowerSummaryCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, FollowerSummaryDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;

    @NonNull protected final FollowerServiceWrapper followerServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public FollowerSummaryCacheRx(
            @NonNull FollowerServiceWrapper followerServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.followerServiceWrapper = followerServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<FollowerSummaryDTO> fetch(@NonNull UserBaseKey key)
    {
        return followerServiceWrapper.getAllFollowersSummaryRx(key);
    }
}
