package com.androidth.general.api.leaderboard.position;

import com.androidth.general.common.persistence.BaseHasExpiration;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.leaderboard.LeaderboardDTO;
import com.androidth.general.api.social.UserFriendsDTOList;

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
