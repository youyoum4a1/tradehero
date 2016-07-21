package com.androidth.general.fragments.competition.zone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.androidth.general.R;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.androidth.general.inject.HierarchyInjector;
import javax.inject.Inject;

public class CompetitionZoneListItemView extends AbstractCompetitionZoneListItemView
{
    @BindView(R.id.icn_competition_zone) protected ImageView zoneIcon;
    @BindView(R.id.competition_zone_title) protected TextView title;
    @BindView(R.id.competition_zone_description) protected TextView description;

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
        ButterKnife.bind(this);
        HierarchyInjector.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        picasso.cancelRequest(zoneIcon);
        super.onDetachedFromWindow();
    }

    //<editor-fold desc="Display Methods">

    @Override public void display(@NonNull CompetitionZoneDTO competitionZoneDTO)
    {
        super.display(competitionZoneDTO);
        if (zoneIcon != null)
        {
            picasso.cancelRequest(zoneIcon);
            RequestCreator request;
            if (competitionZoneDTO.zoneIconUrl != null)
            {
                request = picasso.load(competitionZoneDTO.zoneIconUrl);

            }
            else
            {
                request = picasso.load(competitionZoneDTO.zoneIconResId);
            }
            prepareZoneIcon(request).into(zoneIcon);
        }
        if (title != null)
        {
            title.setText(competitionZoneDTO.title);
        }
        if (description != null)
        {
            description.setVisibility(competitionZoneDTO.descriptionVisibility);
            description.setText(competitionZoneDTO.description);
        }
    }

    @NonNull protected RequestCreator prepareZoneIcon(@NonNull RequestCreator request)
    {
        return request.fit().centerInside();
    }

    //</editor-fold>
}
