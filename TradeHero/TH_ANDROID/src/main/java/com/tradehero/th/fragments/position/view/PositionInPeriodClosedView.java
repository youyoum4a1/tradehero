package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.fragments.position.LeaderboardPositionItemAdapter;

public class PositionInPeriodClosedView extends AbstractPositionView<
        PositionDTO,
        LeaderboardPositionItemAdapter.ExpandableLeaderboardPositionItem>
{
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
}
