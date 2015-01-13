package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.utils.DateUtils;

public class PositionPartialBottomClosedView extends AbstractPartialBottomView
{
    @InjectView(R.id.realised_pl_value_header) protected TextView realisedPLValueHeader;
    @InjectView(R.id.realised_pl_value) protected TextView realisedPLValue;
    @InjectView(R.id.roi_value) protected TextView roiValue;
    @InjectView(R.id.total_invested_value) protected TextView totalInvestedValue;
    @InjectView(R.id.opened_date) protected TextView openedDate;
    @InjectView(R.id.closed_date) protected TextView closedDate;
    @InjectView(R.id.period_value) protected TextView periodHeld;

    protected PositionPartialBottomInPeriodViewHolder inPeriodViewHolder;

    //<editor-fold desc="Constructors">
    public PositionPartialBottomClosedView(Context context)
    {
        super(context);
    }

    public PositionPartialBottomClosedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionPartialBottomClosedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        // in period
        inPeriodViewHolder = new PositionPartialBottomInPeriodViewHolder(getContext(), this);
    }

    @Override public void linkWith(ExpandableListItem<PositionDTO> expandableListItem, boolean andDisplay)
    {
        super.linkWith(expandableListItem, andDisplay);
        if (inPeriodViewHolder != null)
        {
            inPeriodViewHolder.linkWith(expandableListItem, andDisplay);
        }
    }

    @Override public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        super.linkWith(positionDTO, andDisplay);
        if (inPeriodViewHolder != null)
        {
            inPeriodViewHolder.linkWith(positionDTO, andDisplay);
        }
        if (andDisplay)
        {
            displayRealisedPLValueHeader();
            displayRealisedPLValue();
            displayRoiValue();
            displayTotalInvested();
            displayOpenedDate();
            displayClosedDate();
            displayPeriodHeld();
        }
    }

    @Override public void displayExpandingPart()
    {
        super.displayExpandingPart();
        if (inPeriodViewHolder != null)
        {
            inPeriodViewHolder.displayInPeriodModelPart();
        }
    }

    @Override public void displayModelPart()
    {
        super.displayModelPart();
        if (inPeriodViewHolder != null)
        {
            inPeriodViewHolder.displayModelPart();
        }
        displayRealisedPLValueHeader();
        displayRealisedPLValue();
        displayRoiValue();
        displayTotalInvested();
        displayOpenedDate();
        displayClosedDate();
        displayPeriodHeld();
    }

    public void displayRealisedPLValueHeader()
    {
        if (realisedPLValueHeader != null)
        {
            if (positionDTO != null && positionDTO.unrealizedPLRefCcy != null && positionDTO.realizedPLRefCcy < 0)
            {
                realisedPLValueHeader.setText(R.string.position_realised_loss_header);
            }
            else
            {
                realisedPLValueHeader.setText(R.string.position_realised_profit_header);
            }
        }
    }

    public void displayRealisedPLValue()
    {
        if (realisedPLValue != null)
        {
            positionDTOUtils.setRealizedPLLook(realisedPLValue, positionDTO);
        }
    }

    public void displayRoiValue()
    {
        positionDTOUtils.setROISinceInception(roiValue, positionDTO);
    }

    public void displayTotalInvested()
    {
        if (totalInvestedValue != null)
        {
            if (positionDTO != null)
            {
                totalInvestedValue.setText(positionDTOUtils.getSumInvested(getResources(), positionDTO));
            }
        }
    }

    public void displayOpenedDate()
    {
        if (openedDate != null && positionDTO.earliestTradeUtc != null)
        {
            openedDate.setText(DateUtils.getDisplayableDate(getResources(), positionDTO.earliestTradeUtc));
        }
    }

    public void displayClosedDate()
    {
        if (closedDate != null)
        {
            closedDate.setText(DateUtils.getDisplayableDate(getResources(), positionDTO.latestTradeUtc));
        }
    }

    public void displayPeriodHeld()
    {
        if (periodHeld != null)
        {
            if (positionDTO != null && positionDTO.earliestTradeUtc != null && positionDTO.latestTradeUtc != null)
            {
                int nDays = DateUtils.getNumberOfDaysBetweenDates(positionDTO.earliestTradeUtc, positionDTO.latestTradeUtc);
                String s = getResources().getQuantityString(R.plurals.position_period_held_day, nDays, nDays);
                periodHeld.setText(s);
            }
        }
    }
}
