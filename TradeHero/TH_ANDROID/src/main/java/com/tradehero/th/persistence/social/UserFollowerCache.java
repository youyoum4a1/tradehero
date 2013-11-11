package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.FollowerId;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.FollowerService;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class UserFollowerCache extends StraightDTOCache<FollowerId, UserFollowerDTO>
{
    public static final String TAG = UserFollowerCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 100;

    @Inject protected Lazy<FollowerService> followerService;

    //<editor-fold desc="Constructors">
    @Inject public UserFollowerCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected UserFollowerDTO fetch(FollowerId key) throws Throwable
    {
        return followerService.get().getFollowerSubscriptionDetail(key.followedId, key.followerId);
    }

    public List<UserFollowerDTO> getOrFetch(List<FollowerId> followerIds) throws Throwable
    {
        if (followerIds == null)
        {
            return null;
        }

        List<UserFollowerDTO> followerSummaryDTOs = new ArrayList<>();
        for (FollowerId baseKey: followerIds)
        {
            followerSummaryDTOs.add(getOrFetch(baseKey, false));
        }
        return followerSummaryDTOs;
    }
}
