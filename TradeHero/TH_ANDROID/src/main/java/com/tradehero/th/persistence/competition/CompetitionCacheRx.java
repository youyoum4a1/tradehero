package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import rx.Observable;

@Singleton @UserCache
public class CompetitionCacheRx extends BaseFetchDTOCacheRx<CompetitionId, CompetitionDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NonNull private final CompetitionServiceWrapper competitionServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionCacheRx(
            @NonNull CompetitionServiceWrapper competitionServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtilRx);
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
