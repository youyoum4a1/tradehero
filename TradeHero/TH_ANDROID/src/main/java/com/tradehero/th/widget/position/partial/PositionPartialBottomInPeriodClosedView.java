package com.tradehero.th.widget.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import com.tradehero.th.api.position.InPeriodPositionDTO;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.PositionUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by julien on 1/11/13
 */
public class PositionPartialBottomInPeriodClosedView extends PositionPartialBottomClosedView
{
    private OwnedLeaderboardPositionId ownedPositionId;
    private InPeriodPositionDTO positionDTO;

    @Inject protected Lazy<LeaderboardPositionCache> inPeriodPositionCache;

    private TextView realisedPLValue;
    private TextView totalInvestedValue;
    private TextView openedDate;
    private TextView closedDate;
    private TextView periodHeld;

    private DTOCache.Listener<OwnedLeaderboardPositionId, InPeriodPositionDTO> positionCacheListener;
    private DTOCache.GetOrFetchTask<InPeriodPositionDTO> fetchPositionTask;


    public PositionPartialBottomInPeriodClosedView(Context context)
    {
        super(context);
    }

    public PositionPartialBottomInPeriodClosedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionPartialBottomInPeriodClosedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    public static final String TAG = PositionPartialBottomClosedView.class.getSimpleName();

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

    public void linkWith(OwnedLeaderboardPositionId ownedPositionId, boolean andDisplay)
    {
        this.ownedPositionId = ownedPositionId;

        if (positionCacheListener == null)
        {
            positionCacheListener = createPositionCacheListener();
        }

        fetchPositionTask = inPeriodPositionCache.get().getOrFetch(this.ownedPositionId, false, positionCacheListener);
        fetchPositionTask.execute();
    }

    private DTOCache.Listener<OwnedLeaderboardPositionId, InPeriodPositionDTO> createPositionCacheListener()
    {
        return new DTOCache.Listener<OwnedLeaderboardPositionId, InPeriodPositionDTO>()
        {
            @Override public void onDTOReceived(OwnedLeaderboardPositionId key, InPeriodPositionDTO value)
            {
                linkWith(value, true);
            }

            @Override public void onErrorThrown(OwnedLeaderboardPositionId key, Throwable error)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    public void linkWith(InPeriodPositionDTO positionDTO, boolean andDisplay)
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
        if (openedDate != null && positionDTO.earliestTradeUtc != null)
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
