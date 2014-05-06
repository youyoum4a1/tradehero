package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionFormDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.users.UserProfileDTO;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;


@Singleton public class CompetitionServiceWrapper
{
    private final CompetitionService competitionService;

    @Inject public CompetitionServiceWrapper(CompetitionService competitionService)
    {
        super();
        this.competitionService = competitionService;
    }

    //<editor-fold desc="Get Competitions">
    public List<CompetitionDTO> getCompetitions(ProviderId providerId)
            throws RetrofitError
    {
        return this.competitionService.getCompetitions(providerId.key);
    }

    public void getCompetitions(ProviderId providerId, Callback<List<CompetitionDTO>> callback)
    {
        this.competitionService.getCompetitions(providerId.key, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Get Competition Leaderboard">
    public CompetitionLeaderboardDTO getCompetitionLeaderboard(CompetitionLeaderboardId competitionLeaderboardId)
    {
        if (competitionLeaderboardId.page == null)
        {
            return this.competitionService.getCompetitionLeaderboard(
                    competitionLeaderboardId.providerId,
                    competitionLeaderboardId.competitionId);
        }
        else if (competitionLeaderboardId.perPage == null)
        {
            return this.competitionService.getCompetitionLeaderboard(
                    competitionLeaderboardId.providerId,
                    competitionLeaderboardId.competitionId,
                    competitionLeaderboardId.page);
        }
        return this.competitionService.getCompetitionLeaderboard(
                competitionLeaderboardId.providerId,
                competitionLeaderboardId.competitionId,
                competitionLeaderboardId.page,
                competitionLeaderboardId.perPage);
    }

    public void getCompetitionLeaderboard(
            CompetitionLeaderboardId competitionLeaderboardId,
            Callback<CompetitionLeaderboardDTO> callback)
    {
        if (competitionLeaderboardId.page == null)
        {
            this.competitionService.getCompetitionLeaderboard(
                    competitionLeaderboardId.providerId,
                    competitionLeaderboardId.competitionId,
                    callback);
        }
        else if (competitionLeaderboardId.perPage == null)
        {
            this.competitionService.getCompetitionLeaderboard(
                    competitionLeaderboardId.providerId,
                    competitionLeaderboardId.competitionId,
                    competitionLeaderboardId.page,
                    callback);
        }
        else
        {
            this.competitionService.getCompetitionLeaderboard(
                    competitionLeaderboardId.providerId,
                    competitionLeaderboardId.competitionId,
                    competitionLeaderboardId.page,
                    competitionLeaderboardId.perPage,
                    callback);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Enroll">
    public UserProfileDTO enroll(CompetitionFormDTO form)
            throws RetrofitError
    {
        return this.competitionService.enroll(form);
    }

    public void enroll(CompetitionFormDTO form, Callback<UserProfileDTO> callback)
    {
        this.competitionService.enroll(form, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Outbound">
   public UserProfileDTO outbound(CompetitionFormDTO form)
            throws RetrofitError
   {
       return this.competitionService.outbound(form);
   }

    public void outbound(CompetitionFormDTO form, Callback<UserProfileDTO> callback)
    {
        this.competitionService.outbound(form, callback);
    }
    //</editor-fold>
}
