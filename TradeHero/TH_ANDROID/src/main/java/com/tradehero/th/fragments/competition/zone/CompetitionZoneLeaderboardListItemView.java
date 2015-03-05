package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneLeaderboardDTO;
import com.tradehero.th.models.number.THSignedPercentage;

public class CompetitionZoneLeaderboardListItemView extends CompetitionZoneListItemView
{
    public static final int COLOR_ACTIVE = R.color.text_primary;
    public static final int COLOR_INACTIVE = R.color.text_secondary;

    @InjectView(R.id.competition_roi) protected TextView roiView;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public CompetitionZoneLeaderboardListItemView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CompetitionZoneLeaderboardListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CompetitionZoneLeaderboardListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(CompetitionZoneDTO competitionZoneDTO)
    {
        super.display(competitionZoneDTO);
        displayROI();
    }

    //<editor-fold desc="Display Methods">
    @Override public void display()
    {
        super.display();
        displayROI();
    }

    public void displayIcon()
    {
        if (zoneIcon != null)
        {
            if (competitionZoneDTO != null && competitionZoneDTO instanceof CompetitionZoneLeaderboardDTO)
            {
                CompetitionZoneLeaderboardDTO zoneLeaderboard = (CompetitionZoneLeaderboardDTO) competitionZoneDTO;
                if (zoneLeaderboard.competitionDTO != null)
                {
                    String iconUrl = zoneLeaderboard.competitionDTO.getIconUrl();
                    if (iconUrl != null)
                    {
                        picasso.load(iconUrl).into(zoneIcon);
                    }
                }
            }
        }
    }

    @Override public void displayTitle()
    {
        super.displayTitle();
        if (title != null)
        {
            title.setTextColor(getResources().getColor(getTitleColorResId()));
        }
    }

    @ColorRes public int getTitleColorResId()
    {
        Boolean isActive = isActive();
        return isActive == null || isActive ? COLOR_ACTIVE : COLOR_INACTIVE;
    }

    @Nullable public Boolean isActive()
    {
        if (competitionZoneDTO == null)
        {
            return null;
        }
        return ((CompetitionZoneLeaderboardDTO) competitionZoneDTO).isActive();
    }

    public void displayROI()
    {
        if (roiView != null)
        {
            if (competitionZoneDTO != null && competitionZoneDTO instanceof CompetitionZoneLeaderboardDTO)
            {
                LeaderboardUserDTO leaderboardUserDTO = ((CompetitionZoneLeaderboardDTO) competitionZoneDTO).competitionDTO.leaderboardUser;
                if(leaderboardUserDTO != null)
                {
                    THSignedPercentage
                            .builder(leaderboardUserDTO.roiInPeriod * 100)
                            .withDefaultColor()
                            .build()
                            .into(roiView);
                }
                else
                {
                    roiView.setTextColor(getResources().getColor(R.color.text_primary));
                    roiView.setText(R.string.na);
                }
            }
        }
    }
    //</editor-fold>
}
