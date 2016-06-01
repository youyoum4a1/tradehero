package com.ayondo.academy.persistence.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.competition.CompetitionDTOList;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.network.service.CompetitionServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class CompetitionListCacheRx extends BaseFetchDTOCacheRx<ProviderId, CompetitionDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final CompetitionServiceWrapper competitionServiceWrapper;
    @NonNull private final CompetitionCacheRx competitionCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionListCacheRx(
            @NonNull CompetitionServiceWrapper competitionServiceWrapper,
            @NonNull CompetitionCacheRx competitionCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.competitionServiceWrapper = competitionServiceWrapper;
        this.competitionCache = competitionCache;
    }
    //</editor-fold>

    @Override @NonNull public Observable<CompetitionDTOList> fetch(@NonNull ProviderId key)
    {
        return competitionServiceWrapper.getCompetitionsRx(key);
    }

    @Override public void onNext(@NonNull ProviderId key, @NonNull CompetitionDTOList value)
    {
        super.onNext(key, value);
        competitionCache.onNext(value);
    }
}
