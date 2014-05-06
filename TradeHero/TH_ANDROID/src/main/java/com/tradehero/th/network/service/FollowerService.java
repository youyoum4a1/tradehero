package com.tradehero.th.network.service;

import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;


public interface FollowerService
{
    //<editor-fold desc="Get All Followers Summary">
    @GET("/followersSummary/all/{heroId}")
    FollowerSummaryDTO getAllFollowersSummary(
            @Path("heroId") int heroId);

    @GET("/followersSummary/all/{heroId}")
    void getAllFollowersSummary(
            @Path("heroId") int heroId,
            Callback<FollowerSummaryDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get Follower Subscription Detail">
    @GET("/followersSummary/{heroId}/{followerId}")
    UserFollowerDTO getFollowerSubscriptionDetail(
            @Path("heroId") int heroId,
            @Path("followerId") int followerId);

    @GET("/followersSummary/{heroId}/{followerId}")
    void getFollowerSubscriptionDetail(
            @Path("heroId") int heroId,
            @Path("followerId") int followerId,
            Callback<UserFollowerDTO> callback);
    //</editor-fold>
}
