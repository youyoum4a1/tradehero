package com.ayondo.academy.api.social;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.users.UserProfileCompactDTO;
import com.ayondo.academy.persistence.social.HeroType;
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

    @NonNull public HeroType getHeroType()
    {
        return isFreeFollow ? HeroType.FREE : HeroType.PREMIUM;
    }
}
