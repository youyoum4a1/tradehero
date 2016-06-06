package com.androidth.general.api.social;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.users.UserProfileCompactDTO;
import com.androidth.general.persistence.social.HeroType;
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
