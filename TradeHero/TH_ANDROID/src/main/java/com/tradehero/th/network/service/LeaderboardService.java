package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 3:35 PM Copyright (c) TradeHero */
public interface LeaderboardService
{
    @GET("/leaderboards/{lbId}")
    LeaderboardDTO getLeaderboards(@Path("lbId") int lbId, @Query("path") int page, int perPage, @Query("sortType") int sortType);

    @GET("/leaderboards") List<LeaderboardDefDTO> getLeaderboardDefinitions();
}
