package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.SortedPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.position.GetLeaderboardPositionsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.PagedLeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.PerPagedLeaderboardMarkUserId;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

@Singleton public class LeaderboardServiceWrapper
{
    private final LeaderboardService leaderboardService;
    private final LeaderboardServiceAsync leaderboardServiceAsync;

    @Inject public LeaderboardServiceWrapper(
            LeaderboardService leaderboardService,
            LeaderboardServiceAsync leaderboardServiceAsync)
    {
        super();
        this.leaderboardService = leaderboardService;
        this.leaderboardServiceAsync = leaderboardServiceAsync;
    }

    //<editor-fold desc="Get Leaderboard Definitions">
    public List<LeaderboardDefDTO> getLeaderboardDefinitions()
    {
        return leaderboardService.getLeaderboardDefinitions();
    }

    public MiddleCallback<List<LeaderboardDefDTO>> getLeaderboardDefinitions(Callback<List<LeaderboardDefDTO>> callback)
    {
        MiddleCallback<List<LeaderboardDefDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        leaderboardServiceAsync.getLeaderboardDefinitions(middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Leaderboard">
    public LeaderboardDTO getLeaderboard(LeaderboardKey leaderboardKey)
    {
        if (leaderboardKey instanceof SortedPerPagedLeaderboardKey)
        {
            SortedPerPagedLeaderboardKey sortedPerPagedLeaderboardKey = (SortedPerPagedLeaderboardKey) leaderboardKey;
            return leaderboardService.getLeaderboard(
                    sortedPerPagedLeaderboardKey.key,
                    sortedPerPagedLeaderboardKey.page,
                    sortedPerPagedLeaderboardKey.perPage,
                    sortedPerPagedLeaderboardKey.sortType);
        }
        else if (leaderboardKey instanceof PerPagedFilteredLeaderboardKey)
        {
            PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey = (PerPagedFilteredLeaderboardKey) leaderboardKey;
            return leaderboardService.getFilteredLeaderboard(perPagedFilteredLeaderboardKey.key,
                    perPagedFilteredLeaderboardKey.winRatio,
                    perPagedFilteredLeaderboardKey.averageMonthlyTradeCount,
                    perPagedFilteredLeaderboardKey.averageHoldingDays,
                    perPagedFilteredLeaderboardKey.minSharpeRatio,
                    perPagedFilteredLeaderboardKey.maxPosRoiVolatility,
                    perPagedFilteredLeaderboardKey.page,
                    perPagedFilteredLeaderboardKey.perPage);
        }
        else if (leaderboardKey instanceof FriendsPerPagedLeaderboardKey)
        {
            return leaderboardService.getNewFriendsLeaderboard().leaderboard;
        }
        else if (leaderboardKey instanceof PerPagedLeaderboardKey)
        {
            PerPagedLeaderboardKey perPagedLeaderboardKey = (PerPagedLeaderboardKey) leaderboardKey;
            return leaderboardService.getLeaderboard(
                    perPagedLeaderboardKey.key,
                    perPagedLeaderboardKey.page,
                    perPagedLeaderboardKey.perPage);
        }
        else if (leaderboardKey instanceof PagedLeaderboardKey)
        {
            PagedLeaderboardKey pagedLeaderboardKey = (PagedLeaderboardKey) leaderboardKey;
            return leaderboardService.getLeaderboard(
                    pagedLeaderboardKey.key,
                    pagedLeaderboardKey.page,
                    null);
        }
        return leaderboardService.getLeaderboard(leaderboardKey.key, null, null);
    }

    public LeaderboardFriendsDTO getNewFriendsLeaderboard()
    {
        return leaderboardService.getNewFriendsLeaderboard();
    }

    public MiddleCallback<LeaderboardFriendsDTO> getNewFriendsLeaderboard(Callback<LeaderboardFriendsDTO> callback)
    {
        MiddleCallback<LeaderboardFriendsDTO> middleCallback = new BaseMiddleCallback<>(callback);
        leaderboardServiceAsync.getNewFriendsLeaderboard(middleCallback);
        return middleCallback;
    }

    public MiddleCallback<LeaderboardDTO> getLeaderboard(LeaderboardKey leaderboardKey, Callback<LeaderboardDTO> callback)
    {
        MiddleCallback<LeaderboardDTO> middleCallback = new BaseMiddleCallback<>(callback);
        if (leaderboardKey instanceof SortedPerPagedLeaderboardKey)
        {
            SortedPerPagedLeaderboardKey sortedPerPagedLeaderboardKey = (SortedPerPagedLeaderboardKey) leaderboardKey;
            leaderboardServiceAsync.getLeaderboard(
                    sortedPerPagedLeaderboardKey.key,
                    sortedPerPagedLeaderboardKey.page,
                    sortedPerPagedLeaderboardKey.perPage,
                    sortedPerPagedLeaderboardKey.sortType,
                    middleCallback);
        }
        else if (leaderboardKey instanceof PerPagedFilteredLeaderboardKey)
        {
            PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey = (PerPagedFilteredLeaderboardKey) leaderboardKey;
            leaderboardServiceAsync.getFilteredLeaderboard(perPagedFilteredLeaderboardKey.key,
                    perPagedFilteredLeaderboardKey.winRatio,
                    perPagedFilteredLeaderboardKey.averageMonthlyTradeCount,
                    perPagedFilteredLeaderboardKey.averageHoldingDays,
                    perPagedFilteredLeaderboardKey.minSharpeRatio,
                    perPagedFilteredLeaderboardKey.maxPosRoiVolatility,
                    perPagedFilteredLeaderboardKey.page,
                    perPagedFilteredLeaderboardKey.perPage,
                    middleCallback);
        }
        else if (leaderboardKey instanceof FriendsPerPagedLeaderboardKey)
        {
            FriendsPerPagedLeaderboardKey friendsPerPagedLeaderboardKey = (FriendsPerPagedLeaderboardKey) leaderboardKey;
            leaderboardServiceAsync.getFriendsLeaderboard(
                    friendsPerPagedLeaderboardKey.page,
                    friendsPerPagedLeaderboardKey.perPage,
                    friendsPerPagedLeaderboardKey.includeFoF,
                    middleCallback);
        }
        else if (leaderboardKey instanceof PerPagedLeaderboardKey)
        {
            PerPagedLeaderboardKey perPagedLeaderboardKey = (PerPagedLeaderboardKey) leaderboardKey;
            leaderboardServiceAsync.getLeaderboard(
                    perPagedLeaderboardKey.key,
                    perPagedLeaderboardKey.page,
                    perPagedLeaderboardKey.perPage,
                    middleCallback);
        }
        else if (leaderboardKey instanceof PagedLeaderboardKey)
        {
            PagedLeaderboardKey pagedLeaderboardKey = (PagedLeaderboardKey) leaderboardKey;
            leaderboardServiceAsync.getLeaderboard(
                    pagedLeaderboardKey.key,
                    pagedLeaderboardKey.page,
                    null,
                    middleCallback);
        }
        else
        {
            leaderboardServiceAsync.getLeaderboard(leaderboardKey.key, null, null, middleCallback);
        }
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Positions For Leaderboard Mark User">
    public GetLeaderboardPositionsDTO getPositionsForLeaderboardMarkUser(
            LeaderboardMarkUserId key)
    {
        if (key instanceof PerPagedLeaderboardMarkUserId)
        {
            PerPagedLeaderboardMarkUserId perPagedLeaderboardMarkUserId = (PerPagedLeaderboardMarkUserId) key;
            return leaderboardService.getPositionsForLeaderboardMarkUser(
                    perPagedLeaderboardMarkUserId.key,
                    perPagedLeaderboardMarkUserId.page,
                    perPagedLeaderboardMarkUserId.perPage);
        }
        else if (key instanceof PagedLeaderboardMarkUserId)
        {
            PagedLeaderboardMarkUserId pagedLeaderboardMarkUserId = (PagedLeaderboardMarkUserId) key;
            return leaderboardService.getPositionsForLeaderboardMarkUser(
                    pagedLeaderboardMarkUserId.key,
                    pagedLeaderboardMarkUserId.page,
                    null);
        }
        return leaderboardService.getPositionsForLeaderboardMarkUser(
                key.key,
                null,
                null);
    }

    public MiddleCallback<GetLeaderboardPositionsDTO> getPositionsForLeaderboardMarkUser(
            LeaderboardMarkUserId key,
            Callback<GetLeaderboardPositionsDTO> callback)
    {
        MiddleCallback<GetLeaderboardPositionsDTO> middleCallback = new BaseMiddleCallback<>(callback);
        if (key instanceof PerPagedLeaderboardMarkUserId)
        {
            PerPagedLeaderboardMarkUserId perPagedLeaderboardMarkUserId = (PerPagedLeaderboardMarkUserId) key;
            leaderboardServiceAsync.getPositionsForLeaderboardMarkUser(
                    perPagedLeaderboardMarkUserId.key,
                    perPagedLeaderboardMarkUserId.page,
                    perPagedLeaderboardMarkUserId.perPage,
                    middleCallback);
        }
        else if (key instanceof PagedLeaderboardMarkUserId)
        {
            PagedLeaderboardMarkUserId pagedLeaderboardMarkUserId = (PagedLeaderboardMarkUserId) key;
            leaderboardServiceAsync.getPositionsForLeaderboardMarkUser(
                    pagedLeaderboardMarkUserId.key,
                    pagedLeaderboardMarkUserId.page,
                    null,
                    middleCallback);
        }
        else
        {
            leaderboardServiceAsync.getPositionsForLeaderboardMarkUser(
                    key.key,
                    null,
                    null,
                    middleCallback);
        }
        return middleCallback;
    }
    //</editor-fold>

}
