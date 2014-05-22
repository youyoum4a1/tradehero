package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneVideoDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneWizardDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class CompetitionZoneListItemView extends AbstractCompetitionZoneListItemView
{
    protected ImageView zoneIcon;
    protected TextView title;
    protected TextView description;

    @Inject protected Picasso picasso;

    //<editor-fold desc="Constructors">
    public CompetitionZoneListItemView(Context context)
    {
        super(context);
    }

    public CompetitionZoneListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CompetitionZoneListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
        DaggerUtils.inject(this);
    }

    protected void initViews()
    {
        zoneIcon = (ImageView) findViewById(R.id.icn_competition_zone);
        title = (TextView) findViewById(R.id.competition_zone_title);
        description = (TextView) findViewById(R.id.competition_zone_description);
    }

    public void linkWith(CompetitionZoneDTO competitionZoneDTO, boolean andDisplay)
    {
        super.linkWith(competitionZoneDTO, andDisplay);

        if (andDisplay)
        {
            displayIcon();
            displayTitle();
            displayDescription();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayIcon();
        displayTitle();
        displayDescription();
    }

    public void displayIcon()
    {
        if (zoneIcon != null)
        {
            if (competitionZoneDTO instanceof CompetitionZoneWizardDTO)
            {
                zoneIcon.setImageResource(R.drawable.wizard);
            }
            else if (competitionZoneDTO instanceof CompetitionZoneVideoDTO)
            {
                zoneIcon.setImageResource(R.drawable.icn_info);
            }
            else if (competitionZoneDTO != null)
            {
                // TODO
            }
        }
    }

    public void displayTitle()
    {
        TextView titleCopy = this.title;
        if (titleCopy != null)
        {
            titleCopy.setText(competitionZoneDTO.title);
        }
    }

    public void displayDescription()
    {
        TextView descriptionCopy = this.description;
        if (descriptionCopy != null)
        {
            if (competitionZoneDTO != null)
            {
                descriptionCopy.setText(competitionZoneDTO.description);
            }
            descriptionCopy.setVisibility(competitionZoneDTO == null ||
                    competitionZoneDTO.description == null ||
                    competitionZoneDTO.description.length() == 0 ? GONE : VISIBLE);
        }
    }
    //</editor-fold>
}
