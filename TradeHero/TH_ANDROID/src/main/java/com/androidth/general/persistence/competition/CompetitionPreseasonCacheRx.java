package com.androidth.general.persistence.competition;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.competition.CompetitionPreSeasonDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.network.service.ProviderServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class CompetitionPreseasonCacheRx extends BaseFetchDTOCacheRx<ProviderId, CompetitionPreSeasonDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    private ProviderServiceWrapper providerServiceWrapper;

    @Inject CompetitionPreseasonCacheRx(@NonNull ProviderServiceWrapper providerServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtilRx);
        this.providerServiceWrapper = providerServiceWrapper;
    }

    @NonNull @Override protected Observable<CompetitionPreSeasonDTO> fetch(@NonNull ProviderId key)
    {
        return providerServiceWrapper.getPreseasonDetails(key);
    }
}
