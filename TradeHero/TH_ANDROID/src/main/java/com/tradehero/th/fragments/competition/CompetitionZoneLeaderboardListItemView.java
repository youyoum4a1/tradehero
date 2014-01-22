package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneLeaderboardDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class CompetitionZoneLeaderboardListItemView extends CompetitionZoneListItemView
{
    public static final String TAG = CompetitionZoneLeaderboardListItemView.class.getSimpleName();

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
    //</editor-fold>
}
