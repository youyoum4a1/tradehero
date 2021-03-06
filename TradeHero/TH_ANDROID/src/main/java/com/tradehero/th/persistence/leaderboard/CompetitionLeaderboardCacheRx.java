package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import rx.Observable;

@Singleton @UserCache
public class CompetitionLeaderboardCacheRx extends BaseFetchDTOCacheRx<CompetitionLeaderboardId, CompetitionLeaderboardDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NonNull private final CompetitionServiceWrapper competitionServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionLeaderboardCacheRx(
            @NonNull CompetitionServiceWrapper competitionServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.competitionServiceWrapper = competitionServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull public Observable<CompetitionLeaderboardDTO> fetch(@NonNull CompetitionLeaderboardId key)
    {
        return competitionServiceWrapper.getCompetitionLeaderboardRx(key);
    }
}
