package com.tradehero.th.fragments.position;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionInPeriodDTO;

/** Created with IntelliJ IDEA. User: tho Date: 11/6/13 Time: 8:11 PM Copyright (c) TradeHero */
public class LeaderboardPositionItemAdapter extends AbstractPositionItemAdapter<PositionInPeriodDTO>
{
    private final boolean isTimeRestricted;

    public LeaderboardPositionItemAdapter(
            Context context,
            LayoutInflater inflater,
            int headerLayoutId,
            int lockedPositionLayoutId,
            int openPositionLayoutId,
            int closedPositionLayoutId,
            int positionNothingId,
            boolean isTimeRestricted)
    {
        super(context,
                inflater,
                headerLayoutId,
                lockedPositionLayoutId,
                openPositionLayoutId,
                closedPositionLayoutId,
                positionNothingId);
        this.isTimeRestricted = isTimeRestricted;
    }

    @Override protected ExpandableListItem<PositionInPeriodDTO> createExpandableItem(PositionInPeriodDTO dto)
    {
        return new ExpandableLeaderboardPositionItem(dto);
    }

    public static class ExpandableLeaderboardPositionItem extends ExpandableListItem<PositionInPeriodDTO>
    {
        protected boolean timeRestricted;

        public ExpandableLeaderboardPositionItem(PositionInPeriodDTO model)
        {
            super(model);
        }

        public boolean isTimeRestricted()
        {
            return timeRestricted;
        }

        public void setTimeRestricted(boolean timeRestricted)
        {
            this.timeRestricted = timeRestricted;
        }

        @Override public String toString()
        {
            return "ExpandableLeaderboardPositionItem{" +
                    "expanded=" + expanded +
                    ", model=" + model +
                    ", timeRestricted=" + timeRestricted +
                    '}';
        }
    }
}
