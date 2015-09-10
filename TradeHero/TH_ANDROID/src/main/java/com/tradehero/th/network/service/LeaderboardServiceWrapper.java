package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.UserTrendingDTO;
import com.tradehero.chinabuild.data.UserTrendingDTOList;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOFactory;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.*;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.PagedLeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.PerPagedLeaderboardMarkUserId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.leaderboard.def.DTOProcessorLeaderboardDefDTOList;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LeaderboardServiceWrapper
{
    @NotNull private final LeaderboardService leaderboardService;
    @NotNull private final LeaderboardDefDTOFactory leaderboardDefDTOFactory;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardServiceWrapper(
            @NotNull LeaderboardService leaderboardService,
            @NotNull LeaderboardDefDTOFactory leaderboardDefDTOFactory)
    {
        super();
        this.leaderboardService = leaderboardService;
        this.leaderboardDefDTOFactory = leaderboardDefDTOFactory;
    }
    //</editor-fold>

    protected DTOProcessor<LeaderboardDefDTOList> createProcessorLeaderboardDefDTOList()
    {
        return new DTOProcessorLeaderboardDefDTOList(leaderboardDefDTOFactory);
    }

    //<editor-fold desc="Get Leaderboard Definitions">
    @NotNull public LeaderboardDefDTOList getLeaderboardDefinitions()
    {
        return createProcessorLeaderboardDefDTOList().process(leaderboardService.getLeaderboardDefinitions());
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
                UserTrendingDTOList data = leaderboardService.getLeaderboardPrefROI(
                        pagedLeaderboardKey.page,
                        pagedLeaderboardKey.perPage);
                return processFromExtraData(data);
            }
            else if (leaderboardKey.id == LeaderboardDefKeyKnowledge.WINRATIO)//返回高人气榜
            {
                PagedLeaderboardKey pagedLeaderboardKey = (PagedLeaderboardKey) leaderboardKey;
                UserTrendingDTOList data = leaderboardService.getLeaderboardWinRatio(
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
            else if (leaderboardKey.id == LeaderboardDefKeyKnowledge.SEARCH_RECOMMEND)//综合搜索默认推荐
            {
                UserTrendingDTOList data = leaderboardService.getLeaderboardSearchRecommend();
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
                userDTO.tradeCount = dataDTO.tradeCount;
                leaderboardDTO.users.add(userDTO);
            }
            return leaderboardDTO;
        }
        return null;
    }

    public LeaderboardFriendsDTO getNewFriendsLeaderboard()
    {
        return leaderboardService.getNewFriendsLeaderboard();
    }


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

}
