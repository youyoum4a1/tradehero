package com.tradehero.th.fragments.position;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.adapters.ExpandableListReporter;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.fragments.position.view.AbstractPositionView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

public class AbstractPositionItemAdapter
        extends BaseAdapter implements ExpandableListReporter
{
    protected List<PositionItemType> itemTypes = new ArrayList<>();
    protected List<Object> items = new ArrayList<>();

    protected final Context context;
    protected final LayoutInflater inflater;

    private Map<PositionItemType, Integer> positionItemTypeToLayoutId;

    private WeakReference<PositionListener<PositionDTO>> cellListener;

    public AbstractPositionItemAdapter(
            Context context,
            LayoutInflater inflater,
            Map<PositionItemType, Integer> positionItemTypeToLayoutId)
    {
        super();
        this.context = context;
        this.inflater = inflater;
        this.positionItemTypeToLayoutId = positionItemTypeToLayoutId;
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    public void setItems(List<PositionDTO> dtos)
    {
        List<PositionItemType> newItemTypes = new ArrayList<>();
        List<Object> newItems = new ArrayList<>();

        if (dtos == null || dtos.size() == 0)
        {
            newItemTypes.add(PositionItemType.Header);
            newItems.add(null);

            newItemTypes.add(PositionItemType.Placeholder);
            newItems.add(null);
        }
        else
        {
            PositionDTOList<PositionDTO> lockedPositions = new PositionDTOList<>();
            PositionDTOList<PositionDTO> openPositions = new PositionDTOList<>();
            PositionDTOList<PositionDTO> closedPositions = new PositionDTOList<>();

            // Split in open / closed
            for (PositionDTO positionDTO : dtos)
            {
                if (positionDTO.isLocked())
                {
                    lockedPositions.add(positionDTO);
                }
                else if (positionDTO.isClosed())
                {
                    closedPositions.add(positionDTO);
                }
                else
                {
                    openPositions.add(positionDTO);
                }
            }

            // Dress list

            // Open area
            if (lockedPositions.size() > 0)
            {
                newItemTypes.add(PositionItemType.Header);
                PositionDTO positionDTO = lockedPositions.get(0);
                newItems.add(new HeaderDTO(
                        PositionItemType.Locked,
                        positionDTO.aggregateCount,
                        positionDTO.earliestTradeUtc,
                        positionDTO.latestTradeUtc));

                newItemTypes.add(PositionItemType.Locked);
                newItems.add(null);
            }
            else if (openPositions.size() > 0)
            {
                newItemTypes.add(PositionItemType.Header);
                newItems.add(new HeaderDTO(
                        PositionItemType.Open,
                        openPositions.size(),
                        openPositions.getEarliestTradeUtc(),
                        openPositions.getLatestTradeUtc()
                        ));

                for (PositionDTO openPosition : openPositions)
                {
                    if (openPosition instanceof PositionInPeriodDTO)
                    {
                        newItemTypes.add(PositionItemType.OpenInPeriod);
                    }
                    else
                    {
                        newItemTypes.add(PositionItemType.Open);
                    }
                    newItems.add(createExpandableItem(openPosition));
                }
            }
            else
            {
                newItemTypes.add(PositionItemType.Header);
                newItems.add(new HeaderDTO(PositionItemType.Placeholder, null));

                newItemTypes.add(PositionItemType.Placeholder);
                newItems.add(null);
            }

            // Closed area
            if (closedPositions.size() > 0)
            {
                newItemTypes.add(PositionItemType.Header);
                newItems.add(new HeaderDTO(
                        PositionItemType.Closed,
                        closedPositions.size(),
                        closedPositions.getEarliestTradeUtc(),
                        closedPositions.getLatestTradeUtc()
                        ));

                for (PositionDTO closedPosition : closedPositions)
                {
                    if (closedPosition instanceof PositionInPeriodDTO)
                    {
                        newItemTypes.add(PositionItemType.ClosedInPeriod);
                    }
                    else
                    {
                        newItemTypes.add(PositionItemType.Closed);
                    }
                    newItems.add(createExpandableItem(closedPosition));
                }
            }
        }

        this.itemTypes = newItemTypes;
        this.items = newItems;
        notifyDataSetChanged();
    }

    protected ExpandableListItem<PositionDTO> createExpandableItem(PositionDTO dto)
    {
        return new ExpandableListItem<>(dto);
    }

    @Override public int getCount()
    {
        return itemTypes.size();
    }

    @Override public int getItemViewType(int position)
    {
        return itemTypes.get(position).value;
    }

    protected int getLayoutForPosition(int position)
    {
        return positionItemTypeToLayoutId.get(itemTypes.get(position));
    }

    @Override public Object getItem(int position)
    {
        return items.get(position);
    }

    @Override public long getItemId(int position)
    {
        Object item = getItem(position);
        return item == null ? 0 : item.hashCode();
    }

    public String getHeaderText(HeaderDTO headerDTO)
    {
        if (headerDTO == null ||
                headerDTO.headerFor == PositionItemType.Open ||
                headerDTO.headerFor == PositionItemType.Locked ||
                headerDTO.headerFor == PositionItemType.Placeholder)
        {
            return getOpenHeaderText(headerDTO);
        }
        else if (headerDTO.headerFor == PositionItemType.Closed)
        {
            return getClosedHeaderText(headerDTO);
        }
        throw new IllegalArgumentException("Unhandled " + headerDTO.toString() );
    }

    public String getOpenHeaderText(HeaderDTO headerDTO)
    {
        if (headerDTO == null || headerDTO.count == null)
        {
            return context.getString(R.string.position_list_header_open_unsure);
        }
        return context.getString(R.string.position_list_header_open, (int) headerDTO.count);
    }

    public String getClosedHeaderText(HeaderDTO headerDTO)
    {
        if (headerDTO == null || headerDTO.count == null)
        {
            return context.getString(R.string.position_list_header_closed_unsure);
        }
        return context.getString(R.string.position_list_header_closed, (int) headerDTO.count);
    }

    @Override public int getViewTypeCount()
    {
        return PositionItemType.values().length;
    }

    //@SuppressWarnings("unchecked")
    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        int itemViewType = getItemViewType(position);
        int layoutToInflate = getLayoutForPosition(position);

        if (convertView == null)
        {
            try
            {
                convertView = inflater.inflate(layoutToInflate, parent, false);
            }
            catch (Throwable t)
            {
                do
                {
                    Timber.e(t, "error");
                    t = t.getCause();
                }
                while (t != null);
            }
        }

        Object item = getItem(position);

        if (itemViewType == PositionItemType.Header.value)
        {
            prepareHeaderView((PositionSectionHeaderItemView) convertView, (HeaderDTO) item);
        }
        else if (itemViewType == PositionItemType.Locked.value)
        {
            PositionLockedView cell = (PositionLockedView) convertView;
            cell.linkWith((PositionDTO) null, false);
            cell.display();
        }
        else if (itemViewType == PositionItemType.Closed.value || itemViewType == PositionItemType.Open.value)
        {
            ExpandableListItem<PositionDTO> expandableWrapper = (ExpandableListItem<PositionDTO>) getItem(position);
            AbstractPositionView cell = (AbstractPositionView) convertView;
            cell.linkWith(expandableWrapper, false);
            cell.display();
            cell.setListener(new AbstractPositionItemAdapterPositionListener());
        }

        return convertView;
    }

    protected void prepareHeaderView(PositionSectionHeaderItemView convertView, HeaderDTO info)
    {
        convertView.setHeaderTextContent(getHeaderText(info));
        convertView.setTimeBaseTextContent(
                info == null ? null : info.dateStart,
                info == null ? null : info.dateEnd);
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItemViewType(position) != PositionItemType.Header.value;
    }

    /**
     * The listener needs to be strongly referenced elsewhere
     * @param cellListener
     */
    public void setCellListener(PositionListener<PositionDTO> cellListener)
    {
        this.cellListener = new WeakReference<>(cellListener);
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

    public static class HeaderDTO
    {
        public final PositionItemType headerFor;
        public final Integer count;
        public final Date dateStart;
        public final Date dateEnd;

        public HeaderDTO(PositionItemType headerFor, Integer count)
        {
            this.headerFor = headerFor;
            this.count = count;
            this.dateStart = null;
            this.dateEnd = null;
        }

        public HeaderDTO(PositionItemType headerFor, Integer count, Date dateStart, Date dateEnd)
        {
            this.headerFor = headerFor;
            this.count = count;
            this.dateStart = dateStart;
            this.dateEnd = dateEnd;
        }

        @Override public String toString()
        {
            return "HeaderDTO{" +
                    "headerFor=" + headerFor +
                    ", count=" + count +
                    ", dateStart=" + dateStart +
                    ", dateEnd=" + dateEnd +
                    '}';
        }
    }

    protected class AbstractPositionItemAdapterPositionListener
            implements PositionListener<PositionDTO>
    {
        @Override public void onTradeHistoryClicked(PositionDTO clickedOwnedPositionId)
        {
            PositionListener<PositionDTO> listener = cellListener.get();
            if (listener != null)
            {
                listener.onTradeHistoryClicked(clickedOwnedPositionId);
            }
        }

        @Override public void onBuyClicked(PositionDTO clickedOwnedPositionId)
        {
            PositionListener<PositionDTO> listener = cellListener.get();
            if (listener != null)
            {
                listener.onBuyClicked(clickedOwnedPositionId);
            }
        }

        @Override public void onSellClicked(PositionDTO clickedOwnedPositionId)
        {
            PositionListener<PositionDTO> listener = cellListener.get();
            if (listener != null)
            {
                listener.onSellClicked(clickedOwnedPositionId);
            }
        }

        @Override public void onAddAlertClicked(PositionDTO clickedOwnedPositionId)
        {
            PositionListener<PositionDTO> listener = cellListener.get();
            if (listener != null)
            {
                listener.onAddAlertClicked(clickedOwnedPositionId);
            }
        }

        @Override public void onStockInfoClicked(PositionDTO clickedOwnedPositionId)
        {
            PositionListener<PositionDTO> listener = cellListener.get();
            if (listener != null)
            {
                listener.onStockInfoClicked(clickedOwnedPositionId);
            }
        }
    }
}
