package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneLeaderboardDTO;

public class CompetitionZoneLeaderboardListItemView extends CompetitionZoneListItemView
{
    public static final String TAG = CompetitionZoneLeaderboardListItemView.class.getSimpleName();
    public static final int COLOR_ACTIVE = R.color.black;
    public static final int COLOR_INACTIVE = R.color.text_gray_normal;

    //<editor-fold desc="Constructors">
    public CompetitionZoneLeaderboardListItemView(Context context)
    {
        super(context);
    }

    public CompetitionZoneLeaderboardListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CompetitionZoneLeaderboardListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    //<editor-fold desc="Display Methods">
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

    public int getTitleColorResId()
    {
        Boolean isActive = isActive();
        return isActive == null || isActive ? COLOR_ACTIVE : COLOR_INACTIVE;
    }

    public Boolean isActive()
    {
        if (competitionZoneDTO == null)
        {
            return null;
        }
        return ((CompetitionZoneLeaderboardDTO) competitionZoneDTO).isActive();
    }
    //</editor-fold>
}
