package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import retrofit.Callback;
import retrofit.http.GET;

interface LeaderboardServiceAsync {
    @GET("/leaderboards/newfriends")
    void getNewFriendsLeaderboard(Callback<LeaderboardFriendsDTO> callback);

}
