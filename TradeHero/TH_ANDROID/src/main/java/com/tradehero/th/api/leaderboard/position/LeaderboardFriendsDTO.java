package com.tradehero.th.api.leaderboard.position;

import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.HasExpiration;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.social.UserFriendsDTOList;
import java.util.Calendar;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

public class LeaderboardFriendsDTO implements DTO, HasExpiration
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 300;

    public LeaderboardDTO leaderboard;
    public UserFriendsDTOList socialFriends;

    @NotNull public Date expirationDate;

    public LeaderboardFriendsDTO()
    {
        super();
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }

    protected void setExpirationDateSecondsInFuture(int seconds)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, seconds);
        this.expirationDate = calendar.getTime();
    }

    @Override public long getExpiresInSeconds()
    {
        return Math.max(
                0,
                expirationDate.getTime() - Calendar.getInstance().getTime().getTime());
    }
}
