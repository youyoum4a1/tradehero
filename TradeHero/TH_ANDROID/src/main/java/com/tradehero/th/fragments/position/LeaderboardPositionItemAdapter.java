package com.tradehero.th.fragments.position;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
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

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            int layoutToInflate = getLayoutForPosition(position);
            convertView = inflater.inflate(layoutToInflate, parent, false);
        }

        if (isPositionHeaderOpen(position))
        {
            ((PositionSectionHeaderItemView) convertView).setHeaderTextContent(getHeaderText(true));
        }
        else if (isPositionHeaderClosed(position))
        {
            ((PositionSectionHeaderItemView) convertView).setHeaderTextContent(getHeaderText(false));
        }
        else if (isOpenPosition(position) && getOpenPositionsCount() == 0)
        {
            // placeholder, nothing to configure
        }
        else
        {
            ExpandableLeaderboardPositionItem expandableWrapper = (ExpandableLeaderboardPositionItem) getItem(position);

            if (expandableWrapper.getModel().isLocked())
            {
                LockedPositionItem cell = (LockedPositionItem) convertView;
                cell.linkWith(expandableWrapper.getModel(), true);
            }
            else
            {
                View expandingLayout = convertView.findViewById(R.id.expanding_layout);
                if (expandingLayout != null)
                {
                    if (!expandableWrapper.isExpanded())
                    {
                        expandingLayout.setVisibility(View.GONE);
                    }
                    else
                    {
                        expandingLayout.setVisibility(View.VISIBLE);
                    }
                }

                bindCell(convertView, expandableWrapper);
            }
        }
        setLatestView(convertView);
        return convertView;
    }

    private void bindCell(View convertView, ExpandableLeaderboardPositionItem item)
    {
        AbstractPositionView cell = (AbstractPositionView) convertView;
        if (cell instanceof PositionInPeriodClosedView)
        {
            ((PositionInPeriodClosedView) cell).linkWith(item, true);
        }
        else
        {
            cell.linkWith(item.getModel(), true);
        }
        cell.setListener(getInternalListener());
    }

    @Override protected void setPosition(PositionInPeriodDTO positionDTO)
    {
        ExpandableLeaderboardPositionItem item = new ExpandableLeaderboardPositionItem(positionDTO.getLbOwnedPositionId());
        item.setTimeRestricted(isTimeRestricted);

        if (positionDTO.isOpen() == null)
        {
            // TODO decide what to do
        }
        else if (positionDTO.isOpen())
        {
            addOpenPosition(item);
        }
        else
        {
            addClosedPosition(item);
        }
    }

    public int getItemViewType(int position)
    {
        if (isPositionHeaderOpen(position) || isPositionHeaderClosed(position))
        {
            return PositionItemType.Header.value;
        }
        else if (isOpenPosition(position) && getOpenPositionsCount() == 0)
        {
            return PositionItemType.Placeholder.value;
        }
        else if (isOpenPosition(position))
        {
            ExpandableListItem<OwnedLeaderboardPositionId> id = (ExpandableListItem<OwnedLeaderboardPositionId>) getItem(position);
            if (id.getModel().isLocked())
            {
                return PositionItemType.Locked.value;
            }
            else
            {
                return PositionItemType.Open.value;
            }
        }
        else
        {
            return PositionItemType.Closed.value;
        }
    }

    public static class ExpandableLeaderboardPositionItem extends ExpandableListItem<OwnedLeaderboardPositionId>
    {
        private boolean timeRestricted;

        public ExpandableLeaderboardPositionItem(OwnedLeaderboardPositionId model)
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
    }
}
