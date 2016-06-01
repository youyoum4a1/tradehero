package com.ayondo.academy.persistence.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTO;
import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTOList;
import com.ayondo.academy.api.leaderboard.key.LeaderboardDefKey;
import com.ayondo.academy.network.service.LeaderboardServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class LeaderboardDefCacheRx extends BaseFetchDTOCacheRx<LeaderboardDefKey, LeaderboardDefDTO>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @NonNull private final LeaderboardServiceWrapper leaderboardServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefCacheRx(
            @NonNull LeaderboardServiceWrapper leaderboardServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<LeaderboardDefDTO> fetch(@NonNull final LeaderboardDefKey key)
    {
        return leaderboardServiceWrapper.getLeaderboardDef(key);
    }

    public void onNext(@NonNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        for (LeaderboardDefDTO leaderboardDefDTO: leaderboardDefDTOs)
        {
            onNext(leaderboardDefDTO.getLeaderboardDefKey(), leaderboardDefDTO);
        }
    }
}
