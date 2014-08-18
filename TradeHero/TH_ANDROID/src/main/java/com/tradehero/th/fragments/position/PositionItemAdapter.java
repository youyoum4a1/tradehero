package com.tradehero.th.fragments.position;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.position.view.PositionView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PositionItemAdapter extends ArrayAdapter<Object>
{
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_PLACEHOLDER = 1;
    public static final int VIEW_TYPE_LOCKED = 2;
    public static final int VIEW_TYPE_OPEN = 3;
    public static final int VIEW_TYPE_OPEN_IN_PERIOD = 4;
    public static final int VIEW_TYPE_CLOSED = 5;
    public static final int VIEW_TYPE_CLOSED_IN_PERIOD = 6;

    protected Map<Integer, Integer> itemTypeToLayoutId;

    //<editor-fold desc="Constructors">
    public PositionItemAdapter(@NotNull Context context, @NotNull Map<Integer, Integer> itemTypeToLayoutId)
    {
        super(context, 0);
        this.itemTypeToLayoutId = itemTypeToLayoutId;
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return 7;
    }

    @Override public int getItemViewType(int position)
    {
        Object item = getItem(position);
        if (item instanceof PositionDTO)
        {
            return getItemViewType((PositionDTO) item);
        }
        else if (item instanceof HeaderDTO)
        {
            return VIEW_TYPE_HEADER;
        }
        else if (item == null)
        {
            return VIEW_TYPE_PLACEHOLDER;
        }
        throw new IllegalArgumentException("Unhandled item " + item);
    }

    protected int getItemViewType(PositionDTO item)
    {
        if (item.isLocked())
        {
            return VIEW_TYPE_LOCKED;
        }
        else if (item.isClosed() && item instanceof PositionInPeriodDTO)
        {
            return VIEW_TYPE_CLOSED_IN_PERIOD;
        }
        else if (item.isClosed())
        {
            return VIEW_TYPE_CLOSED;
        }
        else if (item.isOpen() && item instanceof PositionInPeriodDTO)
        {
            return VIEW_TYPE_OPEN_IN_PERIOD;
        }
        else if (item.isOpen())
        {
            return VIEW_TYPE_OPEN;
        }
        throw new IllegalArgumentException("Unhandled item " + item);
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItemViewType(position) != VIEW_TYPE_HEADER;
    }

    protected int getLayoutForPosition(int position)
    {
        return itemTypeToLayoutId.get(getItemViewType(position));
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public long getItemId(int position)
    {
        Object item = getItem(position);
        return item == null ? 0 : item.hashCode();
    }

    public void addAll(@Nullable List<PositionDTO> dtos)
    {
        List<Object> newItems = new ArrayList<>();

        if (dtos == null || dtos.size() == 0)
        {
            newItems.add(new HeaderDTO(VIEW_TYPE_PLACEHOLDER, 0));
            newItems.add(null);
        }
        else
        {
            PositionDTOList<PositionDTO> lockedPositions = new PositionDTOList<>();
            PositionDTOList<PositionDTO> openPositions = new PositionDTOList<>();
            PositionDTOList<PositionDTO> closedPositions = new PositionDTOList<>();

            // Split in open / closed
            for (@NotNull PositionDTO positionDTO : dtos)
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
                PositionDTO positionDTO = lockedPositions.get(0);
                newItems.add(new HeaderDTO(
                        VIEW_TYPE_LOCKED,
                        positionDTO.aggregateCount,
                        positionDTO.earliestTradeUtc,
                        positionDTO.latestTradeUtc));

                newItems.add(positionDTO);
            }
            else if (openPositions.size() > 0)
            {
                newItems.add(new HeaderDTO(
                        VIEW_TYPE_OPEN,
                        openPositions.size(),
                        openPositions.getEarliestTradeUtc(),
                        openPositions.getLatestTradeUtc()
                ));

                for (@NotNull PositionDTO openPosition : openPositions)
                {
                    add(newItems, openPosition);
                }
            }
            else
            {
                newItems.add(new HeaderDTO(VIEW_TYPE_PLACEHOLDER, null));
                newItems.add(null);
            }

            // Closed area
            if (closedPositions.size() > 0)
            {
                newItems.add(new HeaderDTO(
                        VIEW_TYPE_CLOSED,
                        closedPositions.size(),
                        closedPositions.getEarliestTradeUtc(),
                        closedPositions.getLatestTradeUtc()
                ));

                for (@NotNull PositionDTO closedPosition : closedPositions)
                {
                    add(newItems, closedPosition);
                }
            }
        }

        addAll(newItems);
    }

    protected void add(@NotNull List<Object> items, @NotNull PositionDTO item)
    {
        items.add(item);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        int itemViewType = getItemViewType(position);
        int layoutToInflate = getLayoutForPosition(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(layoutToInflate, parent, false);
        }

        Object item = getItem(position);

        if (itemViewType == VIEW_TYPE_HEADER)
        {
            prepareHeaderView((PositionSectionHeaderItemView) convertView, (HeaderDTO) item);
        }
        else if (itemViewType == VIEW_TYPE_LOCKED)
        {
            PositionLockedView cell = (PositionLockedView) convertView;
            cell.linkWith((PositionDTO) item, false);
            cell.display();
        }
        else if (itemViewType == VIEW_TYPE_PLACEHOLDER)
        {
            // Do nothing
        }
        else if (convertView instanceof PositionView)
        {
            preparePositionView((PositionView) convertView, item, position);
        }
        else if (convertView instanceof PositionPartialTopView)
        {
            ((PositionPartialTopView) convertView).linkWith((PositionDTO) item, false);
            ((PositionPartialTopView) convertView).display();
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

    public String getHeaderText(HeaderDTO headerDTO)
    {
        if (headerDTO == null ||
                headerDTO.headerForViewType == VIEW_TYPE_OPEN ||
                headerDTO.headerForViewType == VIEW_TYPE_LOCKED ||
                headerDTO.headerForViewType == VIEW_TYPE_PLACEHOLDER)
        {
            return getOpenHeaderText(headerDTO);
        }
        else if (headerDTO.headerForViewType == VIEW_TYPE_CLOSED)
        {
            return getClosedHeaderText(headerDTO);
        }
        throw new IllegalArgumentException("Unhandled " + headerDTO.toString() );
    }

    public String getOpenHeaderText(HeaderDTO headerDTO)
    {
        if (headerDTO == null || headerDTO.count == null)
        {
            return getContext().getString(R.string.position_list_header_open_unsure);
        }
        return getContext().getString(R.string.position_list_header_open, (int) headerDTO.count);
    }

    public String getClosedHeaderText(HeaderDTO headerDTO)
    {
        if (headerDTO == null || headerDTO.count == null)
        {
            return getContext().getString(R.string.position_list_header_closed_unsure);
        }
        return getContext().getString(R.string.position_list_header_closed, (int) headerDTO.count);
    }

    protected void preparePositionView(PositionView cell, Object item, int position)
    {
        cell.linkWith((PositionDTO) item, false);
        cell.display();
    }

    public static class HeaderDTO
    {
        public final int headerForViewType;
        public final Integer count;
        public final Date dateStart;
        public final Date dateEnd;

        public HeaderDTO(int headerForViewType, Integer count)
        {
            this.headerForViewType = headerForViewType;
            this.count = count;
            this.dateStart = null;
            this.dateEnd = null;
        }

        public HeaderDTO(int headerForViewType, Integer count, Date dateStart, Date dateEnd)
        {
            this.headerForViewType = headerForViewType;
            this.count = count;
            this.dateStart = dateStart;
            this.dateEnd = dateEnd;
        }

        @Override public String toString()
        {
            return "HeaderDTO{" +
                    "headerFor=" + headerForViewType +
                    ", count=" + count +
                    ", dateStart=" + dateStart +
                    ", dateEnd=" + dateEnd +
                    '}';
        }
    }


}
