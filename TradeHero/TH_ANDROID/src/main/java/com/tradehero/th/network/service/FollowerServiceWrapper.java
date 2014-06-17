package com.tradehero.th.network.service;

import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;

@Singleton public class FollowerServiceWrapper
{
    @NotNull private final FollowerService followerService;
    @NotNull private final FollowerServiceAsync followerServiceAsync;

    @Inject public FollowerServiceWrapper(
            @NotNull FollowerService followerService,
            @NotNull FollowerServiceAsync followerServiceAsync)
    {
        super();
        this.followerService = followerService;
        this.followerServiceAsync = followerServiceAsync;
    }

    //<editor-fold desc="Get All Followers Summary">
    public FollowerSummaryDTO getAllFollowersSummary(UserBaseKey heroId)
    {
        return followerService.getAllFollowersSummary(heroId.key);
    }

    public MiddleCallback<FollowerSummaryDTO> getAllFollowersSummary(UserBaseKey heroId, Callback<FollowerSummaryDTO> callback)
    {
        MiddleCallback<FollowerSummaryDTO> middleCallback = new BaseMiddleCallback<>(callback);
        followerServiceAsync.getAllFollowersSummary(heroId.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Follower Subscription Detail">
    public UserFollowerDTO getFollowerSubscriptionDetail(FollowerHeroRelationId followerHeroRelationId)
    {
        basicCheck(followerHeroRelationId);
        return this.followerService.getFollowerSubscriptionDetail(followerHeroRelationId.heroId, followerHeroRelationId.followerId);
    }

    public MiddleCallback<UserFollowerDTO> getFollowerSubscriptionDetail(FollowerHeroRelationId followerHeroRelationId, Callback<UserFollowerDTO> callback)
    {
        basicCheck(followerHeroRelationId);
        MiddleCallback<UserFollowerDTO> middleCallback = new BaseMiddleCallback<>(callback);
        this.followerServiceAsync.getFollowerSubscriptionDetail(followerHeroRelationId.heroId, followerHeroRelationId.followerId, middleCallback);
        return middleCallback;
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
