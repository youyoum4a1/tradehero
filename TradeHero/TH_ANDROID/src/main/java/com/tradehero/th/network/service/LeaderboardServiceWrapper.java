package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOFactory;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.SortedPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.UserOnLeaderboardKey;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.PagedLeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.PerPagedLeaderboardMarkUserId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.fragments.leaderboard.LeaderboardSortType;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.leaderboard.def.DTOProcessorLeaderboardDefDTOList;
import com.tradehero.th.models.position.DTOProcessorGetPositions;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class LeaderboardServiceWrapper
{
    @NotNull private final LeaderboardService leaderboardService;
    @NotNull private final LeaderboardServiceAsync leaderboardServiceAsync;
    @NotNull private final LeaderboardDefDTOFactory leaderboardDefDTOFactory;

    @Inject public LeaderboardServiceWrapper(
            @NotNull LeaderboardService leaderboardService,
            @NotNull LeaderboardServiceAsync leaderboardServiceAsync,
            @NotNull LeaderboardDefDTOFactory leaderboardDefDTOFactory)
    {
        super();
        this.leaderboardService = leaderboardService;
        this.leaderboardServiceAsync = leaderboardServiceAsync;
        this.leaderboardDefDTOFactory = leaderboardDefDTOFactory;
    }

    protected DTOProcessor<GetPositionsDTO> createProcessorReceivedGetPositions(LeaderboardMarkUserId leaderboardMarkUserId)
    {
        return new DTOProcessorGetPositions(leaderboardMarkUserId);
    }

    protected DTOProcessor<LeaderboardDefDTOList> createProcessorLeaderboardDefDTOList()
    {
        return new DTOProcessorLeaderboardDefDTOList(leaderboardDefDTOFactory);
    }

    //<editor-fold desc="Get Leaderboard Definitions">
    @NotNull public LeaderboardDefDTOList getLeaderboardDefinitions()
    {
        return createProcessorLeaderboardDefDTOList().process(leaderboardService.getLeaderboardDefinitions());
    }

    @NotNull public MiddleCallback<LeaderboardDefDTOList> getLeaderboardDefinitions(@Nullable Callback<LeaderboardDefDTOList> callback)
    {
        MiddleCallback<LeaderboardDefDTOList> middleCallback = new BaseMiddleCallback<>(callback, createProcessorLeaderboardDefDTOList());
        leaderboardServiceAsync.getLeaderboardDefinitions(middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Leaderboard">
    public LeaderboardDTO getLeaderboard(@NotNull LeaderboardKey leaderboardKey)
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

                    // HACK https://www.pivotaltracker.com/story/show/73042972
                    Math.max(1, perPagedFilteredLeaderboardKey.averageHoldingDays),

                    perPagedFilteredLeaderboardKey.minSharpeRatio,
                    perPagedFilteredLeaderboardKey.minConsistency == null ? null : 1 / perPagedFilteredLeaderboardKey.minConsistency,
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

    @NotNull public MiddleCallback<LeaderboardDTO> getLeaderboard(
            @NotNull LeaderboardKey leaderboardKey,
            @Nullable Callback<LeaderboardDTO> callback)
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

                    // HACK https://www.pivotaltracker.com/story/show/73042972
                    Math.max(1, perPagedFilteredLeaderboardKey.averageHoldingDays),
                    perPagedFilteredLeaderboardKey.minSharpeRatio,
                    perPagedFilteredLeaderboardKey.minConsistency == null ? null : 1 / perPagedFilteredLeaderboardKey.minConsistency,
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

    public LeaderboardFriendsDTO getNewFriendsLeaderboard()
    {
        return leaderboardService.getNewFriendsLeaderboard();
    }

    @NotNull public MiddleCallback<LeaderboardFriendsDTO> getNewFriendsLeaderboard(Callback<LeaderboardFriendsDTO> callback)
    {
        MiddleCallback<LeaderboardFriendsDTO> middleCallback = new BaseMiddleCallback<>(callback);
        leaderboardServiceAsync.getNewFriendsLeaderboard(middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get User On Leaderboard">
    @NotNull public LeaderboardDTO getUserOnLeaderboard(
            @NotNull UserOnLeaderboardKey userOnLeaderboardKey,
            @Nullable LeaderboardSortType leaderboardSortType)
    {
        return leaderboardService.getUserOnLeaderboard(
                userOnLeaderboardKey.leaderboardKey.key,
                userOnLeaderboardKey.userBaseKey.key,
                leaderboardSortType == null ? null : leaderboardSortType.getFlag());
    }

    @NotNull public MiddleCallback<LeaderboardDTO> getUserOnLeaderboard(
            @NotNull UserOnLeaderboardKey userOnLeaderboardKey,
            @Nullable LeaderboardSortType leaderboardSortType,
            @Nullable Callback<LeaderboardDTO> callback)
    {
        MiddleCallback<LeaderboardDTO> middleCallback = new BaseMiddleCallback<>(callback);
        leaderboardServiceAsync.getUserOnLeaderboard(
                userOnLeaderboardKey.leaderboardKey.key,
                userOnLeaderboardKey.userBaseKey.key,
                leaderboardSortType == null ? null : leaderboardSortType.getFlag(),
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Positions For Leaderboard Mark User">
    public GetPositionsDTO getPositionsForLeaderboardMarkUser(
            @NotNull LeaderboardMarkUserId key)
    {
        GetPositionsDTO received;
        if (key instanceof PerPagedLeaderboardMarkUserId)
        {
            PerPagedLeaderboardMarkUserId perPagedLeaderboardMarkUserId = (PerPagedLeaderboardMarkUserId) key;
            received = leaderboardService.getPositionsForLeaderboardMarkUser(
                    perPagedLeaderboardMarkUserId.key,
                    perPagedLeaderboardMarkUserId.page,
                    perPagedLeaderboardMarkUserId.perPage);
        }
        else if (key instanceof PagedLeaderboardMarkUserId)
        {
            PagedLeaderboardMarkUserId pagedLeaderboardMarkUserId = (PagedLeaderboardMarkUserId) key;
            received = leaderboardService.getPositionsForLeaderboardMarkUser(
                    pagedLeaderboardMarkUserId.key,
                    pagedLeaderboardMarkUserId.page,
                    null);
        }
        else
        {
            received = leaderboardService.getPositionsForLeaderboardMarkUser(
                    key.key,
                    null,
                    null);
        }
        if (received != null)
        {
            received.setOnInPeriod(key);
        }
        return received;
    }

    @NotNull public MiddleCallback<GetPositionsDTO> getPositionsForLeaderboardMarkUser(
            @NotNull LeaderboardMarkUserId key,
            @Nullable Callback<GetPositionsDTO> callback)
    {
        MiddleCallback<GetPositionsDTO> middleCallback = new BaseMiddleCallback<>(callback, createProcessorReceivedGetPositions(key));
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
