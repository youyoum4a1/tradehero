package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDisplayCellDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZonePreSeasonDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneVideoDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneWizardDTO;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;

public class CompetitionZoneListItemView extends AbstractCompetitionZoneListItemView
{
    @InjectView(R.id.icn_competition_zone) protected ImageView zoneIcon;
    @InjectView(R.id.competition_zone_title) protected TextView title;
    @InjectView(R.id.competition_zone_description) protected TextView description;

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
        ButterKnife.inject(this);
        HierarchyInjector.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        picasso.cancelRequest(zoneIcon);
        super.onDetachedFromWindow();
    }

    //<editor-fold desc="Display Methods">
    @Override public void display(CompetitionZoneDTO competitionZoneDTO)
    {
        super.display(competitionZoneDTO);
        displayIcon();
        displayTitle();
        displayDescription();
    }

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
                CompetitionZoneWizardDTO competitionZoneWizardDTO = ((CompetitionZoneWizardDTO) competitionZoneDTO);
                if (competitionZoneWizardDTO.getIconUrl() != null)
                {
                    picasso.cancelRequest(zoneIcon);
                    picasso.load(competitionZoneWizardDTO.getIconUrl())
                            .fit()
                            .centerInside()
                            .into(zoneIcon);
                }
                else
                {
                    zoneIcon.setImageResource(R.drawable.wizard);
                }
            }
            else if (competitionZoneDTO instanceof CompetitionZoneVideoDTO)
            {
                zoneIcon.setImageResource(R.drawable.ic_action_action_about);
            }
            else if (competitionZoneDTO instanceof CompetitionZoneDisplayCellDTO)
            {
                CompetitionZoneDisplayCellDTO displayCellDTO = (CompetitionZoneDisplayCellDTO) competitionZoneDTO;
                String iconUrl = displayCellDTO.getIconUrl();
                if (iconUrl != null && !iconUrl.isEmpty())
                {
                    picasso.cancelRequest(zoneIcon);
                    picasso.load(iconUrl)
                            .fit()
                            .centerInside()
                            .into(zoneIcon);
                }
            }
            else if (competitionZoneDTO instanceof CompetitionZonePreSeasonDTO)
            {
                CompetitionZonePreSeasonDTO preSeasonDTO = (CompetitionZonePreSeasonDTO) competitionZoneDTO;
                String iconUrl = preSeasonDTO.iconUrl;
                if (iconUrl != null && !iconUrl.isEmpty())
                {
                    picasso.cancelRequest(zoneIcon);
                    picasso.load(iconUrl)
                            .fit()
                            .centerInside()
                            .into(zoneIcon);
                }
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
