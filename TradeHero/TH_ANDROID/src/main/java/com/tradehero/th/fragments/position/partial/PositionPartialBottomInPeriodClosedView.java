package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.fragments.position.LeaderboardPositionItemAdapter;

/**
 * Created by julien on 1/11/13
 */
public class PositionPartialBottomInPeriodClosedView
        extends AbstractPositionPartialBottomClosedView<
            PositionInPeriodDTO,
            LeaderboardPositionItemAdapter.ExpandableLeaderboardPositionItem>
{
    public static final String TAG = PositionPartialBottomInPeriodClosedView.class.getSimpleName();

    private PositionPartialBottomInPeriodViewHolder inPeriodViewHolder;

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

    @Override protected void initViews()
    {
        // overall
        super.initViews();

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

    @Override public void linkWith(PositionInPeriodDTO positionDTO, boolean andDisplay)
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

    @Override public void linkWith(PortfolioDTO portfolioDTO, boolean andDisplay)
    {
        super.linkWith(portfolioDTO, andDisplay);
        if (inPeriodViewHolder != null)
        {
            inPeriodViewHolder.linkWith(portfolioDTO, andDisplay);
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
