package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.competition.CompetitionFormDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton public class CompetitionServiceWrapper
{
    @NotNull private final CompetitionService competitionService;
    @NotNull private final CompetitionServiceAsync competitionServiceAsync;
    @NotNull private final CompetitionServiceRx competitionServiceRx;
    @NotNull private final UserProfileCacheRx userProfileCache;
    @NotNull private final HomeContentCacheRx homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionServiceWrapper(
            @NotNull CompetitionService competitionService,
            @NotNull CompetitionServiceAsync competitionServiceAsync,
            @NotNull CompetitionServiceRx competitionServiceRx,
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull HomeContentCacheRx homeContentCache)
    {
        super();
        this.competitionService = competitionService;
        this.competitionServiceAsync = competitionServiceAsync;
        this.competitionServiceRx = competitionServiceRx;
        this.userProfileCache = userProfileCache;
        this.homeContentCache = homeContentCache;
    }
    //</editor-fold>

    protected DTOProcessorUpdateUserProfile createDTOProcessorUserProfile()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache, homeContentCache);
    }

    //<editor-fold desc="Get Competitions">
    public CompetitionDTOList getCompetitions(@NotNull ProviderId providerId)
    {
        return this.competitionService.getCompetitions(providerId.key);
    }

    @NotNull public Observable<CompetitionDTOList> getCompetitionsRx(@NotNull ProviderId providerId)
    {
        return this.competitionServiceRx.getCompetitions(providerId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Competition">
    public CompetitionDTO getCompetition(@NotNull CompetitionId competitionId)
    {
        return competitionService.getCompetition(competitionId.key);
    }

    @NotNull public Observable<CompetitionDTO> getCompetitionRx(@NotNull CompetitionId competitionId)
    {
        return competitionServiceRx.getCompetition(competitionId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Competition Leaderboard">
    public CompetitionLeaderboardDTO getCompetitionLeaderboard(@NotNull CompetitionLeaderboardId competitionLeaderboardId)
    {
        return this.competitionService.getCompetitionLeaderboard(
                competitionLeaderboardId.providerId,
                competitionLeaderboardId.competitionId,
                competitionLeaderboardId.page,
                competitionLeaderboardId.perPage);
    }

    @NotNull public Observable<CompetitionLeaderboardDTO> getCompetitionLeaderboardRx(@NotNull CompetitionLeaderboardId competitionLeaderboardId)
    {
        return this.competitionServiceRx.getCompetitionLeaderboard(
                competitionLeaderboardId.providerId,
                competitionLeaderboardId.competitionId,
                competitionLeaderboardId.page,
                competitionLeaderboardId.perPage);
    }
    //</editor-fold>

    //<editor-fold desc="Enroll">
    public UserProfileDTO enroll(@NotNull CompetitionFormDTO form)
    {
        return createDTOProcessorUserProfile().process(this.competitionService.enroll(form));
    }

    @NotNull public MiddleCallback<UserProfileDTO> enroll(
            @NotNull CompetitionFormDTO form,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createDTOProcessorUserProfile());
        this.competitionServiceAsync.enroll(form, middleCallback);
        return middleCallback;
    }

    public Observable<UserProfileDTO> enrollRx(@NotNull CompetitionFormDTO form)
    {
        return this.competitionServiceRx.enroll(form)
                .doOnNext(createDTOProcessorUserProfile());
    }
    //</editor-fold>

    //<editor-fold desc="Outbound">
    public Observable<UserProfileDTO> outboundRx(@NotNull CompetitionFormDTO form)
    {
        return this.competitionServiceRx.outbound(form)
                .doOnNext(createDTOProcessorUserProfile());
    }
    //</editor-fold>
}
