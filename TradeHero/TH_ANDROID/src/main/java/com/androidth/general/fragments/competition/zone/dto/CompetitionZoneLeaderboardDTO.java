package com.androidth.general.fragments.competition.zone.dto;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;

import com.androidth.general.R;
import com.androidth.general.api.competition.CompetitionDTO;
import com.androidth.general.fragments.leaderboard.LeaderboardType;
import com.androidth.general.models.number.THSignedPercentage;

public class CompetitionZoneLeaderboardDTO extends CompetitionZoneDTO
{
    @NonNull public final CompetitionDTO competitionDTO;
    @NonNull public final LeaderboardType leaderboardType;
    //public final int titleColor;
    @NonNull public final Spanned roi;

    //<editor-fold desc="Constructors">
    public CompetitionZoneLeaderboardDTO(
            @NonNull Resources resources,
            @Nullable String title,
            @Nullable String description,
            @NonNull CompetitionDTO competitionDTO,
            @Nullable LeaderboardType leaderboardType)
    {
        super(title,
                description,
                competitionDTO.getIconUrl(),
                R.drawable.default_image);
        this.competitionDTO = competitionDTO;
        this.leaderboardType = leaderboardType != null ? leaderboardType : LeaderboardType.STOCKS;

        Boolean isActive = isActive();
        //titleColor = isActive == null || isActive ? CompetitionZoneLeaderboardListItemView.COLOR_ACTIVE : CompetitionZoneLeaderboardListItemView.COLOR_INACTIVE;

        //<editor-fold desc="ROI">
        if (competitionDTO.leaderboardUser != null)
        {
            roi = THSignedPercentage
                    .builder(competitionDTO.leaderboardUser.roiInPeriod * 100)
                    .withDefaultColor()
                    .build().createSpanned();
        }
        else
        {
            roi = new SpannableString(resources.getString(R.string.na));
        }
        //</editor-fold>
    }
    //</editor-fold>

    @Nullable public Boolean isActive()
    {
        if (competitionDTO.leaderboard == null)
        {
            return null;
        }
        return competitionDTO.leaderboard.isWithinUtcRestricted();
    }
}
