package com.ayondo.academy.persistence.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.competition.CompetitionPreSeasonDTO;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.network.service.ProviderServiceWrapper;
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
