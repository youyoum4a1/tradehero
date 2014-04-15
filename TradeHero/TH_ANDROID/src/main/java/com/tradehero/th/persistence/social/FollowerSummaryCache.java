package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.FollowerServiceWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class FollowerSummaryCache extends StraightDTOCache<UserBaseKey, FollowerSummaryDTO>
{
    public static final String TAG = FollowerSummaryCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 100;

    protected FollowerServiceWrapper followerServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public FollowerSummaryCache(FollowerServiceWrapper followerServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.followerServiceWrapper = followerServiceWrapper;
    }
    //</editor-fold>

    @Override protected FollowerSummaryDTO fetch(UserBaseKey key) throws Throwable
    {
        return followerServiceWrapper.getAllFollowersSummary(key);
    }

    public List<FollowerSummaryDTO> getOrFetch(List<UserBaseKey> baseKeys) throws Throwable
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<FollowerSummaryDTO> followerSummaryDTOs = new ArrayList<>();
        for (UserBaseKey baseKey : baseKeys)
        {
            followerSummaryDTOs.add(getOrFetch(baseKey, false));
        }
        return followerSummaryDTOs;
    }


    ////////////////////////////////

    //private void computeFollowersTypeCount(FollowerSummaryDTO data)
    //{
    //    if (data != null)
    //    {
    //        int paidCount = 0;
    //        int freeCount = 0;
    //        List<UserFollowerDTO> followerDTOList = data.userFollowers;
    //        if (followerDTOList != null)
    //        {
    //            //totalCount = followerDTOList.size();
    //            for(UserFollowerDTO follower:followerDTOList)
    //            {
    //                if (!follower.isFreeFollow)
    //                {
    //                    paidCount += 1;
    //                }
    //                else
    //                {
    //                    freeCount += 1;
    //                }
    //
    //            }
    //
    //        }
    //        data.paidFollowerCount = paidCount;
    //        data.freeFollowerCount = freeCount;
    //
    //    }
    //}
    //
    //private Map<HeroType,FollowerSummaryDTO> splitFollowers(FollowerSummaryDTO data)
    //{
    //    if (data == null || data.userFollowers == null)
    //    {
    //        return null;
    //    }
    //    List<UserFollowerDTO> allFollowers = data.userFollowers;
    //
    //    Map<HeroType,FollowerSummaryDTO> map = new HashMap();
    //
    //    FollowerSummaryDTO paidFollowerSummaryDTO = new FollowerSummaryDTO();
    //    paidFollowerSummaryDTO.userFollowers = new ArrayList<UserFollowerDTO>();
    //    //TODO
    //    paidFollowerSummaryDTO.payoutSummary = data.payoutSummary;
    //    paidFollowerSummaryDTO.totalRevenue = data.totalRevenue;
    //
    //    FollowerSummaryDTO freeFollowerSummaryDTO = new FollowerSummaryDTO();
    //    freeFollowerSummaryDTO.userFollowers = new ArrayList<UserFollowerDTO>();
    //    //TODO
    //    freeFollowerSummaryDTO.payoutSummary = data.payoutSummary;
    //    freeFollowerSummaryDTO.totalRevenue = data.totalRevenue;
    //
    //    map.put(HeroType.PREMIUM,paidFollowerSummaryDTO);
    //    map.put(HeroType.FREE,freeFollowerSummaryDTO);
    //
    //    for(UserFollowerDTO follower:allFollowers)
    //    {
    //        if (!follower.isFreeFollow)
    //        {
    //            paidFollowerSummaryDTO.userFollowers.add(follower);
    //        }else {
    //            freeFollowerSummaryDTO.userFollowers.add(follower);
    //        }
    //    }
    //    paidFollowerSummaryDTO.freeFollowerCount = data.freeFollowerCount;
    //    paidFollowerSummaryDTO.paidFollowerCount = data.paidFollowerCount;
    //
    //    freeFollowerSummaryDTO.freeFollowerCount = data.freeFollowerCount;
    //    freeFollowerSummaryDTO.paidFollowerCount = data.paidFollowerCount;
    //
    //    return map;
    //}

}
