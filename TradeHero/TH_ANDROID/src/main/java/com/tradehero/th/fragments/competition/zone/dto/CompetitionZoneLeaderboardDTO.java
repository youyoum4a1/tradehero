package com.tradehero.th.fragments.competition.zone.dto;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneLeaderboardListItemView;
import com.tradehero.th.models.number.THSignedPercentage;

public class CompetitionZoneLeaderboardDTO extends CompetitionZoneDTO
{
    @Nullable public final CompetitionDTO competitionDTO;
    public final int titleColor;
    @NonNull public final Spannable roi;

    //<editor-fold desc="Constructors">
    public CompetitionZoneLeaderboardDTO(
            @NonNull Resources resources,
            @Nullable String title,
            @Nullable String description,
            @Nullable CompetitionDTO competitionDTO)
    {
        super(title,
                description,
                competitionDTO != null ? competitionDTO.getIconUrl() : null,
                R.drawable.default_image);
        this.competitionDTO = competitionDTO;

        Boolean isActive = isActive();
        titleColor = isActive == null || isActive ? CompetitionZoneLeaderboardListItemView.COLOR_ACTIVE : CompetitionZoneLeaderboardListItemView.COLOR_INACTIVE;

        //<editor-fold desc="ROI">
        if (competitionDTO != null
                && competitionDTO.leaderboardUser != null)
        {
            roi = (Spannable) THSignedPercentage
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
        if (competitionDTO == null || competitionDTO.leaderboard == null)
        {
            return null;
        }
        return competitionDTO.leaderboard.isWithinUtcRestricted();
    }
}
