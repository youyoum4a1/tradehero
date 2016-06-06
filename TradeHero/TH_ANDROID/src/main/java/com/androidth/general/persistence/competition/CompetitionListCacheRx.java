package com.androidth.general.persistence.competition;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.competition.CompetitionDTOList;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.network.service.CompetitionServiceWrapper;
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
