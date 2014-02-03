package com.tradehero.th.fragments.position;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomInPeriodClosedView;

/**
 * Created by julien on 1/11/13
 */
public class PositionInPeriodClosedView extends AbstractPositionClosedView<PositionInPeriodDTO>
{
    public static final String TAG = PositionInPeriodClosedView.class.getSimpleName();

    private PositionPartialBottomInPeriodClosedView bottomView;

    //<editor-fold desc="Constructors">
    public PositionInPeriodClosedView(Context context)
    {
        super(context);
    }

    public PositionInPeriodClosedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionInPeriodClosedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void initViews()
    {
        super.initViews();
        bottomView = (PositionPartialBottomInPeriodClosedView) findViewById(R.id.expanding_layout);
    }

    @Override public void linkWith(PositionInPeriodDTO positionDTO, boolean andDisplay)
    {
        super.linkWith(positionDTO, andDisplay);
        this.bottomView.linkWith(positionDTO, andDisplay);
    }

    public void linkWith(LeaderboardPositionItemAdapter.ExpandableLeaderboardPositionItem item, boolean andDisplay)
    {
        super.linkWith(item.getModel(), andDisplay);
        this.bottomView.linkWith(item, andDisplay);
    }
}
