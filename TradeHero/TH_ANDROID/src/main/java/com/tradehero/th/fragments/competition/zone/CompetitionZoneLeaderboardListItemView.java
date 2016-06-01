package com.ayondo.academy.fragments.competition.zone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.Bind;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.ayondo.academy.fragments.competition.zone.dto.CompetitionZoneLeaderboardDTO;

public class CompetitionZoneLeaderboardListItemView extends CompetitionZoneListItemView
{
    public static final int COLOR_ACTIVE = R.color.text_primary;
    public static final int COLOR_INACTIVE = R.color.text_secondary;

    @Bind(R.id.competition_roi) protected TextView roiView;

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

    @Override public void display(@NonNull CompetitionZoneDTO competitionZoneDTO)
    {
        super.display(competitionZoneDTO);
        CompetitionZoneLeaderboardDTO dto = (CompetitionZoneLeaderboardDTO) competitionZoneDTO;
        if (title != null)
        {
            title.setTextColor(dto.titleColor);
        }
        if (roiView != null)
        {
            roiView.setText(dto.roi);
        }
    }
}
