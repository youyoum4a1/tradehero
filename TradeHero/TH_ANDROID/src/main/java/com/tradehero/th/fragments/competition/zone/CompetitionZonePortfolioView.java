package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZonePortfolioDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneVideoDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneWizardDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class CompetitionZonePortfolioView extends CompetitionZoneListItemView
{
    public static final String TAG = CompetitionZonePortfolioView.class.getSimpleName();

    protected ImageView zoneIcon;
    protected TextView title;
    protected TextView description;

    @Inject protected Picasso picasso;

    //<editor-fold desc="Constructors">
    public CompetitionZonePortfolioView(Context context)
    {
        super(context);
    }

    public CompetitionZonePortfolioView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CompetitionZonePortfolioView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    //<editor-fold desc="Display Methods">
    @Override public void displayIcon()
    {
        super.displayIcon();

        if (zoneIcon != null)
        {
            boolean loaded = false;
            if (competitionZoneDTO instanceof CompetitionZonePortfolioDTO)
            {
                UserProfileCompactDTO profileDTO = ((CompetitionZonePortfolioDTO) competitionZoneDTO).userProfileCompactDTO;
                if (profileDTO != null && profileDTO.largePicture != null)
                {
                    picasso.load(profileDTO.largePicture)
                            .transform(new RoundedShapeTransformation())
                            .into(zoneIcon);
                    loaded = true;
                }
            }

            if (!loaded)
            {
                picasso.load(R.drawable.superman_facebook)
                        .transform(new RoundedShapeTransformation())
                        .into(zoneIcon);
            }
        }
    }
    //</editor-fold>
}
