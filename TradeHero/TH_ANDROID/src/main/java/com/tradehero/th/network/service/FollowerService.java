package com.tradehero.th.network.service;

import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 9:24 PM To change this template use File | Settings | File Templates. */
public interface FollowerService
{
    //<editor-fold desc="Get Followers Summary">
    @GET("/followersSummary/{userId}")
    FollowerSummaryDTO getFollowersSummary(
            @Path("userId") int userId)
        throws RetrofitError;



    @GET("/followersSummary/{userId}")
    void getFollowersSummary(
            @Path("userId") int userId,
            Callback<FollowerSummaryDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get Follower Subscription Detail">
    @GET("/followersSummary/{userId}/{followerId}")
    UserFollowerDTO getFollowerSubscriptionDetail(
            @Path("userId") int userId,
            @Path("followerId") int followerId)
        throws RetrofitError;

    @GET("/followersSummary/{userId}/{followerId}")
    void getFollowerSubscriptionDetail(
            @Path("userId") int userId,
            @Path("followerId") int followerId,
            Callback<UserFollowerDTO> callback);
    //</editor-fold>
}
