package com.tradehero.th.network.service;

import com.tradehero.th.api.social.key.FollowerId;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.social.HeroKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

/**
 * Repurposes requests
 * Created by xavier on 12/12/13.
 */
@Singleton public class FollowerServiceWrapper
{
    private final FollowerService followerService;

    @Inject public FollowerServiceWrapper(FollowerService followerService)
    {
        super();
        this.followerService = followerService;
    }

    //<editor-fold desc="Get All Followers Summary">
    public FollowerSummaryDTO getAllFollowersSummary(UserBaseKey heroId)
    {
        return followerService.getAllFollowersSummary(heroId.key);
    }

    public void getAllFollowersSummary(UserBaseKey heroId, Callback<FollowerSummaryDTO> callback)
    {
        followerService.getAllFollowersSummary(heroId.key, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Get Follower Subscription Detail">
    public UserFollowerDTO getFollowerSubscriptionDetail(FollowerId followerId)
    {
        basicCheck(followerId);
        return this.followerService.getFollowerSubscriptionDetail(followerId.heroId, followerId.followerId);
    }

    public void getFollowerSubscriptionDetail(FollowerId followerId, Callback<UserFollowerDTO> callback)
    {
        basicCheck(followerId);
        this.followerService.getFollowerSubscriptionDetail(followerId.heroId, followerId.followerId, callback);
    }
    //</editor-fold>

    private void basicCheck(FollowerId followerId)
    {
        if (followerId == null)
        {
            throw new NullPointerException("followerId cannot be null");
        }
        if (followerId.followerId == null)
        {
            throw new NullPointerException("followerId.followerId cannot be null");
        }
        if (followerId.heroId == null)
        {
            throw new NullPointerException("followerId.heroId cannot be null");
        }
    }
}
