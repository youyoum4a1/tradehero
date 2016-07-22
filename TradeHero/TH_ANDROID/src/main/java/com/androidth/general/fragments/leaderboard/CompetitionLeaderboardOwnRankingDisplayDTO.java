package com.androidth.general.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import com.androidth.general.common.annotation.ViewVisibilityValue;
import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.leaderboard.LeaderboardUserDTO;
import com.androidth.general.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.models.number.THSignedNumber;

public class CompetitionLeaderboardOwnRankingDisplayDTO extends CompetitionLeaderboardItemDisplayDTO
{
    private String infoTextFormat;
    @ViewVisibilityValue int infoButtonContainerVisibility;
    @NonNull CharSequence infoText;
    @Nullable public String rule;
    @Nullable public ForegroundColorSpan textColorSpan;

    public CompetitionLeaderboardOwnRankingDisplayDTO(@NonNull Resources resources,
            @NonNull CurrentUserId currentUserId)
    {
        super(resources, currentUserId);
        infoButtonContainerVisibility = View.GONE;
        infoText = "";
        infoTextFormat = "";
    }

    public CompetitionLeaderboardOwnRankingDisplayDTO(@NonNull Resources resources, @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileDTO currentUserProfileDTO, ProviderDTO providerDTO)
    {
        super(resources, currentUserId, currentUserProfileDTO, providerDTO);
        infoButtonContainerVisibility = View.GONE;
        infoText = "";
        infoTextFormat = "";
        rule = resources.getString(R.string.leaderboard_see_competition_rules);
        textColorSpan = createTextColorSpan(resources);
    }

    public CompetitionLeaderboardOwnRankingDisplayDTO(@NonNull Resources resources, @NonNull CurrentUserId currentUserId,
            @NonNull LeaderboardUserDTO leaderboardItem,
            @NonNull UserProfileDTO currentUserProfileDTO, @NonNull ProviderDTO providerDTO, CompetitionLeaderboardDTO competitionLeaderboardDTO)
    {
        super(resources, currentUserId, leaderboardItem, currentUserProfileDTO, providerDTO, competitionLeaderboardDTO);
        this.infoButtonContainerVisibility = (this.prizeSize > 0 && this.ranking <= this.prizeSize) ? View.VISIBLE : View.GONE;
        infoTextFormat = resources.getString(R.string.leaderboard_ranks_needed);
    }

    private ForegroundColorSpan createTextColorSpan(Resources resources)
    {
        return new ForegroundColorSpan(resources.getColor(R.color.general_brand_color));
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
        else
        {
            this.infoText = "";
        }
    }
}
