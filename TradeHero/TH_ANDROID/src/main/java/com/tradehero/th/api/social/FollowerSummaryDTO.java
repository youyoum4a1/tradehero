package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.List;

public class FollowerSummaryDTO implements DTO
{
    public List<UserFollowerDTO> userFollowers;
    public double totalRevenue;
    public HeroPayoutSummaryDTO payoutSummary;

    public FollowerSummaryDTO()
    {
        super();
    }

    public int getPaidFollowerCount()
    {
        if (userFollowers == null)
        {
            return 0;
        }
        int count = 0;
        for (UserFollowerDTO userFollowerDTO : userFollowers)
        {
            if (!userFollowerDTO.isFreeFollow)
            {
                count++;
            }
        }
        return count;
    }

    public FollowerSummaryDTO getPaidFollowerSummaryDTO()
    {
        if (userFollowers == null)
        {
            return null;
        }
        FollowerSummaryDTO followerSummaryDTO =  new FollowerSummaryDTO();
        followerSummaryDTO.userFollowers = new ArrayList<>();

        for (UserFollowerDTO userFollowerDTO : userFollowers)
        {
            if (!userFollowerDTO.isFreeFollow)
            {
                followerSummaryDTO.userFollowers.add(userFollowerDTO);
            }
        }
        return followerSummaryDTO;
    }

    public int getFreeFollowerCount()
    {
        if (userFollowers == null)
        {
            return 0;
        }
        int count = 0;
        for (UserFollowerDTO userFollowerDTO : userFollowers)
        {
            if (userFollowerDTO.isFreeFollow)
            {
                count++;
            }
        }
        return count;
    }

    public FollowerSummaryDTO getFreeFollowerSummaryDTO()
    {
        if (userFollowers == null)
        {
            return null;
        }
        FollowerSummaryDTO followerSummaryDTO =  new FollowerSummaryDTO();
        followerSummaryDTO.userFollowers = new ArrayList<>();

        for (UserFollowerDTO userFollowerDTO : userFollowers)
        {
            if (userFollowerDTO.isFreeFollow)
            {
                followerSummaryDTO.userFollowers.add(userFollowerDTO);
            }
        }
        return followerSummaryDTO;
    }



    @Override public String toString()
    {
        if (userFollowers != null)
        {
            return String.format(
                    "userFollowers:%d, paidFollowerCount:%d, freeFollowerCount:%d",
                    userFollowers.size(),
                    getPaidFollowerCount(),
                    getFreeFollowerCount());
        }
        return String.format(
                "userFollowers is null, paidFollowerCount:%d, freeFollowerCount:%d",
                getPaidFollowerCount(),
                getFreeFollowerCount());
    }
}
