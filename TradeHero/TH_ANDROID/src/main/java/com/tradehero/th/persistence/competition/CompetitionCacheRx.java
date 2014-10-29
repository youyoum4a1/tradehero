package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class CompetitionCacheRx extends BaseFetchDTOCacheRx<CompetitionId, CompetitionDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NotNull private final CompetitionServiceWrapper competitionServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionCacheRx(@NotNull CompetitionServiceWrapper competitionServiceWrapper)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE);
        this.competitionServiceWrapper = competitionServiceWrapper;
    }
    //</editor-fold>

    @Override @NotNull public Observable<CompetitionDTO> fetch(@NotNull CompetitionId key)
    {
        return competitionServiceWrapper.getCompetitionRx(key);
    }

    public void onNext(@NotNull List<? extends CompetitionDTO> values)
    {
        for (@NotNull CompetitionDTO competitionDTO: values)
        {
            onNext(competitionDTO.getCompetitionId(), competitionDTO);
        }
    }
}
