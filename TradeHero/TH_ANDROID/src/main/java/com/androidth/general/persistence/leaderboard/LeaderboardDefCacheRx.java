package com.androidth.general.persistence.leaderboard;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.leaderboard.def.LeaderboardDefDTO;
import com.androidth.general.api.leaderboard.def.LeaderboardDefDTOList;
import com.androidth.general.api.leaderboard.key.LeaderboardDefKey;
import com.androidth.general.network.service.LeaderboardServiceWrapper;
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
