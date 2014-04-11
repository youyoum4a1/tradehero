package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.network.service.FollowerServiceWrapper;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class FollowerSummaryCache extends StraightDTOCache<HeroKey, FollowerSummaryDTO>
{
    public static final String TAG = FollowerSummaryCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 100;

    @Inject protected Lazy<FollowerServiceWrapper> followerServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public FollowerSummaryCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected FollowerSummaryDTO fetch(HeroKey key) throws Throwable
    {
        FollowerSummaryDTO data = followerServiceWrapper.get().getFollowersSummary(key);
        computeFollowersTypeCount(data);
        if (data != null)
        {
            if (key.heroType == HeroType.ALL)
            {
                return data;
            }else {
                Map<HeroType,FollowerSummaryDTO> followersMap = splitFollowers(data);
                if (followersMap != null)
                {
                    put(new HeroKey(key.userBaseKey,HeroType.ALL),data);
                    return followersMap.get(key.heroType);
                }
                return null;

            }
        }

        return null;
    }

    @Override public FollowerSummaryDTO get(HeroKey key)
    {
        if (key.heroType == HeroType.ALL)
        {
            FollowerSummaryDTO data = super.get(key);
            Timber.d("FollowerSummaryDTO get(key:%s),data %s",key,data);
            return super.get(key);
        }else {
            //since we just cache all followers,so we need to find the followers of requested type
            FollowerSummaryDTO allFollowers = get(new HeroKey(key.userBaseKey,HeroType.ALL));
            if (allFollowers != null)
            {
                //computeFollowersTypeCount(allFollowers);
                Map<HeroType,FollowerSummaryDTO> followersMap = splitFollowers(allFollowers);
                if (followersMap != null)
                {
                    FollowerSummaryDTO data = followersMap.get(key.heroType);
                    Timber.d("FollowerSummaryDTO get(key:%s),data %s",key,data);
                    return data;
                }
            }
            return null;
        }

    }


    @Override public FollowerSummaryDTO put(HeroKey key, FollowerSummaryDTO value)
    {
        //Just cache all followers,do not cache paid followers and free followers separately.
        if (key.heroType != HeroType.ALL){
            return value;
        }
        return super.put(key, value);
    }

    public List<FollowerSummaryDTO> getOrFetch(List<HeroKey> baseKeys) throws Throwable
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<FollowerSummaryDTO> followerSummaryDTOs = new ArrayList<>();
        for (HeroKey baseKey: baseKeys)
        {
            followerSummaryDTOs.add(getOrFetch(baseKey, false));
        }
        return followerSummaryDTOs;
    }


    ////////////////////////////////

    private void computeFollowersTypeCount(FollowerSummaryDTO data)
    {
        if (data != null)
        {
            int paidCount = 0;
            int freeCount = 0;
            List<UserFollowerDTO> followerDTOList = data.userFollowers;
            if (followerDTOList != null)
            {
                //totalCount = followerDTOList.size();
                for(UserFollowerDTO follower:followerDTOList)
                {
                    if (!follower.isFreeFollow)
                    {
                        paidCount += 1;
                    }
                    else
                    {
                        freeCount += 1;
                    }

                }

            }
            data.paidFollowerCount = paidCount;
            data.freeFollowerCount = freeCount;

        }
    }

    private Map<HeroType,FollowerSummaryDTO> splitFollowers(FollowerSummaryDTO data)
    {
        if (data == null || data.userFollowers == null)
        {
            return null;
        }
        List<UserFollowerDTO> allFollowers = data.userFollowers;

        Map<HeroType,FollowerSummaryDTO> map = new HashMap();

        FollowerSummaryDTO paidFollowerSummaryDTO = new FollowerSummaryDTO();
        paidFollowerSummaryDTO.userFollowers = new ArrayList<UserFollowerDTO>();
        //TODO
        paidFollowerSummaryDTO.payoutSummary = data.payoutSummary;
        paidFollowerSummaryDTO.totalRevenue = data.totalRevenue;

        FollowerSummaryDTO freeFollowerSummaryDTO = new FollowerSummaryDTO();
        freeFollowerSummaryDTO.userFollowers = new ArrayList<UserFollowerDTO>();
        //TODO
        freeFollowerSummaryDTO.payoutSummary = data.payoutSummary;
        freeFollowerSummaryDTO.totalRevenue = data.totalRevenue;

        map.put(HeroType.PREMIUM,paidFollowerSummaryDTO);
        map.put(HeroType.FREE,freeFollowerSummaryDTO);

        for(UserFollowerDTO follower:allFollowers)
        {
            if (!follower.isFreeFollow)
            {
                paidFollowerSummaryDTO.userFollowers.add(follower);
            }else {
                freeFollowerSummaryDTO.userFollowers.add(follower);
            }
        }
        paidFollowerSummaryDTO.freeFollowerCount = data.freeFollowerCount;
        paidFollowerSummaryDTO.paidFollowerCount = data.paidFollowerCount;

        freeFollowerSummaryDTO.freeFollowerCount = data.freeFollowerCount;
        freeFollowerSummaryDTO.paidFollowerCount = data.paidFollowerCount;

        return map;
    }


}
