package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.UserTrendingDTO;
import com.tradehero.chinabuild.data.UserTrendingDTOList;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
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
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.leaderboard.def.DTOProcessorLeaderboardDefDTOList;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
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

    //<editor-fold desc="Constructors">
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
    //</editor-fold>

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
        if (leaderboardKey instanceof UserOnLeaderboardKey)
        {
            return leaderboardService.getUserOnLeaderboard(
                    leaderboardKey.id,
                    ((UserOnLeaderboardKey) leaderboardKey).userBaseKey.key,
                    null);
        }
        else if (leaderboardKey instanceof SortedPerPagedLeaderboardKey)
        {
            SortedPerPagedLeaderboardKey sortedPerPagedLeaderboardKey = (SortedPerPagedLeaderboardKey) leaderboardKey;
            return leaderboardService.getLeaderboard(
                    sortedPerPagedLeaderboardKey.id,
                    sortedPerPagedLeaderboardKey.page,
                    sortedPerPagedLeaderboardKey.perPage,
                    sortedPerPagedLeaderboardKey.sortType);
        }
        else if (leaderboardKey instanceof PerPagedFilteredLeaderboardKey)
        {
            PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey = (PerPagedFilteredLeaderboardKey) leaderboardKey;
            return leaderboardService.getFilteredLeaderboard(perPagedFilteredLeaderboardKey.id,
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
                    perPagedLeaderboardKey.id,
                    perPagedLeaderboardKey.page,
                    perPagedLeaderboardKey.perPage);
        }
        else if (leaderboardKey instanceof PagedLeaderboardKey)
        {
            if (leaderboardKey.id == LeaderboardDefKeyKnowledge.DAYS_ROI)//返回推荐榜
            {
                PagedLeaderboardKey pagedLeaderboardKey = (PagedLeaderboardKey) leaderboardKey;
                UserTrendingDTOList data = leaderboardService.getLeaderboardDayROI(
                        pagedLeaderboardKey.page,
                        pagedLeaderboardKey.perPage);
                return processFromExtraData(data);
            }
            else if (leaderboardKey.id == LeaderboardDefKeyKnowledge.POPULAR)//返回人气榜
            {
                PagedLeaderboardKey pagedLeaderboardKey = (PagedLeaderboardKey) leaderboardKey;
                UserTrendingDTOList data = leaderboardService.getLeaderboardPopular(
                        pagedLeaderboardKey.page,
                        pagedLeaderboardKey.perPage);
                return processFromExtraData(data);
            }
            else if (leaderboardKey.id == LeaderboardDefKeyKnowledge.WEALTH)//返回土豪榜
            {
                PagedLeaderboardKey pagedLeaderboardKey = (PagedLeaderboardKey) leaderboardKey;
                UserTrendingDTOList data = leaderboardService.getLeaderboardWealth(
                        pagedLeaderboardKey.page,
                        pagedLeaderboardKey.perPage);
                return processFromExtraData(data);
            }
            else
            {
                PagedLeaderboardKey pagedLeaderboardKey = (PagedLeaderboardKey) leaderboardKey;
                return leaderboardService.getLeaderboard(
                        pagedLeaderboardKey.id,
                        pagedLeaderboardKey.page,
                        pagedLeaderboardKey.perPage);
            }
        }
        return leaderboardService.getLeaderboard(leaderboardKey.id, null, null);
    }

    public LeaderboardDTO processFromExtraData(UserTrendingDTOList data)
    {
        if (data != null && data.size() > 0)
        {
            LeaderboardDTO leaderboardDTO = new LeaderboardDTO();
            leaderboardDTO.users = new LeaderboardUserDTOList();
            int sizeData = data.size();
            for (int i = 0; i < sizeData; i++)
            {
                UserTrendingDTO dataDTO = data.get(i);
                LeaderboardUserDTO userDTO = new LeaderboardUserDTO();
                userDTO.id = dataDTO.userId;
                userDTO.followerCount = dataDTO.followerCount;
                userDTO.displayName = dataDTO.name;
                userDTO.picture = dataDTO.pictureUrl;
                userDTO.totalWealth = dataDTO.totalWealth;
                userDTO.roiInPeriod = dataDTO.winRatio;
                userDTO.perfRoi = dataDTO.perfRoi;
                leaderboardDTO.users.add(userDTO);
            }
            return leaderboardDTO;
        }
        return null;
    }

    @NotNull public MiddleCallback<LeaderboardDTO> getLeaderboard(
            @NotNull LeaderboardKey leaderboardKey,
            @Nullable Callback<LeaderboardDTO> callback)
    {
        MiddleCallback<LeaderboardDTO> middleCallback = new BaseMiddleCallback<>(callback);
        if (leaderboardKey instanceof UserOnLeaderboardKey)
        {
            leaderboardServiceAsync.getUserOnLeaderboard(
                    leaderboardKey.id,
                    ((UserOnLeaderboardKey) leaderboardKey).userBaseKey.key,
                    null,
                    middleCallback);
        }
        else if (leaderboardKey instanceof SortedPerPagedLeaderboardKey)
        {
            SortedPerPagedLeaderboardKey sortedPerPagedLeaderboardKey = (SortedPerPagedLeaderboardKey) leaderboardKey;
            leaderboardServiceAsync.getLeaderboard(
                    sortedPerPagedLeaderboardKey.id,
                    sortedPerPagedLeaderboardKey.page,
                    sortedPerPagedLeaderboardKey.perPage,
                    sortedPerPagedLeaderboardKey.sortType,
                    middleCallback);
        }
        else if (leaderboardKey instanceof PerPagedFilteredLeaderboardKey)
        {
            PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey = (PerPagedFilteredLeaderboardKey) leaderboardKey;
            leaderboardServiceAsync.getFilteredLeaderboard(perPagedFilteredLeaderboardKey.id,
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
                    perPagedLeaderboardKey.id,
                    perPagedLeaderboardKey.page,
                    perPagedLeaderboardKey.perPage,
                    middleCallback);
        }
        else if (leaderboardKey instanceof PagedLeaderboardKey)
        {
            PagedLeaderboardKey pagedLeaderboardKey = (PagedLeaderboardKey) leaderboardKey;
            leaderboardServiceAsync.getLeaderboard(
                    pagedLeaderboardKey.id,
                    pagedLeaderboardKey.page,
                    null,
                    middleCallback);
        }
        else
        {
            leaderboardServiceAsync.getLeaderboard(leaderboardKey.id, null, null, middleCallback);
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
