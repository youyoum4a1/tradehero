package com.androidth.general.fragments.competition.zone;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneLeaderboardDTO;

import butterknife.Bind;

public class CompetitionZoneLeaderboardListItemView extends CompetitionZoneListItemView
{

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

    @Override public void display(@NonNull CompetitionZoneDTO competitionZoneDTO) {
        super.display(competitionZoneDTO);
        if (competitionZoneDTO instanceof CompetitionZoneLeaderboardDTO) {
            CompetitionZoneLeaderboardDTO dto = (CompetitionZoneLeaderboardDTO) competitionZoneDTO;
            if (title != null) {
                if (dto.isActive())
                    title.setTextColor(Color.BLACK);
                else title.setTextColor(Color.GRAY);
                //title.setTextColor(dto.titleColor);
            }
            if (roiView != null) {
                roiView.setText(dto.roi);
            }
        }
    }
}
