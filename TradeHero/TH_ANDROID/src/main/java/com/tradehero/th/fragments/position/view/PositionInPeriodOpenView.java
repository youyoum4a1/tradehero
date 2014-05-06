package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.fragments.position.LeaderboardPositionItemAdapter;

public class PositionInPeriodOpenView extends AbstractPositionView<
            PositionInPeriodDTO,
        LeaderboardPositionItemAdapter.ExpandableLeaderboardPositionItem>
{
    //<editor-fold desc="Constructors">
    public PositionInPeriodOpenView(Context context)
    {
        super(context);
    }

    public PositionInPeriodOpenView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionInPeriodOpenView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>
}
