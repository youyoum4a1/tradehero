package com.tradehero.th.fragments.position;

import android.content.Context;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;
import java.util.Map;

public class LeaderboardExpandablePositionItemAdapter extends ExpandablePositionItemAdapter
{
    public LeaderboardExpandablePositionItemAdapter(Context context,
            Map<PositionItemType, Integer> positionItemTypeToLayoutId)
    {
        super(context, positionItemTypeToLayoutId);
    }

    @Override protected ExpandableListItem<PositionDTO> createExpandableItem(PositionDTO dto)
    {
        return new ExpandableLeaderboardPositionItem(dto);
    }

    public static class ExpandableLeaderboardPositionItem extends ExpandableListItem<PositionDTO>
    {
        public ExpandableLeaderboardPositionItem(PositionDTO model)
        {
            super(model);
        }

        @Override public String toString()
        {
            return "ExpandableLeaderboardPositionItem{" +
                    "expanded=" + expanded +
                    ", model=" + model +
                    '}';
        }
    }
}
