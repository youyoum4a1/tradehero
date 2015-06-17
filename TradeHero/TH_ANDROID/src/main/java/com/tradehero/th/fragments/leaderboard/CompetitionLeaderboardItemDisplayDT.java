package com.tradehero.th.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;

class CompetitionLeaderboardItemDisplayDT extends LeaderboardMarkedUserItemDisplayDto
{
    public ProviderDTO providerDTO;
    protected final int prizeSize;
    @ViewVisibilityValue public int prizeIconVisibility = View.GONE;

    public CompetitionLeaderboardItemDisplayDT(@NonNull Resources resources,
            @NonNull CurrentUserId currentUserId)
    {
        super(resources, currentUserId);
        this.prizeSize = 0;
    }

    public CompetitionLeaderboardItemDisplayDT(@NonNull Resources resources, @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileDTO currentUserProfileDTO, ProviderDTO providerDTO)
    {
        super(resources, currentUserId, currentUserProfileDTO);
        this.providerDTO = providerDTO;
        this.prizeSize = 0;
    }

    public CompetitionLeaderboardItemDisplayDT(@NonNull Resources resources, @NonNull CurrentUserId currentUserId,
            @NonNull LeaderboardUserDTO leaderboardItem,
            @NonNull UserProfileDTO currentUserProfileDTO,
            @NonNull ProviderDTO providerDTO,
            @NonNull CompetitionLeaderboardDTO competitionLeaderboardDTO)
    {
        super(resources, currentUserId, leaderboardItem, currentUserProfileDTO);
        this.providerDTO = providerDTO;
        this.prizeSize = competitionLeaderboardDTO.getPrizeSize();
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
}
