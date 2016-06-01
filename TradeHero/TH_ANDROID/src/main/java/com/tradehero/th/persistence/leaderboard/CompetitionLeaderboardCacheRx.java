package com.ayondo.academy.persistence.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.ayondo.academy.api.leaderboard.competition.CompetitionLeaderboardId;
import com.ayondo.academy.network.service.CompetitionServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class CompetitionLeaderboardCacheRx extends BaseFetchDTOCacheRx<CompetitionLeaderboardId, CompetitionLeaderboardDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    @NonNull private final CompetitionServiceWrapper competitionServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionLeaderboardCacheRx(
            @NonNull CompetitionServiceWrapper competitionServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.competitionServiceWrapper = competitionServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull public Observable<CompetitionLeaderboardDTO> fetch(@NonNull CompetitionLeaderboardId key)
    {
        return competitionServiceWrapper.getCompetitionLeaderboardRx(key);
    }
}
