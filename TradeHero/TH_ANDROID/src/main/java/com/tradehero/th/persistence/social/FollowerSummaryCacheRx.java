package com.tradehero.th.persistence.social;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.FollowerServiceWrapper;
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
