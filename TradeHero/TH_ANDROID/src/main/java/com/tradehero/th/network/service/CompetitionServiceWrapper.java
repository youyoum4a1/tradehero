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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton public class CompetitionServiceWrapper
{
    @NonNull private final CompetitionService competitionService;
    @NonNull private final CompetitionServiceAsync competitionServiceAsync;
    @NonNull private final CompetitionServiceRx competitionServiceRx;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final HomeContentCacheRx homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionServiceWrapper(
            @NonNull CompetitionService competitionService,
            @NonNull CompetitionServiceAsync competitionServiceAsync,
            @NonNull CompetitionServiceRx competitionServiceRx,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache)
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
    public CompetitionDTOList getCompetitions(@NonNull ProviderId providerId)
    {
        return this.competitionService.getCompetitions(providerId.key);
    }

    @NonNull public Observable<CompetitionDTOList> getCompetitionsRx(@NonNull ProviderId providerId)
    {
        return this.competitionServiceRx.getCompetitions(providerId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Competition">
    public CompetitionDTO getCompetition(@NonNull CompetitionId competitionId)
    {
        return competitionService.getCompetition(competitionId.key);
    }

    @NonNull public Observable<CompetitionDTO> getCompetitionRx(@NonNull CompetitionId competitionId)
    {
        return competitionServiceRx.getCompetition(competitionId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Competition Leaderboard">
    public CompetitionLeaderboardDTO getCompetitionLeaderboard(@NonNull CompetitionLeaderboardId competitionLeaderboardId)
    {
        return this.competitionService.getCompetitionLeaderboard(
                competitionLeaderboardId.providerId,
                competitionLeaderboardId.competitionId,
                competitionLeaderboardId.page,
                competitionLeaderboardId.perPage);
    }

    @NonNull public Observable<CompetitionLeaderboardDTO> getCompetitionLeaderboardRx(@NonNull CompetitionLeaderboardId competitionLeaderboardId)
    {
        return this.competitionServiceRx.getCompetitionLeaderboard(
                competitionLeaderboardId.providerId,
                competitionLeaderboardId.competitionId,
                competitionLeaderboardId.page,
                competitionLeaderboardId.perPage);
    }
    //</editor-fold>

    //<editor-fold desc="Enroll">
    public UserProfileDTO enroll(@NonNull CompetitionFormDTO form)
    {
        return createDTOProcessorUserProfile().process(this.competitionService.enroll(form));
    }

    @NonNull public MiddleCallback<UserProfileDTO> enroll(
            @NonNull CompetitionFormDTO form,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createDTOProcessorUserProfile());
        this.competitionServiceAsync.enroll(form, middleCallback);
        return middleCallback;
    }

    public Observable<UserProfileDTO> enrollRx(@NonNull CompetitionFormDTO form)
    {
        return this.competitionServiceRx.enroll(form)
                .doOnNext(createDTOProcessorUserProfile());
    }
    //</editor-fold>

    //<editor-fold desc="Outbound">
    public Observable<UserProfileDTO> outboundRx(@NonNull CompetitionFormDTO form)
    {
        return this.competitionServiceRx.outbound(form)
                .doOnNext(createDTOProcessorUserProfile());
    }
    //</editor-fold>
}
