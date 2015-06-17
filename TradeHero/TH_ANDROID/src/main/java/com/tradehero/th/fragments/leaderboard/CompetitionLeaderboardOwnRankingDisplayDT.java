package com.tradehero.th.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.number.THSignedNumber;

public class CompetitionLeaderboardOwnRankingDisplayDT extends CompetitionLeaderboardItemDisplayDT
{
    private String infoTextFormat;
    @ViewVisibilityValue int infoButtonContainerVisibility;
    @NonNull CharSequence infoText;

    public CompetitionLeaderboardOwnRankingDisplayDT(@NonNull Resources resources,
            @NonNull CurrentUserId currentUserId)
    {
        super(resources, currentUserId);
        infoButtonContainerVisibility = View.GONE;
        infoText = "";
        infoTextFormat = "";
    }

    public CompetitionLeaderboardOwnRankingDisplayDT(@NonNull Resources resources, @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileDTO currentUserProfileDTO, ProviderDTO providerDTO)
    {
        super(resources, currentUserId, currentUserProfileDTO, providerDTO);
        infoButtonContainerVisibility = View.GONE;
        infoText = "";
        infoTextFormat = "";
    }

    public CompetitionLeaderboardOwnRankingDisplayDT(@NonNull Resources resources, @NonNull CurrentUserId currentUserId,
            @NonNull LeaderboardUserDTO leaderboardItem,
            @NonNull UserProfileDTO currentUserProfileDTO, @NonNull ProviderDTO providerDTO, CompetitionLeaderboardDTO competitionLeaderboardDTO)
    {
        super(resources, currentUserId, leaderboardItem, currentUserProfileDTO, providerDTO, competitionLeaderboardDTO);
        this.infoButtonContainerVisibility = (this.prizeSize > 0 && this.ranking <= this.prizeSize) ? View.VISIBLE : View.GONE;
        infoTextFormat = resources.getString(R.string.leaderboard_ranks_needed);
    }

    @Override protected void isQualifiedForPrize(boolean qualified)
    {
        super.isQualifiedForPrize(qualified);
        boolean showInfo = this.prizeSize > 0 && qualified;
        this.infoButtonContainerVisibility = showInfo ? View.VISIBLE : View.GONE;
        if (showInfo)
        {
            int needed = this.ranking - prizeSize;
            this.infoText = THSignedNumber.builder(needed)
                    .format(infoTextFormat)
                    .build().createSpanned();
        }
    }
}
