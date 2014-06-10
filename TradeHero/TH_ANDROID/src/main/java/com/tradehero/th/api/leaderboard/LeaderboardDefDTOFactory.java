package com.tradehero.th.api.leaderboard;

import android.content.Context;
import com.tradehero.th.R;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class LeaderboardDefDTOFactory
{
    @NotNull private final Context context;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefDTOFactory(@NotNull Context context)
    {
        this.context = context;
    }
    //</editor-fold>

    public void complementServerLeaderboardDefDTOs(@NotNull LeaderboardDefDTOList leaderboardDefDTOs)
    {

    }

    @NotNull public LeaderboardDefDTO createHeroLeaderboardDefDTO()
    {
        LeaderboardDefDTO heroLeaderboardDefDTO = new LeaderboardDefDTO();
        heroLeaderboardDefDTO.id = LeaderboardDefDTO.LEADERBOARD_HERO_ID;
        heroLeaderboardDefDTO.name = context.getString(R.string.leaderboard_community_heros);
        return heroLeaderboardDefDTO;
    }

    @NotNull public LeaderboardDefDTO createFollowerLeaderboardDefDTO()
    {
        LeaderboardDefDTO followerLeaderboardDefDTO = new LeaderboardDefDTO();
        followerLeaderboardDefDTO.id = LeaderboardDefDTO.LEADERBOARD_FOLLOWER_ID;
        followerLeaderboardDefDTO.name = context.getString(R.string.leaderboard_community_followers);
        return followerLeaderboardDefDTO;
    }

    @NotNull public LeaderboardDefDTO createFriendLeaderboardDefDTO()
    {
        LeaderboardDefDTO friendLeaderboardDTO = new LeaderboardDefDTO();
        friendLeaderboardDTO.id = LeaderboardDefDTO.LEADERBOARD_FRIEND_ID;
        friendLeaderboardDTO.name = context.getString(R.string.leaderboard_community_friends);
        return friendLeaderboardDTO;
    }

    @NotNull public LeaderboardDefDTO createExchangeLeaderboardDefDTO()
    {
        LeaderboardDefDTO exchangeLeaderboardDefDTO = new LeaderboardDefDTO();
        exchangeLeaderboardDefDTO.id = LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID;
        exchangeLeaderboardDefDTO.name = context.getString(R.string.leaderboard_community_by_exchange);
        return exchangeLeaderboardDefDTO;
    }

    @NotNull public LeaderboardDefDTO createSectorLeaderboardDefDTO()
    {
        LeaderboardDefDTO fakeSectorDto = new LeaderboardDefDTO();
        fakeSectorDto.id = LeaderboardDefDTO.LEADERBOARD_DEF_SECTOR_ID;
        fakeSectorDto.name = context.getString(R.string.leaderboard_community_by_sector);
        return fakeSectorDto;
    }
}
