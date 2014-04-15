package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.PositionUtils;
import javax.inject.Inject;

/**
 * Created by julien on 30/10/13
 */
abstract public class AbstractPositionPartialBottomClosedView<
            PositionDTOType extends PositionDTO,
            ExpandableListItemType extends ExpandableListItem<PositionDTOType>
            >
        extends AbstractPartialBottomView<PositionDTOType, ExpandableListItemType>
{
    public static final String TAG = AbstractPositionPartialBottomClosedView.class.getSimpleName();

    private TextView realisedPLValue;
    protected TextView roiValue;
    private TextView totalInvestedValue;
    private TextView openedDate;
    private TextView closedDate;
    private TextView periodHeld;

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

    @Override protected void initViews()
    {
        super.initViews();

        // overall
        realisedPLValue = (TextView) findViewById(R.id.position_realized_pl_value);
        roiValue = (TextView) findViewById(R.id.roi_value);
        totalInvestedValue = (TextView) findViewById(R.id.total_invested_value);
        openedDate = (TextView) findViewById(R.id.opened_date);
        closedDate = (TextView) findViewById(R.id.closed_date);
        periodHeld = (TextView) findViewById(R.id.period_value);
    }

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
            if (positionDTO != null)
            {
                realisedPLValue.setText(positionUtils.getRealizedPL(getContext(), positionDTO));
            }
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
                totalInvestedValue.setText(positionUtils.getSumInvested(getContext(), positionDTO));
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
