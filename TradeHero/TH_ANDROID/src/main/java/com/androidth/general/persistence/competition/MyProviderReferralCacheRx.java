package com.androidth.general.persistence.competition;

import android.support.annotation.NonNull;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.referral.MyProviderReferralDTO;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.network.service.LiveServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class MyProviderReferralCacheRx extends BaseFetchDTOCacheRx<ProviderId, MyProviderReferralDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    @NonNull private final LiveServiceWrapper liveServiceWrapper;

    @Inject public MyProviderReferralCacheRx(
            @NonNull LiveServiceWrapper liveServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.liveServiceWrapper = liveServiceWrapper;
    }

    @Override @NonNull public Observable<MyProviderReferralDTO> fetch(@NonNull ProviderId key)
    {
        return liveServiceWrapper.getMyProviderReferralStatusRx(key);
    }

}
