package com.tradehero.th.network.service;

import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class FollowerServiceWrapper
{
    @NotNull private final FollowerServiceRx followerServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public FollowerServiceWrapper(@NotNull FollowerServiceRx followerServiceRx)
    {
        super();
        this.followerServiceRx = followerServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get All Followers Summary">
    @NotNull public Observable<FollowerSummaryDTO> getAllFollowersSummaryRx(@NotNull UserBaseKey heroId)
    {
        return followerServiceRx.getAllFollowersSummary(heroId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Follower Subscription Detail">
    @NotNull public Observable<UserFollowerDTO> getFollowerSubscriptionDetailRx(@NotNull FollowerHeroRelationId followerHeroRelationId)
    {
        return this.followerServiceRx.getFollowerSubscriptionDetail(followerHeroRelationId.heroId, followerHeroRelationId.followerId);
    }
    //</editor-fold>
}
