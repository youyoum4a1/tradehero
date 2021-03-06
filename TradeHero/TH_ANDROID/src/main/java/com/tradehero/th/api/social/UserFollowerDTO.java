package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.persistence.social.HeroType;
import java.util.Date;
import java.util.List;

public class UserFollowerDTO extends UserProfileCompactDTO implements DTO
{
    public List<FollowerTransactionDTO> followerTransactions;
    public double totalRevenue;
    public boolean isFreeFollow;
    public Date followingSince;

    // This one does not appear to be in TH_SVR
    public double roiSinceInception;

    public UserFollowerDTO()
    {
        super();
    }

    public HeroType getHeroType()
    {
        return isFreeFollow ? HeroType.FREE : HeroType.PREMIUM;
    }
}
