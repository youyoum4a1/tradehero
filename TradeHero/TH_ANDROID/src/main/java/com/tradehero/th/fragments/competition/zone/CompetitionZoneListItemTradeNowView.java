package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.tradehero.thm.R;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneTradeNowDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class CompetitionZoneListItemTradeNowView extends AbstractCompetitionZoneListItemView
{
    private ImageView tradeNowIcon;
    @Inject Picasso picasso;

    //<editor-fold desc="Constructors">
    public CompetitionZoneListItemTradeNowView(Context context)
    {
        super(context);
    }

    public CompetitionZoneListItemTradeNowView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CompetitionZoneListItemTradeNowView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        tradeNowIcon = (ImageView) findViewById(R.id.ic_competition_zone_trade_now);
        DaggerUtils.inject(this);
    }

    public void linkWith(CompetitionZoneDTO competitionZoneDTO, boolean andDisplay)
    {
        super.linkWith(competitionZoneDTO, andDisplay);

        if (andDisplay)
        {
            displayTradeNowIcon();
        }
    }

    public void displayTradeNowIcon()
    {
        if (tradeNowIcon != null)
        {
            if (competitionZoneDTO != null)
            {
                CompetitionZoneTradeNowDTO tradeNowDTO = (CompetitionZoneTradeNowDTO) competitionZoneDTO;
                if (tradeNowDTO.imageResId > 0)
                {
                    tradeNowIcon.setImageResource(tradeNowDTO.imageResId);
                }
                else
                {
                    picasso.load(tradeNowDTO.imageUrl)
                            .into(tradeNowIcon);
                }
            }
        }
    }
}
