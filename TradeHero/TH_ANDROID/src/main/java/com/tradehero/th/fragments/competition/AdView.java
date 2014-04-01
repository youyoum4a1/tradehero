package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneAdvertisementDTO;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by tho on 4/1/2014.
 */
public class AdView extends ImageView
    implements DTOView<CompetitionZoneAdvertisementDTO>
{
    @Inject Lazy<Picasso> picasso;

    //<editor-fold desc="Constructors">
    public AdView(Context context)
    {
        super(context);
    }

    public AdView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AdView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
    }

    @Override public void display(CompetitionZoneAdvertisementDTO competitionZoneAdvertisementDTO)
    {
        if (competitionZoneAdvertisementDTO != null)
        {
            linkWith(competitionZoneAdvertisementDTO.getAdDTO(), true);
        }
    }

    private void linkWith(AdDTO adDTO, boolean andDisplay)
    {
        if (andDisplay)
        {
            picasso.get().load(adDTO.bannerImageUrl)
                    .into(this);
        }
    }
}
