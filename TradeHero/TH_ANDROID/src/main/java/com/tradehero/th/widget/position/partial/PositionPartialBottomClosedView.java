package com.tradehero.th.widget.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.PositionUtils;
import dagger.Lazy;

import javax.inject.Inject;

/**
 * Created by julien on 30/10/13
 */
public class PositionPartialBottomClosedView extends RelativeLayout
{
    public static final String TAG = PositionPartialBottomClosedView.class.getSimpleName();

    private OwnedPositionId ownedPositionId;
    private PositionDTO positionDTO;

    @Inject Lazy<PositionCache> positionCache;

    private TextView realisedPLValue;
    private TextView totalInvestedValue;
    private TextView openedDate;
    private TextView closedDate;
    private TextView periodHeld;

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
        DaggerUtils.inject(this);
        initViews();
    }

    protected void initViews()
    {
        realisedPLValue = (TextView) findViewById(R.id.position_realized_pl_value);
        totalInvestedValue = (TextView) findViewById(R.id.total_invested_value);
        openedDate = (TextView) findViewById(R.id.opened_date);
        closedDate = (TextView) findViewById(R.id.closed_date);
        periodHeld = (TextView) findViewById(R.id.period_value);
    }

    public void onDestroyView()
    {
        // Nothing to do
    }

    public void linkWith(OwnedPositionId ownedPositionId, boolean andDisplay)
    {
        this.ownedPositionId = ownedPositionId;

        linkWith(positionCache.get().get(this.ownedPositionId), andDisplay);
    }

    public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        if (andDisplay)
        {
            display();
        }
    }

    public void display()
    {
        displayRealisedPLValue();
        displayTotalInvested();
        displayOpenedDate();
        displayClosedDate();
        displayPeriodHeld();
    }

    public void displayRealisedPLValue()
    {
        if (realisedPLValue != null)
        {
            realisedPLValue.setText(PositionUtils.getRealizedPL(getContext(), positionDTO));
        }
    }

    private void displayTotalInvested()
    {
        if (totalInvestedValue != null)
        {
            totalInvestedValue.setText(PositionUtils.getSumInvested(getContext(), positionDTO));
        }
    }

    private void displayOpenedDate()
    {
        if (openedDate != null)
        {
            openedDate.setText(DateUtils.getDisplayableDate(getContext(), positionDTO.earliestTradeUtc));
        }
    }

    private void displayClosedDate()
    {
        if (closedDate != null)
        {
            closedDate.setText(DateUtils.getDisplayableDate(getContext(), positionDTO.latestTradeUtc));
        }
    }

    private void displayPeriodHeld()
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
