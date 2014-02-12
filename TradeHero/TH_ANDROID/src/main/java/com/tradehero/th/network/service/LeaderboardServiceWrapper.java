package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.FriendsSortedPerPagedLeaderboardKey;
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
        if (sortedPerPagedLeaderboardKey instanceof FriendsSortedPerPagedLeaderboardKey)
        {
            return getLeaderboard((FriendsSortedPerPagedLeaderboardKey) sortedPerPagedLeaderboardKey);
        }
        return leaderboardService.getLeaderboard(
                sortedPerPagedLeaderboardKey.key,
                sortedPerPagedLeaderboardKey.page,
                sortedPerPagedLeaderboardKey.perPage,
                sortedPerPagedLeaderboardKey.sortType);
    }

    public void getLeaderboard(SortedPerPagedLeaderboardKey sortedPerPagedLeaderboardKey, Callback<LeaderboardDTO> callback)
    {
        if (sortedPerPagedLeaderboardKey instanceof FriendsSortedPerPagedLeaderboardKey)
        {
            getLeaderboard((FriendsSortedPerPagedLeaderboardKey) sortedPerPagedLeaderboardKey, callback);
        }
        else
        {
            leaderboardService.getLeaderboard(
                    sortedPerPagedLeaderboardKey.key,
                    sortedPerPagedLeaderboardKey.page,
                    sortedPerPagedLeaderboardKey.perPage,
                    sortedPerPagedLeaderboardKey.sortType,
                    callback);
        }
    }

    public LeaderboardDTO getLeaderboard(FriendsSortedPerPagedLeaderboardKey friendsSortedPerPagedLeaderboardKey) throws RetrofitError
    {
        if (friendsSortedPerPagedLeaderboardKey.sortType == null)
        {
            return leaderboardService.getFriendsLeaderboard(
                    friendsSortedPerPagedLeaderboardKey.page,
                    friendsSortedPerPagedLeaderboardKey.perPage,
                    friendsSortedPerPagedLeaderboardKey.includeFoF);
        }
        else if (friendsSortedPerPagedLeaderboardKey.includeFoF == null)
        {
            return leaderboardService.getFriendsLeaderboard(
                    friendsSortedPerPagedLeaderboardKey.page,
                    friendsSortedPerPagedLeaderboardKey.perPage);
        }
        else if (friendsSortedPerPagedLeaderboardKey.perPage == null)
        {
            return leaderboardService.getFriendsLeaderboard(
                    friendsSortedPerPagedLeaderboardKey.page);
        }
        else if (friendsSortedPerPagedLeaderboardKey.page == null)
        {
            return leaderboardService.getFriendsLeaderboard();
        }
        return leaderboardService.getFriendsLeaderboard(
                friendsSortedPerPagedLeaderboardKey.page,
                friendsSortedPerPagedLeaderboardKey.perPage,
                friendsSortedPerPagedLeaderboardKey.includeFoF,
                friendsSortedPerPagedLeaderboardKey.sortType);
    }

    public void getLeaderboard(FriendsSortedPerPagedLeaderboardKey friendsSortedPerPagedLeaderboardKey, Callback<LeaderboardDTO> callback)
    {
        if (friendsSortedPerPagedLeaderboardKey.sortType == null)
        {
            leaderboardService.getFriendsLeaderboard(
                    friendsSortedPerPagedLeaderboardKey.page,
                    friendsSortedPerPagedLeaderboardKey.perPage,
                    friendsSortedPerPagedLeaderboardKey.includeFoF,
                    callback);
        }
        else if (friendsSortedPerPagedLeaderboardKey.includeFoF == null)
        {
            leaderboardService.getFriendsLeaderboard(
                    friendsSortedPerPagedLeaderboardKey.page,
                    friendsSortedPerPagedLeaderboardKey.perPage,
                    callback);
        }
        else if (friendsSortedPerPagedLeaderboardKey.perPage == null)
        {
            leaderboardService.getFriendsLeaderboard(
                    friendsSortedPerPagedLeaderboardKey.page,
                    callback);
        }
        else if (friendsSortedPerPagedLeaderboardKey.page == null)
        {
            leaderboardService.getFriendsLeaderboard(callback);
        }
        else
        {
            leaderboardService.getFriendsLeaderboard(
                    friendsSortedPerPagedLeaderboardKey.page,
                    friendsSortedPerPagedLeaderboardKey.perPage,
                    friendsSortedPerPagedLeaderboardKey.includeFoF,
                    friendsSortedPerPagedLeaderboardKey.sortType,
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
