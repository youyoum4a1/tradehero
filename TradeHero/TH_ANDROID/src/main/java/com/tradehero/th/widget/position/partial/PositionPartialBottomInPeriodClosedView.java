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
    public static final String TAG = PositionPartialBottomClosedView.class.getSimpleName();
    private OwnedLeaderboardPositionId ownedPositionId;

    @Inject protected Lazy<LeaderboardPositionCache> inPeriodPositionCache;

    private DTOCache.Listener<OwnedLeaderboardPositionId, InPeriodPositionDTO> positionCacheListener;
    private DTOCache.GetOrFetchTask<InPeriodPositionDTO> fetchPositionTask;

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

    private void displayInPeriod()
    {
        InPeriodPositionDTO inPeriodPositionDTO = (InPeriodPositionDTO) positionDTO;

        inPeriodPL.setText(PositionUtils.getInPeriodRealizedPL(getContext(), inPeriodPositionDTO));
        PositionUtils.setROIInPeriod(inPeriodRoiValue, inPeriodPositionDTO);
        inPeriodAdditionalInvested.setText(PositionUtils.getAdditionalInvested(getContext(), inPeriodPositionDTO));
        inPeriodValueAtStart.setText(PositionUtils.getValueAtStart(getContext(), inPeriodPositionDTO));

        inPeriodStartValueDate.setText(DateUtils.getDisplayableDate(getContext(), inPeriodPositionDTO.latestTradeUtc));
        //inPeriodStartValueDate.setText(String.format());
    }

    public void display()
    {
        displayInPeriod();

        super.display();
    }
}
