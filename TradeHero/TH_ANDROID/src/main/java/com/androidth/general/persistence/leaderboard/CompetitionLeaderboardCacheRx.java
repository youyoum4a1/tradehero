package com.androidth.general.persistence.leaderboard;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.androidth.general.api.leaderboard.competition.CompetitionLeaderboardId;
import com.androidth.general.network.service.CompetitionServiceWrapper;
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
