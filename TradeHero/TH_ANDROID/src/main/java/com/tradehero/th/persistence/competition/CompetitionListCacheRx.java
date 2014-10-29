package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class CompetitionListCacheRx extends BaseFetchDTOCacheRx<ProviderId, CompetitionDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

    @NotNull private final CompetitionServiceWrapper competitionServiceWrapper;
    @NotNull private final CompetitionCacheRx competitionCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionListCacheRx(
            @NotNull CompetitionServiceWrapper competitionServiceWrapper,
            @NotNull CompetitionCacheRx competitionCache)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE);
        this.competitionServiceWrapper = competitionServiceWrapper;
        this.competitionCache = competitionCache;
    }
    //</editor-fold>

    @Override @NotNull public Observable<CompetitionDTOList> fetch(@NotNull ProviderId key)
    {
        return competitionServiceWrapper.getCompetitionsRx(key);
    }

    @Override public void onNext(@NotNull ProviderId key, @NotNull CompetitionDTOList value)
    {
        super.onNext(key, value);
        competitionCache.onNext(value);
    }
}
