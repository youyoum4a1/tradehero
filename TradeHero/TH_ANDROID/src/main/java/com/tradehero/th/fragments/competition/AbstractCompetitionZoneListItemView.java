package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneDTO;

/**
 * Created by xavier on 1/17/14.
 */
abstract public class AbstractCompetitionZoneListItemView extends RelativeLayout implements DTOView<CompetitionZoneDTO>
{
    public static final String TAG = AbstractCompetitionZoneListItemView.class.getSimpleName();

    protected CompetitionZoneDTO competitionZoneDTO;

    //<editor-fold desc="Constructors">
    public AbstractCompetitionZoneListItemView(Context context)
    {
        super(context);
    }

    public AbstractCompetitionZoneListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AbstractCompetitionZoneListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    public void display(CompetitionZoneDTO competitionZoneDTO)
    {
        THLog.d(TAG, "display");
        linkWith(competitionZoneDTO, true);
    }

    public void linkWith(CompetitionZoneDTO competitionZoneDTO, boolean andDisplay)
    {
        this.competitionZoneDTO = competitionZoneDTO;

        if (andDisplay)
        {
        }
    }

    public CompetitionZoneDTO getCompetitionZoneDTO()
    {
        return competitionZoneDTO;
    }
}
