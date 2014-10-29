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
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton public class FollowerServiceWrapper
{
    @NotNull private final FollowerService followerService;
    @NotNull private final FollowerServiceAsync followerServiceAsync;
    @NotNull private final FollowerServiceRx followerServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public FollowerServiceWrapper(
            @NotNull FollowerService followerService,
            @NotNull FollowerServiceAsync followerServiceAsync,
            @NotNull FollowerServiceRx followerServiceRx)
    {
        super();
        this.followerService = followerService;
        this.followerServiceAsync = followerServiceAsync;
        this.followerServiceRx = followerServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get All Followers Summary">
    @NotNull public FollowerSummaryDTO getAllFollowersSummary(@NotNull UserBaseKey heroId)
    {
        return followerService.getAllFollowersSummary(heroId.key);
    }

    @NotNull public MiddleCallback<FollowerSummaryDTO> getAllFollowersSummary(@NotNull UserBaseKey heroId, @Nullable Callback<FollowerSummaryDTO> callback)
    {
        MiddleCallback<FollowerSummaryDTO> middleCallback = new BaseMiddleCallback<>(callback);
        followerServiceAsync.getAllFollowersSummary(heroId.key, middleCallback);
        return middleCallback;
    }

    @NotNull public Observable<FollowerSummaryDTO> getAllFollowersSummaryRx(@NotNull UserBaseKey heroId)
    {
        return followerServiceRx.getAllFollowersSummary(heroId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Follower Subscription Detail">
    @NotNull public UserFollowerDTO getFollowerSubscriptionDetail(@NotNull FollowerHeroRelationId followerHeroRelationId)
    {
        return this.followerService.getFollowerSubscriptionDetail(followerHeroRelationId.heroId, followerHeroRelationId.followerId);
    }

    @NotNull public MiddleCallback<UserFollowerDTO> getFollowerSubscriptionDetail(@NotNull FollowerHeroRelationId followerHeroRelationId, @Nullable Callback<UserFollowerDTO> callback)
    {
        MiddleCallback<UserFollowerDTO> middleCallback = new BaseMiddleCallback<>(callback);
        this.followerServiceAsync.getFollowerSubscriptionDetail(followerHeroRelationId.heroId, followerHeroRelationId.followerId, middleCallback);
        return middleCallback;
    }

    @NotNull public Observable<UserFollowerDTO> getFollowerSubscriptionDetailRx(@NotNull FollowerHeroRelationId followerHeroRelationId)
    {
        return this.followerServiceRx.getFollowerSubscriptionDetail(followerHeroRelationId.heroId, followerHeroRelationId.followerId);
    }
    //</editor-fold>
}
