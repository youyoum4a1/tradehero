package com.androidth.general.api.social;

import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTO;
import java.util.ArrayList;
import java.util.List;

public class FollowerSummaryDTO implements DTO
{
    public List<UserFollowerDTO> userFollowers;
    public double totalRevenue;
    public HeroPayoutSummaryDTO payoutSummary;

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

    @Nullable public List<UserFollowerDTO> getPaidFollowers()
    {
        if (userFollowers == null)
        {
            return null;
        }

        List<UserFollowerDTO> list = new ArrayList<>();

        for (UserFollowerDTO userFollowerDTO : userFollowers)
        {
            if (!userFollowerDTO.isFreeFollow)
            {
                list.add(userFollowerDTO);
            }
        }
        return list;
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

    @Nullable public List<UserFollowerDTO> getFreeFollowers()
    {
        if (userFollowers == null)
        {
            return null;
        }

        List<UserFollowerDTO> list = new ArrayList<>();

        for (UserFollowerDTO userFollowerDTO : userFollowers)
        {
            if (userFollowerDTO.isFreeFollow)
            {
                list.add(userFollowerDTO);
            }
        }
        return list;
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
