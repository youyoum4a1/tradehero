package com.tradehero.th.network.service;

import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;


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
    public UserFollowerDTO getFollowerSubscriptionDetail(FollowerHeroRelationId followerHeroRelationId)
    {
        basicCheck(followerHeroRelationId);
        return this.followerService.getFollowerSubscriptionDetail(followerHeroRelationId.heroId, followerHeroRelationId.followerId);
    }

    public void getFollowerSubscriptionDetail(FollowerHeroRelationId followerHeroRelationId, Callback<UserFollowerDTO> callback)
    {
        basicCheck(followerHeroRelationId);
        this.followerService.getFollowerSubscriptionDetail(followerHeroRelationId.heroId, followerHeroRelationId.followerId, callback);
    }
    //</editor-fold>

    private void basicCheck(FollowerHeroRelationId followerHeroRelationId)
    {
        if (followerHeroRelationId == null)
        {
            throw new NullPointerException("followerId cannot be null");
        }
        if (followerHeroRelationId.followerId == null)
        {
            throw new NullPointerException("followerId.followerId cannot be null");
        }
        if (followerHeroRelationId.heroId == null)
        {
            throw new NullPointerException("followerId.heroId cannot be null");
        }
    }
}
