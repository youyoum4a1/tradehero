package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import com.androidth.general.api.social.FollowerSummaryDTO;
import com.androidth.general.api.social.UserFollowerDTO;
import com.androidth.general.api.social.key.FollowerHeroRelationId;
import com.androidth.general.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;
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
