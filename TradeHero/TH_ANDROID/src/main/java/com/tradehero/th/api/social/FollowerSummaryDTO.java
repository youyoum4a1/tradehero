package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 9:22 PM To change this template use File | Settings | File Templates. */
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
