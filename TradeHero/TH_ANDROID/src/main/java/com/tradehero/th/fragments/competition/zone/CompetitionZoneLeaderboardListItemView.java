package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneLeaderboardDTO;

public class CompetitionZoneLeaderboardListItemView extends CompetitionZoneListItemView
{
    public static final int COLOR_ACTIVE = R.color.black;
    public static final int COLOR_INACTIVE = R.color.text_gray_normal;

    protected TextView prizeView;

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

    @Override protected void initViews()
    {
        super.initViews();
        prizeView = (TextView) findViewById(R.id.competition_prize);
    }

    @Override public void linkWith(CompetitionZoneDTO competitionZoneDTO, boolean andDisplay)
    {
        super.linkWith(competitionZoneDTO, andDisplay);
        if (andDisplay)
        {
            displayPrize();
        }
    }

    //<editor-fold desc="Display Methods">
    @Override public void display()
    {
        super.display();
        displayPrize();
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

    public void displayPrize()
    {
        if (prizeView != null)
        {
            if (competitionZoneDTO != null && competitionZoneDTO instanceof CompetitionZoneLeaderboardDTO)
            {
                prizeView.setText(((CompetitionZoneLeaderboardDTO) competitionZoneDTO).competitionDTO.prizeValueWithCcy);
            }
        }
    }
    //</editor-fold>
}
