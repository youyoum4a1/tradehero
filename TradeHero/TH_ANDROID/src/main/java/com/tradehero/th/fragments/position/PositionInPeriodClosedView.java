package com.tradehero.th.fragments.position;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.position.PositionInPeriodDTO;

/**
 * Created by julien on 1/11/13
 */
public class PositionInPeriodClosedView extends AbstractPositionView<
            PositionInPeriodDTO,
            LeaderboardPositionItemAdapter.ExpandableLeaderboardPositionItem>
{
    public static final String TAG = PositionInPeriodClosedView.class.getSimpleName();

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

    public boolean isShowingTopRoiValue()
    {
        return expandableListItem == null || !expandableListItem.isExpanded();
    }
}
