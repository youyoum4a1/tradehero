package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionDTOList;
import com.tradehero.th.api.competition.CompetitionFormDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class CompetitionServiceWrapper
{
    @NonNull private final CompetitionServiceRx competitionServiceRx;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final HomeContentCacheRx homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionServiceWrapper(
            @NonNull CompetitionServiceRx competitionServiceRx,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache)
    {
        super();
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
    @NonNull public Observable<CompetitionDTOList> getCompetitionsRx(@NonNull ProviderId providerId)
    {
        return this.competitionServiceRx.getCompetitions(providerId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Competition">
    @NonNull public Observable<CompetitionDTO> getCompetitionRx(@NonNull CompetitionId competitionId)
    {
        return competitionServiceRx.getCompetition(competitionId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Competition Leaderboard">
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
