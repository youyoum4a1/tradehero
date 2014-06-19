package com.tradehero.th.fragments.position;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;
import java.util.Map;

public class LeaderboardPositionItemAdapter extends AbstractPositionItemAdapter
{
    private final boolean isTimeRestricted;

    public LeaderboardPositionItemAdapter(Context context,
            Map<PositionItemType, Integer> positionItemTypeToLayoutId,
            boolean isTimeRestricted)
    {
        super(context, positionItemTypeToLayoutId);
        this.isTimeRestricted = isTimeRestricted;
    }

    @Override protected ExpandableListItem<PositionDTO> createExpandableItem(PositionDTO dto)
    {
        return new ExpandableLeaderboardPositionItem(dto);
    }

    public static class ExpandableLeaderboardPositionItem extends ExpandableListItem<PositionDTO>
    {
        protected boolean timeRestricted;

        public ExpandableLeaderboardPositionItem(PositionDTO model)
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
                    ", timeRestricted=" + timeRestricted +
                    ", model=" + model +
                    '}';
        }
    }
}
