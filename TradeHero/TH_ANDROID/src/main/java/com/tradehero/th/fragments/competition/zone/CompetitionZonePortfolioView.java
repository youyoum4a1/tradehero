package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.util.AttributeSet;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZonePortfolioDTO;
import com.tradehero.th.models.graphics.TransformationUsage;
import javax.inject.Inject;
import javax.inject.Named;

public class CompetitionZonePortfolioView extends CompetitionZoneListItemView
{
    public static final String TAG = CompetitionZonePortfolioView.class.getSimpleName();

    @Inject @Named(TransformationUsage.USER_PHOTO) protected Transformation zoneIconTransformation;

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
                            .transform(zoneIconTransformation)
                            .into(zoneIcon);
                    loaded = true;
                }
            }

            if (!loaded)
            {
                picasso.load(R.drawable.superman_facebook)
                        .transform(zoneIconTransformation)
                        .into(zoneIcon);
            }
        }
    }
    //</editor-fold>
}
