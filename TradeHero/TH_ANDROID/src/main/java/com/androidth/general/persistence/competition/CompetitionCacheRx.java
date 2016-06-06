package com.androidth.general.persistence.competition;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.competition.CompetitionDTO;
import com.androidth.general.api.competition.key.CompetitionId;
import com.androidth.general.network.service.CompetitionServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class CompetitionCacheRx extends BaseFetchDTOCacheRx<CompetitionId, CompetitionDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    @NonNull private final CompetitionServiceWrapper competitionServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionCacheRx(
            @NonNull CompetitionServiceWrapper competitionServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtilRx);
        this.competitionServiceWrapper = competitionServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull public Observable<CompetitionDTO> fetch(@NonNull CompetitionId key)
    {
        return competitionServiceWrapper.getCompetitionRx(key);
    }

    public void onNext(@NonNull List<? extends CompetitionDTO> values)
    {
        for (CompetitionDTO competitionDTO: values)
        {
            onNext(competitionDTO.getCompetitionId(), competitionDTO);
        }
    }
}
