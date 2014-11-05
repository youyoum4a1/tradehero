package com.tradehero.th.network.service;

import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import rx.Observable;

@Singleton public class FollowerServiceWrapper
{
    @NonNull private final FollowerServiceRx followerServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public FollowerServiceWrapper(@NonNull FollowerServiceRx followerServiceRx)
    {
        super();
        this.followerServiceRx = followerServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get All Followers Summary">
    @NonNull public Observable<FollowerSummaryDTO> getAllFollowersSummaryRx(@NonNull UserBaseKey heroId)
    {
        return followerServiceRx.getAllFollowersSummary(heroId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Follower Subscription Detail">
    @NonNull public Observable<UserFollowerDTO> getFollowerSubscriptionDetailRx(@NonNull FollowerHeroRelationId followerHeroRelationId)
    {
        return this.followerServiceRx.getFollowerSubscriptionDetail(followerHeroRelationId.heroId, followerHeroRelationId.followerId);
    }
    //</editor-fold>
}
