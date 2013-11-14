package com.tradehero.th.adapters.position;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.widget.position.AbstractPositionView;
import com.tradehero.th.widget.position.LockedPositionItem;
import com.tradehero.th.widget.position.PositionSectionHeaderItemView;

/** Created with IntelliJ IDEA. User: tho Date: 11/6/13 Time: 8:11 PM Copyright (c) TradeHero */
public class InPeriodPositionItemAdapter extends AbstractPositionItemAdapter<PositionInPeriodDTO>
{
    public InPeriodPositionItemAdapter(Context context, LayoutInflater inflater, int headerLayoutId,
            int lockedPositionLayoutId, int openPositionLayoutId, int closedPositionLayoutId, int positionNothingId)
    {
        super(context, inflater, headerLayoutId, lockedPositionLayoutId, openPositionLayoutId, closedPositionLayoutId, positionNothingId);
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
            ExpandableListItem<OwnedLeaderboardPositionId> expandableWrapper = (ExpandableListItem<OwnedLeaderboardPositionId>) getItem(position);
            if (expandableWrapper.getModel().isLocked())
            {
                LockedPositionItem cell = (LockedPositionItem)convertView;
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

                bindCell(convertView, expandableWrapper.getModel());
            }
        }
        setLatestView(convertView);
        return convertView;
    }

    private void bindCell(View convertView, OwnedLeaderboardPositionId model)
    {
        AbstractPositionView cell = (AbstractPositionView)convertView;
        cell.linkWith(model, true);
        cell.setListener(getInternalListener());
    }

    @Override protected void setPosition(PositionInPeriodDTO positionDTO)
    {

        ExpandableListItem<OwnedLeaderboardPositionId> item = new ExpandableListItem<>(positionDTO.getLbOwnedPositionId());


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
}
