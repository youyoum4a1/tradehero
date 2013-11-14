package com.tradehero.th.widget.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionCache;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.PositionUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by julien on 1/11/13
 */
public class PositionPartialBottomInPeriodClosedView extends PositionPartialBottomClosedView
{
    public static final String TAG = PositionPartialBottomClosedView.class.getSimpleName();
    private OwnedLeaderboardPositionId ownedPositionId;

    @Inject protected Lazy<LeaderboardPositionCache> inPeriodPositionCache;

    private DTOCache.Listener<OwnedLeaderboardPositionId, PositionInPeriodDTO> positionCacheListener;
    private DTOCache.GetOrFetchTask<PositionInPeriodDTO> fetchPositionTask;

    private TextView inPeriodPL;
    private TextView inPeriodAdditionalInvested;
    private TextView inPeriodValueAtStart;
    private TextView inPeriodStartValueDate;
    private TextView inPeriodRoiValue;

    //<editor-fold desc="Constructors">
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
    //</editor-fold>

    public void onDestroyView()
    {
        // Nothing to do
    }

    @Override protected void initViews()
    {
        // in period
        inPeriodPL = (TextView) findViewById(R.id.in_period_pl_value);
        inPeriodAdditionalInvested = (TextView) findViewById(R.id.in_period_additional_invested);
        inPeriodValueAtStart = (TextView) findViewById(R.id.in_period_start_value);
        inPeriodStartValueDate = (TextView) findViewById(R.id.in_period_start_value_date);
        inPeriodRoiValue = (TextView) findViewById(R.id.in_period_roi_value);

        super.initViews();
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

    private DTOCache.Listener<OwnedLeaderboardPositionId, PositionInPeriodDTO> createPositionCacheListener()
    {
        return new DTOCache.Listener<OwnedLeaderboardPositionId, PositionInPeriodDTO>()
        {
            @Override public void onDTOReceived(OwnedLeaderboardPositionId key, PositionInPeriodDTO value)
            {
                linkWith(value, true);
            }

            @Override public void onErrorThrown(OwnedLeaderboardPositionId key, Throwable error)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    public void linkWith(PositionInPeriodDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        if (andDisplay)
        {
            display();
        }
    }

    private void displayInPeriod()
    {
        PositionInPeriodDTO positionInPeriodDTO = (PositionInPeriodDTO) positionDTO;

        inPeriodPL.setText(PositionUtils.getInPeriodRealizedPL(getContext(), positionInPeriodDTO));
        PositionUtils.setROIInPeriod(inPeriodRoiValue, positionInPeriodDTO);
        inPeriodAdditionalInvested.setText(PositionUtils.getAdditionalInvested(getContext(), positionInPeriodDTO));
        inPeriodValueAtStart.setText(PositionUtils.getValueAtStart(getContext(), positionInPeriodDTO));

        inPeriodStartValueDate.setText(DateUtils.getDisplayableDate(getContext(), positionInPeriodDTO.latestTradeUtc));
        //inPeriodStartValueDate.setText(String.format());
    }

    public void display()
    {
        displayInPeriod();

        super.display();
    }
}
