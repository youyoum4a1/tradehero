package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.fragments.position.LeaderboardPositionItemAdapter;

public class PositionPartialBottomInPeriodOpenView
        extends AbstractPositionPartialBottomOpenView<
        PositionDTO,
            LeaderboardPositionItemAdapter.ExpandableLeaderboardPositionItem>
{
    private PositionPartialBottomInPeriodViewHolder inPeriodViewHolder;

    //<editor-fold desc="Constructors">
    public PositionPartialBottomInPeriodOpenView(Context context)
    {
        super(context);
    }

    public PositionPartialBottomInPeriodOpenView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionPartialBottomInPeriodOpenView(Context context, AttributeSet attrs, int defStyle)
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

    @Override public void linkWith(LeaderboardPositionItemAdapter.ExpandableLeaderboardPositionItem expandableListItem, boolean andDisplay)
    {
        super.linkWith(expandableListItem, andDisplay);
        if (inPeriodViewHolder != null)
        {
            inPeriodViewHolder.linkWith(expandableListItem, andDisplay);
        }
        if (andDisplay)
        {
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
    }
}
