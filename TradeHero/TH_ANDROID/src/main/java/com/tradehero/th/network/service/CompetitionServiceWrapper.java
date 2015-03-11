package com.tradehero.th.network.service;

import com.tradehero.chinabuild.cache.*;
import com.tradehero.chinabuild.data.CompetitionDescription;
import com.tradehero.chinabuild.data.UGCFromDTO;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.chinabuild.data.UserCompetitionDTOList;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class CompetitionServiceWrapper
{
    @NotNull private final CompetitionService competitionService;
    @NotNull private final CompetitionServiceAsync competitionServiceAsync;
    @NotNull private final UserProfileCache userProfileCache;

    @Inject public CompetitionServiceWrapper(
            @NotNull CompetitionService competitionService,
            @NotNull CompetitionServiceAsync competitionServiceAsync,
            @NotNull UserProfileCache userProfileCache)
    {
        super();
        this.competitionService = competitionService;
        this.competitionServiceAsync = competitionServiceAsync;
        this.userProfileCache = userProfileCache;
    }

    //<editor-fold desc="Get Competitions">
    public CompetitionDTOList getCompetitions(@NotNull ProviderId providerId)
    {
        return this.competitionService.getCompetitions(providerId.key);
    }

    //<editor-fold desc="Get Competition">
    public CompetitionDTO getCompetition(@NotNull CompetitionId competitionId)
    {
        return competitionService.getCompetition(competitionId.key);
    }

    //<editor-fold desc="Get Competition Leaderboard">
    public CompetitionLeaderboardDTO getCompetitionLeaderboard(@NotNull CompetitionLeaderboardId competitionLeaderboardId)
    {
        return this.competitionService.getCompetitionLeaderboard(
                competitionLeaderboardId.providerId,
                competitionLeaderboardId.competitionId,
                competitionLeaderboardId.page,
                competitionLeaderboardId.perPage);
    }

    //用户创建UGC比赛
    @NotNull public MiddleCallback<UserCompetitionDTO> creatUGC(
            @NotNull String name, @NotNull String description, @NotNull int durationDays, @NotNull int[] exchangeIds,
            @Nullable Callback<UserCompetitionDTO> callback)
    {
        MiddleCallback<UserCompetitionDTO> middleCallback = new BaseMiddleCallback<>(callback);
        this.competitionServiceAsync.creatUGC(new UGCFromDTO(name, description, durationDays, exchangeIds), middleCallback);
        return middleCallback;
    }

    //用户报名参加UGC比赛
    @NotNull public MiddleCallback<UserCompetitionDTO> enrollUGCompetition(
            int competitionId,
            @Nullable Callback<UserCompetitionDTO> callback)
    {
        MiddleCallback<UserCompetitionDTO> middleCallback = new BaseMiddleCallback<>(callback);
        this.competitionServiceAsync.enrollUGCompetition(competitionId, middleCallback);
        return middleCallback;
    }

    //通过比赛id获取比赛详情
    @NotNull public MiddleCallback<UserCompetitionDTO> getCompetitionDetail(
            int competitionId,
            @Nullable Callback<UserCompetitionDTO> callback)
    {
        MiddleCallback<UserCompetitionDTO> middleCallback = new BaseMiddleCallback<>(callback);
        this.competitionServiceAsync.getCompetitionDetail(competitionId, middleCallback);
        return middleCallback;
    }



    //获取自己的比赛排名信息
    @NotNull public MiddleCallback<LeaderboardDTO> getMySelfRank(
            int leaderboardsId,int userId,
            @Nullable Callback<LeaderboardDTO> callback)
    {
        MiddleCallback<LeaderboardDTO> middleCallback = new BaseMiddleCallback<>(callback);
        this.competitionServiceAsync.getMySelfRank(leaderboardsId,userId,middleCallback);
        return middleCallback;
    }

    public void retrieveMyOpenCompetitions(Callback<UserCompetitionDTOList> callback){
        competitionServiceAsync.getMyOpenCompetitions(callback);
    }

    public void retrieveMyClosedCompetitions(int perPage, int page, Callback<UserCompetitionDTOList> callback){
        competitionServiceAsync.getMyClosedCompetitions(perPage, page, callback);
    }

    public void updateCompetitionDescription(int competitionId, CompetitionDescription description, Callback<UserCompetitionDTO> callback){
        competitionServiceAsync.updateCompetitionDescription(competitionId, description, callback);
    }

    @NotNull public UserCompetitionDTOList getCompetition(CompetitionListType key)
    {
        if (key instanceof CompetitionListTypeOffical)
        {
            return competitionService.getOfficalCompetitions(key.page, key.PER_PAGE);
        }
        else if (key instanceof CompetitionListTypeUser)
        {
            return competitionService.getUserCompetitions(key.page, key.PER_PAGE);
        }
        else if (key instanceof CompetitionListTypeVip)
        {
            return competitionService.getVipCompetitions(key.page, key.PER_PAGE);
        }
        else if (key instanceof CompetitionListTypeSearch)
        {
            return competitionService.getSearchCompetitions(((CompetitionListTypeSearch) key).name, key.page, key.PER_PAGE);
        }
        else if(key instanceof CompetitionListTypeRecommand)
        {
            return competitionService.getRecommandCompetitions();
        }

        return null;
    }

    @NotNull public PositionDTO getPositionDTO(PositionDTOKey key)
    {
        return competitionService.getPositionCompactDTO(key.competitionId, key.securityId.getExchange(),key.securityId.getSecuritySymbol());
    }
}
