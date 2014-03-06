package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.SortedPerPagedLeaderboardKey;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by xavier on 1/22/14.
 */
@Singleton public class LeaderboardServiceWrapper
{
    private final LeaderboardService leaderboardService;

    @Inject public LeaderboardServiceWrapper(LeaderboardService leaderboardService)
    {
        super();
        this.leaderboardService = leaderboardService;
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

    //<editor-fold desc="Get Leaderboard">
    public LeaderboardDTO getLeaderboard(LeaderboardKey leaderboardKey) throws RetrofitError
    {
        if (leaderboardKey instanceof PagedLeaderboardKey)
        {
            return getLeaderboard((PagedLeaderboardKey) leaderboardKey);
        }
        return leaderboardService.getLeaderboard(leaderboardKey.key);
    }

    public void getLeaderboard(LeaderboardKey leaderboardKey, Callback<LeaderboardDTO> callback)
    {
        if (leaderboardKey instanceof PagedLeaderboardKey)
        {
            getLeaderboard((PagedLeaderboardKey) leaderboardKey, callback);
        }
        else
        {
            leaderboardService.getLeaderboard(leaderboardKey.key, callback);
        }
    }

    public LeaderboardDTO getLeaderboard(PagedLeaderboardKey pagedLeaderboardKey) throws RetrofitError
    {
        if (pagedLeaderboardKey instanceof PerPagedLeaderboardKey)
        {
            return getLeaderboard((PerPagedLeaderboardKey) pagedLeaderboardKey);
        }
        return leaderboardService.getLeaderboard(
                pagedLeaderboardKey.key,
                pagedLeaderboardKey.page);
    }

    public void getLeaderboard(PagedLeaderboardKey pagedLeaderboardKey, Callback<LeaderboardDTO> callback)
    {
        if (pagedLeaderboardKey instanceof PerPagedLeaderboardKey)
        {
            getLeaderboard((PerPagedLeaderboardKey) pagedLeaderboardKey, callback);
        }
        else
        {
            leaderboardService.getLeaderboard(
                    pagedLeaderboardKey.key,
                    pagedLeaderboardKey.page,
                    callback);
        }
    }

    public LeaderboardDTO getLeaderboard(PerPagedLeaderboardKey perPagedLeaderboardKey) throws RetrofitError
    {
        if (perPagedLeaderboardKey instanceof SortedPerPagedLeaderboardKey)
        {
            return getLeaderboard((SortedPerPagedLeaderboardKey) perPagedLeaderboardKey);
        }
        if (perPagedLeaderboardKey instanceof PerPagedFilteredLeaderboardKey)
        {
            return getLeaderboard((PerPagedFilteredLeaderboardKey) perPagedLeaderboardKey);
        }
        if (perPagedLeaderboardKey instanceof FriendsPerPagedLeaderboardKey)
        {
            return getLeaderboard((FriendsPerPagedLeaderboardKey) perPagedLeaderboardKey);
        }
        return leaderboardService.getLeaderboard(
                perPagedLeaderboardKey.key,
                perPagedLeaderboardKey.page,
                perPagedLeaderboardKey.perPage);
    }

    public void getLeaderboard(PerPagedLeaderboardKey perPagedLeaderboardKey, Callback<LeaderboardDTO> callback)
    {
        if (perPagedLeaderboardKey instanceof SortedPerPagedLeaderboardKey)
        {
            getLeaderboard((SortedPerPagedLeaderboardKey) perPagedLeaderboardKey, callback);
        }
        else if (perPagedLeaderboardKey instanceof PerPagedFilteredLeaderboardKey)
        {
            getLeaderboard((PerPagedFilteredLeaderboardKey) perPagedLeaderboardKey, callback);
        }
        else if (perPagedLeaderboardKey instanceof FriendsPerPagedLeaderboardKey)
        {
            getLeaderboard((FriendsPerPagedLeaderboardKey) perPagedLeaderboardKey, callback);
        }
        else

        {
            leaderboardService.getLeaderboard(
                    perPagedLeaderboardKey.key,
                    perPagedLeaderboardKey.page,
                    perPagedLeaderboardKey.perPage,
                    callback);
        }
    }

    public LeaderboardDTO getLeaderboard(SortedPerPagedLeaderboardKey sortedPerPagedLeaderboardKey) throws RetrofitError
    {
        return leaderboardService.getLeaderboard(
                sortedPerPagedLeaderboardKey.key,
                sortedPerPagedLeaderboardKey.page,
                sortedPerPagedLeaderboardKey.perPage,
                sortedPerPagedLeaderboardKey.sortType);
    }

    public void getLeaderboard(SortedPerPagedLeaderboardKey sortedPerPagedLeaderboardKey, Callback<LeaderboardDTO> callback)
    {
        leaderboardService.getLeaderboard(
                sortedPerPagedLeaderboardKey.key,
                sortedPerPagedLeaderboardKey.page,
                sortedPerPagedLeaderboardKey.perPage,
                sortedPerPagedLeaderboardKey.sortType,
                callback);
    }

    public LeaderboardDTO getLeaderboard(FriendsPerPagedLeaderboardKey friendsPerPagedLeaderboardKey) throws RetrofitError
    {
        if (friendsPerPagedLeaderboardKey.includeFoF == null)
        {
            return leaderboardService.getFriendsLeaderboard(
                    friendsPerPagedLeaderboardKey.page,
                    friendsPerPagedLeaderboardKey.perPage);
        }
        else if (friendsPerPagedLeaderboardKey.perPage == null)
        {
            return leaderboardService.getFriendsLeaderboard(
                    friendsPerPagedLeaderboardKey.page);
        }
        else if (friendsPerPagedLeaderboardKey.page == null)
        {
            return leaderboardService.getFriendsLeaderboard();
        }
        return leaderboardService.getFriendsLeaderboard(
                friendsPerPagedLeaderboardKey.page,
                friendsPerPagedLeaderboardKey.perPage,
                friendsPerPagedLeaderboardKey.includeFoF);
    }

    public void getLeaderboard(FriendsPerPagedLeaderboardKey friendsPerPagedLeaderboardKey, Callback<LeaderboardDTO> callback)
    {
        if (friendsPerPagedLeaderboardKey.includeFoF == null)
        {
            leaderboardService.getFriendsLeaderboard(
                    friendsPerPagedLeaderboardKey.page,
                    friendsPerPagedLeaderboardKey.perPage,
                    callback);
        }
        else if (friendsPerPagedLeaderboardKey.perPage == null)
        {
            leaderboardService.getFriendsLeaderboard(
                    friendsPerPagedLeaderboardKey.page,
                    callback);
        }
        else if (friendsPerPagedLeaderboardKey.page == null)
        {
            leaderboardService.getFriendsLeaderboard(callback);
        }
        else
        {
            leaderboardService.getFriendsLeaderboard(
                    friendsPerPagedLeaderboardKey.page,
                    friendsPerPagedLeaderboardKey.perPage,
                    friendsPerPagedLeaderboardKey.includeFoF,
                    callback);
        }
    }

    public LeaderboardDTO getLeaderboard(PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey) throws RetrofitError
    {
        if (perPagedFilteredLeaderboardKey.perPage == null)
        {
            return leaderboardService.getFilteredLeaderboard(perPagedFilteredLeaderboardKey.key,
                    perPagedFilteredLeaderboardKey.winRatio,
                    perPagedFilteredLeaderboardKey.averageMonthlyTradeCount,
                    perPagedFilteredLeaderboardKey.averageHoldingDays,
                    perPagedFilteredLeaderboardKey.minSharpeRatio,
                    perPagedFilteredLeaderboardKey.maxPosRoiVolatility,
                    perPagedFilteredLeaderboardKey.page);
        }
        else if (perPagedFilteredLeaderboardKey.page == null)
        {
            return leaderboardService.getFilteredLeaderboard(perPagedFilteredLeaderboardKey.key,
                    perPagedFilteredLeaderboardKey.winRatio,
                    perPagedFilteredLeaderboardKey.averageMonthlyTradeCount,
                    perPagedFilteredLeaderboardKey.averageHoldingDays,
                    perPagedFilteredLeaderboardKey.minSharpeRatio,
                    perPagedFilteredLeaderboardKey.maxPosRoiVolatility);
        }
        return leaderboardService.getFilteredLeaderboard(perPagedFilteredLeaderboardKey.key,
                perPagedFilteredLeaderboardKey.winRatio,
                perPagedFilteredLeaderboardKey.averageMonthlyTradeCount,
                perPagedFilteredLeaderboardKey.averageHoldingDays,
                perPagedFilteredLeaderboardKey.minSharpeRatio,
                perPagedFilteredLeaderboardKey.maxPosRoiVolatility,
                perPagedFilteredLeaderboardKey.page,
                perPagedFilteredLeaderboardKey.perPage);
    }

    public void getLeaderboard(PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey, Callback<LeaderboardDTO> callback)
    {
        if (perPagedFilteredLeaderboardKey.perPage == null)
        {
            leaderboardService.getFilteredLeaderboard(perPagedFilteredLeaderboardKey.key,
                    perPagedFilteredLeaderboardKey.winRatio,
                    perPagedFilteredLeaderboardKey.averageMonthlyTradeCount,
                    perPagedFilteredLeaderboardKey.averageHoldingDays,
                    perPagedFilteredLeaderboardKey.minSharpeRatio,
                    perPagedFilteredLeaderboardKey.maxPosRoiVolatility,
                    perPagedFilteredLeaderboardKey.page,
                    callback);
        }
        else if (perPagedFilteredLeaderboardKey.page == null)
        {
            leaderboardService.getFilteredLeaderboard(perPagedFilteredLeaderboardKey.key,
                    perPagedFilteredLeaderboardKey.winRatio,
                    perPagedFilteredLeaderboardKey.averageMonthlyTradeCount,
                    perPagedFilteredLeaderboardKey.averageHoldingDays,
                    perPagedFilteredLeaderboardKey.minSharpeRatio,
                    perPagedFilteredLeaderboardKey.maxPosRoiVolatility,
                    callback);
        }
        else
        {
            leaderboardService.getFilteredLeaderboard(perPagedFilteredLeaderboardKey.key,
                    perPagedFilteredLeaderboardKey.winRatio,
                    perPagedFilteredLeaderboardKey.averageMonthlyTradeCount,
                    perPagedFilteredLeaderboardKey.averageHoldingDays,
                    perPagedFilteredLeaderboardKey.minSharpeRatio,
                    perPagedFilteredLeaderboardKey.maxPosRoiVolatility,
                    perPagedFilteredLeaderboardKey.page,
                    perPagedFilteredLeaderboardKey.perPage,
                    callback);
        }
    }
    //</editor-fold>
}
