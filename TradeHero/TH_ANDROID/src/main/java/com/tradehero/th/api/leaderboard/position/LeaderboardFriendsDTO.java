package com.tradehero.th.api.leaderboard.position;

import com.tradehero.common.persistence.BaseHasExpiration;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.social.UserFriendsDTOList;

public class LeaderboardFriendsDTO extends BaseHasExpiration
        implements DTO
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 300;

    public LeaderboardDTO leaderboard;
    public UserFriendsDTOList socialFriends;

    //<editor-fold desc="Constructors">
    public LeaderboardFriendsDTO()
    {
        super(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }
    //</editor-fold>
}
