package com.androidth.general.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import com.androidth.general.common.annotation.ViewVisibilityValue;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.leaderboard.LeaderboardUserDTO;
import com.androidth.general.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.androidth.general.api.leaderboard.competition.CompetitionLeaderboardId;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserProfileDTO;

class CompetitionLeaderboardItemDisplayDTO extends LeaderboardMarkedUserItemDisplayDto
{
    public ProviderDTO providerDTO;
    protected final int prizeSize;
    @ViewVisibilityValue public int prizeIconVisibility = View.GONE;

    public CompetitionLeaderboardItemDisplayDTO(@NonNull Resources resources,
            @NonNull CurrentUserId currentUserId)
    {
        super(resources, currentUserId);
        this.prizeSize = 0;
    }

    public CompetitionLeaderboardItemDisplayDTO(@NonNull Resources resources, @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileDTO currentUserProfileDTO, ProviderDTO providerDTO, Integer capAt)
    {
        super(resources, currentUserId, currentUserProfileDTO, capAt);
        this.providerDTO = providerDTO;
        this.prizeSize = 0;
    }

    public CompetitionLeaderboardItemDisplayDTO(@NonNull Resources resources, @NonNull CurrentUserId currentUserId,
            @NonNull LeaderboardUserDTO leaderboardItem,
            @NonNull UserProfileDTO currentUserProfileDTO,
            @NonNull ProviderDTO providerDTO,
            @NonNull CompetitionLeaderboardDTO competitionLeaderboardDTO)
    {
        super(resources, currentUserId, leaderboardItem, currentUserProfileDTO);
        this.providerDTO = providerDTO;
        this.prizeSize = competitionLeaderboardDTO.getPrizeSize();
        this.lbmuRoiPeriodVisibility = View.VISIBLE;
    }

    @Override public void setRanking(int ranking)
    {
        super.setRanking(ranking);
        isQualifiedForPrize(prizeSize > 0 && ranking <= prizeSize);
    }

    protected void isQualifiedForPrize(boolean qualified)
    {
        prizeIconVisibility = qualified ? View.VISIBLE : View.GONE;
    }

    public static class Requisite extends LeaderboardMarkedUserItemDisplayDto.Requisite
    {
        @NonNull final ProviderDTO providerDTO;
        @NonNull final CompetitionLeaderboardDTO competitionLeaderboardDTO;
        final Integer capAt;

        public Requisite(@NonNull LeaderboardMarkedUserItemDisplayDto.Requisite parent,
                @NonNull Pair<ProviderId, ProviderDTO> providerPair,
                @NonNull CompetitionLeaderboardDTO competitionLeaderboard)
        {
            this(parent.currentLeaderboardUserDTO,
                    parent.currentUserProfileDTO,
                    providerPair.second,
                    competitionLeaderboard);
        }

        public Requisite(
                @Nullable LeaderboardUserDTO currentLeaderboardUserDTO,
                @NonNull UserProfileDTO currentUserProfileDTO,
                @NonNull ProviderDTO providerDTO,
                @NonNull CompetitionLeaderboardDTO competitionLeaderboardDTO)
        {
            super(currentLeaderboardUserDTO, currentUserProfileDTO, competitionLeaderboardDTO.leaderboard.getCapAt());
            this.providerDTO = providerDTO;
            this.competitionLeaderboardDTO = competitionLeaderboardDTO;
            this.capAt = this.competitionLeaderboardDTO.leaderboard.getCapAt();
        }
    }
}
