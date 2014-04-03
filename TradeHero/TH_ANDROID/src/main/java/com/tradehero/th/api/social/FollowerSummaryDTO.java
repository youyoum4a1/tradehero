package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 9:22 PM To change this template use File | Settings | File Templates. */
public class FollowerSummaryDTO implements DTO
{
    public static final String TAG = FollowerSummaryDTO.class.getSimpleName();

    public List<UserFollowerDTO> userFollowers;
    public double totalRevenue;
    //TODO
    public HeroPayoutSummaryDTO payoutSummary;


    public int paidFollowerCount;

    public int freeFollowerCount;

    public FollowerSummaryDTO()
    {
        super();
    }

    @Override public String toString()
    {
        if (userFollowers != null)
        {
            return String.format("userFollowers:%d,paidFollowerCount:%d,freeFollowerCount:%d",userFollowers.size(),paidFollowerCount,freeFollowerCount);
        }
        return String.format("userFollowers is null,paidFollowerCount:%d,freeFollowerCount:%d",userFollowers.size(),paidFollowerCount,freeFollowerCount);
    }
}
