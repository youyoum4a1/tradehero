package com.tradehero.th.persistence.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.StocksLeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserIdList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class LeaderboardUserCacheRx extends BaseDTOCacheRx<LeaderboardUserId, StocksLeaderboardUserDTO>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    private static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardUserCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NonNull Map<LeaderboardUserId, StocksLeaderboardUserDTO> leaderboardUserDTOs)
    {
        for (Map.Entry<LeaderboardUserId, StocksLeaderboardUserDTO> pair: leaderboardUserDTOs.entrySet())
        {
            onNext(pair.getKey(), pair.getValue());
        }
    }

    public void put(@NonNull List<? extends StocksLeaderboardUserDTO> leaderboardUserDTOs)
    {
        for (StocksLeaderboardUserDTO stocksLeaderboardUserDTO : leaderboardUserDTOs)
        {
            onNext(stocksLeaderboardUserDTO.getLeaderboardUserId(), stocksLeaderboardUserDTO);
        }
    }

    public LeaderboardUserIdList getAllKeys()
    {
        return new LeaderboardUserIdList(snapshot().keySet(), (LeaderboardUserId) null);
    }
}
