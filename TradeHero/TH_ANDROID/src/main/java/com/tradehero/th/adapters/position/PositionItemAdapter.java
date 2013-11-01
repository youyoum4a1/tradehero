package com.tradehero.th.adapters.position;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.adapters.ExpandableListReporter;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.widget.position.*;
import com.tradehero.th.widget.position.partial.PositionPartialTopView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class PositionItemAdapter extends BaseAdapter implements ExpandableListReporter
{
    public static final String TAG = PositionItemAdapter.class.getName();

    private List<PositionDTO> receivedPositions;
    private List<ExpandableListItem<OwnedPositionId>> openPositions; // If nothing, it will show the positionNothingId layout
    private List<ExpandableListItem<OwnedPositionId>> closedPositions;

    protected final Context context;
    protected final LayoutInflater inflater;
    private final int headerLayoutId;
    private final int openPositionLayoutId;
    private final int lockedPositionLayoutId;
    private final int closedPositionLayoutId;
    private final int positionNothingId;

    private WeakReference<View> latestView = new WeakReference<>(null);
    private HashMap<Integer, Integer> viewTypeToLayoutId;

    private WeakReference<PositionListener> cellListener;
    // this listener is used as a bridge between the cell and the listener of the adapter
    private PositionListener internalListener;

    public PositionItemAdapter(Context context, LayoutInflater inflater, int headerLayoutId, int lockedPositionLayoutId, int openPositionLayoutId, int closedPositionLayoutId, int positionNothingId)
    {
        super();
        this.context = context;
        this.inflater = inflater;
        this.headerLayoutId = headerLayoutId;
        this.lockedPositionLayoutId = lockedPositionLayoutId;
        this.openPositionLayoutId = openPositionLayoutId;
        this.closedPositionLayoutId = closedPositionLayoutId;
        this.positionNothingId = positionNothingId;

        buildViewTypeMap();
        this.internalListener = new PositionListener()
        {
            @Override public void onTradeHistoryClicked(OwnedPositionId clickedOwnedPositionId)
            {
                PositionListener listener = cellListener.get();
                if (listener != null)
                {
                    listener.onTradeHistoryClicked(clickedOwnedPositionId);
                }
            }

            @Override public void onBuyClicked(OwnedPositionId clickedOwnedPositionId)
            {
                PositionListener listener = cellListener.get();
                if (listener != null)
                {
                    listener.onBuyClicked(clickedOwnedPositionId);
                }
            }

            @Override public void onSellClicked(OwnedPositionId clickedOwnedPositionId)
            {
                PositionListener listener = cellListener.get();
                if (listener != null)
                {
                    listener.onSellClicked(clickedOwnedPositionId);
                }
            }

            @Override public void onAddAlertClicked(OwnedPositionId clickedOwnedPositionId)
            {
                PositionListener listener = cellListener.get();
                if (listener != null)
                {
                    listener.onAddAlertClicked(clickedOwnedPositionId);
                }
            }

            @Override public void onStockInfoClicked(OwnedPositionId clickedOwnedPositionId)
            {
                PositionListener listener = cellListener.get();
                if (listener != null)
                {
                    listener.onStockInfoClicked(clickedOwnedPositionId);
                }
            }
        };
    }

    private void buildViewTypeMap()
    {
        viewTypeToLayoutId = new HashMap<>();
        viewTypeToLayoutId.put(PositionItemType.Header.value, this.headerLayoutId);
        viewTypeToLayoutId.put(PositionItemType.Placeholder.value, this.positionNothingId);
        viewTypeToLayoutId.put(PositionItemType.Locked.value, this.lockedPositionLayoutId);
        viewTypeToLayoutId.put(PositionItemType.Open.value, this.openPositionLayoutId);
        viewTypeToLayoutId.put(PositionItemType.Closed.value, this.closedPositionLayoutId);
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    //<editor-fold desc="Counting Methods">
    public int getOpenPositionsCount()
    {
        return openPositions == null ? 0 : openPositions.size();
    }

    public int getVisibleOpenPositionsCount()
    {
        // If the list is empty, it will still show "tap to trade"
        return openPositions == null ? 0 : Math.max(1, openPositions.size());
    }

    public int getClosedPositionsCount()
    {
        return closedPositions == null ? 0 : closedPositions.size();
    }

    @Override public int getCount()
    {
        return 2 + getVisibleOpenPositionsCount() + getClosedPositionsCount();
    }
    //</editor-fold>

    //<editor-fold desc="Index Methods">
    public int getOpenPositionIndex(int position)
    {
        return position - 1;
    }

    public int getClosedPositionIndex(int position)
    {
        return position - 2 - getVisibleOpenPositionsCount();
    }

    public boolean isPositionHeaderOpen(int position)
    {
        return position == 0;
    }

    public boolean isOpenPosition(int position)
    {
        return position >= 1 && position <= getVisibleOpenPositionsCount();
    }

    public boolean isPositionHeaderClosed(int position)
    {
        return position == (getVisibleOpenPositionsCount() + 1);
    }

    public boolean isClosedPosition(int position)
    {
        return position >= (getVisibleOpenPositionsCount() + 2);
    }
    //</editor-fold>

    @Override public long getItemId(int position)
    {
        long itemId;
        if (isPositionHeaderOpen(position))
        {
            itemId = "openPositionHeader".hashCode();
        }
        else if (isOpenPosition(position))
        {
            if (getOpenPositionsCount() == 0)
            {
                itemId = "tapToTrade".hashCode();
            }
            else
            {
                itemId = openPositions.get(position - 1).hashCode();
            }
        }
        else if (isPositionHeaderClosed(position))
        {
            itemId = "closedPositionHeader".hashCode();
        }
        else
        {
            itemId = closedPositions.get(position - 2 - getVisibleOpenPositionsCount()).hashCode();
        }
        return itemId;
    }

    @Override public Object getItem(int position)
    {
        if (isOpenPosition(position) && getOpenPositionsCount() > 0)
        {
            return openPositions.get(getOpenPositionIndex(position));
        }
        else if (isClosedPosition(position))
        {
            return closedPositions.get(getClosedPositionIndex(position));
        }
        return null;
    }

    public String getHeaderText(boolean isForOpenPositions)
    {
        int stringResId;
        int count;
        if (isForOpenPositions)
        {
            stringResId = (openPositions == null) ? R.string.position_list_header_open_unsure : R.string.position_list_header_open;
            count = getOpenPositionsCount();
        }
        else
        {
            stringResId = (closedPositions == null) ? R.string.position_list_header_closed_unsure : R.string.position_list_header_closed;
            count = getClosedPositionsCount();
        }
        return String.format(context.getResources().getString(stringResId), count);
    }

    @Override public int getItemViewType(int position)
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
            ExpandableListItem<OwnedPositionId> id = (ExpandableListItem<OwnedPositionId>)getItem(position);
            if (id.getModel().isLocked()) {
                return PositionItemType.Locked.value;
            } else {
                return PositionItemType.Open.value;
            }
        }
        else
        {
            return PositionItemType.Closed.value;
        }
    }

    @Override public int getViewTypeCount()
    {
        return PositionItemType.values().length;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            int layoutToInflate = viewTypeToLayoutId.get(getItemViewType(position));
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
            ExpandableListItem<OwnedPositionId> expandableWrapper = (ExpandableListItem<OwnedPositionId>) getItem(position);
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

                AbstractPositionView cell = (AbstractPositionView)convertView;
                cell.linkWith(expandableWrapper.getModel(), true);
                cell.setListener(internalListener);
            }
        }
        latestView = new WeakReference<>(convertView);
        return convertView;
    }

    /**
     * The listener needs to be strongly referenced elsewhere
     * @param cellListener
     */
    public void setCellListener(PositionListener cellListener)
    {
        this.cellListener = new WeakReference<>(cellListener);
    }

    public void setPositions(List<PositionDTO> positions, PortfolioId portfolioId)
    {
        this.receivedPositions = positions;

        if (positions == null)
        {
            openPositions = null;
            closedPositions = null;
        }
        else
        {
            openPositions = new ArrayList<>();
            closedPositions = new ArrayList<>();

            for (PositionDTO positionDTO: positions)
            {
                ExpandableListItem<OwnedPositionId> item = new ExpandableListItem<>(positionDTO.getOwnedPositionId(portfolioId.key));
                if (positionDTO.isOpen() == null)
                {
                    // TODO decide what to do
                }
                else if (positionDTO.isOpen())
                {
                    openPositions.add(item);
                }
                else
                {
                    closedPositions.add(item);
                }
            }
        }
    }

    //<editor-fold desc="ExpandableListReporter">
    @Override public List<Boolean> getExpandedStatesPerPosition()
    {
        List<Boolean> expandedStates = new ArrayList<>();

        int position = 0;
        Object itemAtPosition = null;
        while (position < getCount())
        {
            itemAtPosition = getItem(position);
            if (itemAtPosition instanceof ExpandableListItem)
            {
                expandedStates.add(((ExpandableListItem) itemAtPosition).isExpanded());
            }
            else
            {
                expandedStates.add(false);
            }
            position++;
        }

        return expandedStates;
    }

    @Override public void setExpandedStatesPerPosition(boolean[] expandedStatesPerPosition)
    {
        if (expandedStatesPerPosition == null)
        {
            return;
        }
        List<Boolean> expandedStates = new ArrayList<>();
        for (boolean state: expandedStatesPerPosition)
        {
            expandedStates.add(state);
        }
        setExpandedStatesPerPosition(expandedStates);
    }

    @Override public void setExpandedStatesPerPosition(List<Boolean> expandedStatesPerPosition)
    {
        if (expandedStatesPerPosition == null)
        {
            return;
        }

        int position = 0;
        while (position < getCount() && position < expandedStatesPerPosition.size())
        {
            Object itemAtPosition = getItem(position);
            if (itemAtPosition instanceof ExpandableListItem)
            {
                ((ExpandableListItem) itemAtPosition).setExpanded(expandedStatesPerPosition.get(position));
            }
            position++;
        }
    }
    //</editor-fold>
}
