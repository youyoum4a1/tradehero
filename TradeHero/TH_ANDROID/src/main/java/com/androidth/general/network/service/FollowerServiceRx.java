package com.androidth.general.network.service;

import com.androidth.general.api.social.FollowerSummaryDTO;
import com.androidth.general.api.social.UserFollowerDTO;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

interface FollowerServiceRx
{
    //<editor-fold desc="Get All Followers Summary">
    @GET("api/followersSummary/all/{heroId}")
    Observable<FollowerSummaryDTO> getAllFollowersSummary(
            @Path("heroId") int heroId);
    //</editor-fold>

    //<editor-fold desc="Get Follower Subscription Detail">
    @GET("api/followersSummary/{heroId}/{followerId}")
    Observable<UserFollowerDTO> getFollowerSubscriptionDetail(
            @Path("heroId") int heroId,
            @Path("followerId") int followerId);
    //</editor-fold>
}
