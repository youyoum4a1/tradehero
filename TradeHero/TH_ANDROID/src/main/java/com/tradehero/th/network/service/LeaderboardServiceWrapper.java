package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by xavier on 1/22/14.
 */
@Singleton public class LeaderboardServiceWrapper
{
    public static final String TAG = LeaderboardServiceWrapper.class.getSimpleName();

    @Inject protected LeaderboardService leaderboardService;

    @Inject public LeaderboardServiceWrapper()
    {
        super();
    }

    //<editor-fold desc="Get Leaderboard Definitions">
    public List<LeaderboardDefDTO> getLeaderboardDefinitions() throws RetrofitError
    {
        return leaderboardService.getLeaderboardDefinitions();
    }

    public void getLeaderboardDefinitions(Callback<List<LeaderboardDefDTO>> callback)
    {
        leaderboardService.getLeaderboardDefinitions(callback);
    }
    //</editor-fold>
}
