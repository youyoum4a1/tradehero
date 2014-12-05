package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import java.io.Serializable;
import java.util.Date;
import org.jetbrains.annotations.Nullable;

public class UserSearchResultDTO implements DTO,Serializable
{
    public String userFirstName;
    public String userLastName;
    public String userthDisplayName;
    public Integer userId;
    public String userPicture;

    public Double userCashBalanceRefCcy;
    @Nullable public Date userMarkingAsOfUtc;
    public Double userRoiSinceInception;
    public Double userPlSinceInceptionRefCcy;

    public UserBaseKey getUserBaseKey()
    {
        return new UserBaseKey(userId);
    }

    public UserSearchResultDTO(LeaderboardUserDTO dto)
    {
        this.userId = dto.id;
        this.userthDisplayName = dto.displayName;
        this.userPicture = dto.picture;
        this.userRoiSinceInception = dto.perfRoi;
    }
}
