package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.utils.DateUtils;

abstract public class AbstractPositionPartialBottomClosedView<
            PositionDTOType extends PositionDTO,
            ExpandableListItemType extends ExpandableListItem<PositionDTOType>
            >
        extends AbstractPartialBottomView<PositionDTOType, ExpandableListItemType>
{
    @InjectView(R.id.position_realized_pl_value) protected TextView realisedPLValue;
    @InjectView(R.id.roi_value) protected TextView roiValue;
    @InjectView(R.id.total_invested_value) protected TextView totalInvestedValue;
    @InjectView(R.id.opened_date) protected TextView openedDate;
    @InjectView(R.id.closed_date) protected TextView closedDate;
    @InjectView(R.id.period_value) protected TextView periodHeld;

    //<editor-fold desc="Constructors">
    public AbstractPositionPartialBottomClosedView(Context context)
    {
        super(context);
    }

    public AbstractPositionPartialBottomClosedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AbstractPositionPartialBottomClosedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void linkWith(PositionDTOType positionDTO, boolean andDisplay)
    {
        super.linkWith(positionDTO, andDisplay);
        if (andDisplay)
        {
            displayRealisedPLValue();
            displayRoiValue();
            displayTotalInvested();
            displayOpenedDate();
            displayClosedDate();
            displayPeriodHeld();
        }
    }

    @Override public void displayModelPart()
    {
        super.displayModelPart();
        displayRealisedPLValue();
        displayRoiValue();
        displayTotalInvested();
        displayOpenedDate();
        displayClosedDate();
        displayPeriodHeld();
    }

    public void displayRealisedPLValue()
    {
        if (realisedPLValue != null)
        {
            positionUtils.setRealizedPLLook(realisedPLValue, positionDTO);
        }
    }

    public void displayRoiValue()
    {
        positionUtils.setROISinceInception(roiValue, positionDTO);
    }

    public void displayTotalInvested()
    {
        if (totalInvestedValue != null)
        {
            if (positionDTO != null)
            {
                totalInvestedValue.setText(positionUtils.getSumInvested(getResources(), positionDTO));
            }
        }
    }

    public void displayOpenedDate()
    {
        if (openedDate != null && positionDTO.earliestTradeUtc != null)
        {
            openedDate.setText(DateUtils.getDisplayableDate(getContext(), positionDTO.earliestTradeUtc));
        }
    }

    public void displayClosedDate()
    {
        if (closedDate != null)
        {
            closedDate.setText(DateUtils.getDisplayableDate(getContext(), positionDTO.latestTradeUtc));
        }
    }

    public void displayPeriodHeld()
    {
        if (periodHeld != null)
        {
            if (positionDTO != null && positionDTO.earliestTradeUtc != null && positionDTO.latestTradeUtc != null)
            {
                int nDays = DateUtils.getNumberOfDaysBetweenDates(positionDTO.earliestTradeUtc, positionDTO.latestTradeUtc);
                String s;
                if (nDays > 1 )
                {
                    s = String.format(getContext().getString(R.string.position_period_held_day_plural), nDays);
                }
                else
                {
                    s = String.format(getContext().getString(R.string.position_period_held_day_singular), nDays);
                }
                periodHeld.setText(s);
            }
        }
    }
}
