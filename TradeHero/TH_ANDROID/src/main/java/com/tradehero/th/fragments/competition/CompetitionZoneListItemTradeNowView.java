package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneTradeNowDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by xavier on 1/17/14.
 */
public class CompetitionZoneListItemTradeNowView extends AbstractCompetitionZoneListItemView
{
    public static final String TAG = CompetitionZoneListItemTradeNowView.class.getSimpleName();

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
                picasso.load(((CompetitionZoneTradeNowDTO) competitionZoneDTO).imageUrl)
                             .into(tradeNowIcon);
            }
        }
    }
}
