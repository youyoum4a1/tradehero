package com.tradehero.th.fragments.position;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.position.view.PositionNothingView;
import com.tradehero.th.fragments.position.view.PositionView;
import com.tradehero.th.inject.HierarchyInjector;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class PositionItemAdapter extends ArrayAdapter<Object>
{
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_PLACEHOLDER = 1;
    public static final int VIEW_TYPE_LOCKED = 2;
    public static final int VIEW_TYPE_OPEN_LONG = 3;
    public static final int VIEW_TYPE_OPEN_LONG_IN_PERIOD = 4;
    public static final int VIEW_TYPE_OPEN_SHORT = 5;
    public static final int VIEW_TYPE_OPEN_SHORT_IN_PERIOD = 6;
    public static final int VIEW_TYPE_CLOSED = 7;
    public static final int VIEW_TYPE_CLOSED_IN_PERIOD = 8;

    protected Map<Integer, Integer> itemTypeToLayoutId;
    private PortfolioDTO portfolioDTO;
    private UserProfileDTO userProfileDTO;

    @Inject CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    public PositionItemAdapter(@NonNull Context context, @NonNull Map<Integer, Integer> itemTypeToLayoutId)
    {
        super(context, 0);
        HierarchyInjector.inject(context, this);
        this.itemTypeToLayoutId = itemTypeToLayoutId;
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return 9;
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

    protected int getItemViewType(@NonNull PositionDTO item)
    {
        Boolean isClosed = item.isClosed();
        Boolean isOpen = item.isOpen();
        if (item.isLocked())
        {
            return VIEW_TYPE_LOCKED;
        }
        else if (isClosed != null && isClosed)
        {
            if (item instanceof PositionInPeriodDTO)
            {
                return VIEW_TYPE_CLOSED_IN_PERIOD;
            }
            return VIEW_TYPE_CLOSED;
        }
        else if (isOpen != null && isOpen)
        {
            boolean isShort = item.positionStatus != null && item.positionStatus.equals(PositionStatus.SHORT);
            if (isShort)
            {
                if (item instanceof PositionInPeriodDTO)
                {
                    return VIEW_TYPE_OPEN_SHORT_IN_PERIOD;
                }
                return VIEW_TYPE_OPEN_SHORT;
            }
            if (item instanceof PositionInPeriodDTO)
            {
                return VIEW_TYPE_OPEN_LONG_IN_PERIOD;
            }
            return VIEW_TYPE_OPEN_LONG;
        }

        // TODO short
        throw new IllegalArgumentException("Unhandled item " + item);
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        int viewType = getItemViewType(position);
        if(viewType != VIEW_TYPE_HEADER)
        {
            if(viewType == VIEW_TYPE_PLACEHOLDER && userProfileDTO != null)
            {
                return userProfileDTO.getBaseKey().equals(currentUserId.toUserBaseKey());
            }
        }
        return false;
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
            PositionDTOList<PositionDTO> openLongPositions = new PositionDTOList<>();
            PositionDTOList<PositionDTO> openShortPositions = new PositionDTOList<>();
            PositionDTOList<PositionDTO> closedPositions = new PositionDTOList<>();

            // Split in open / closed
            for (PositionDTO positionDTO : dtos)
            {
                switch (getItemViewType(positionDTO))
                {
                    case VIEW_TYPE_LOCKED:
                        lockedPositions.add(positionDTO);
                        break;
                    case VIEW_TYPE_CLOSED_IN_PERIOD:
                    case VIEW_TYPE_CLOSED:
                        closedPositions.add(positionDTO);
                        break;
                    case VIEW_TYPE_OPEN_LONG_IN_PERIOD:
                    case VIEW_TYPE_OPEN_LONG:
                        openLongPositions.add(positionDTO);
                        break;
                    case VIEW_TYPE_OPEN_SHORT_IN_PERIOD:
                    case VIEW_TYPE_OPEN_SHORT:
                        openShortPositions.add(positionDTO);
                        break;
                }
            }

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
            else if (openShortPositions.size() + openLongPositions.size() > 0)
            {
                if (openShortPositions.size() > 0)
                {
                    newItems.add(new HeaderDTO(
                            VIEW_TYPE_OPEN_SHORT,
                            openShortPositions.size(),
                            openShortPositions.getEarliestTradeUtc(),
                            openShortPositions.getLatestTradeUtc()
                    ));

                    for (PositionDTO openPosition : openShortPositions)
                    {
                        add(newItems, openPosition);
                    }
                }

                if (openLongPositions.size() > 0)
                {
                    newItems.add(new HeaderDTO(
                            VIEW_TYPE_OPEN_LONG,
                            openLongPositions.size(),
                            openLongPositions.getEarliestTradeUtc(),
                            openLongPositions.getLatestTradeUtc()
                    ));

                    for (PositionDTO openPosition : openLongPositions)
                    {
                        add(newItems, openPosition);
                    }
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

                for (PositionDTO closedPosition : closedPositions)
                {
                    add(newItems, closedPosition);
                }
            }
        }

        addAll(newItems);
    }

    protected void add(@NonNull List<Object> items, @NonNull PositionDTO item)
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
            if(convertView instanceof PositionNothingView)
            {
                ((PositionNothingView) convertView).display(isEnabled(position));
            }
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

    protected void prepareHeaderView(PositionSectionHeaderItemView convertView, final HeaderDTO info)
    {
        convertView.setHeaderTextContent(getHeaderText(info));
        convertView.setTimeBaseTextContent(
                info == null ? null : info.dateStart,
                info == null ? null : info.dateEnd);
        convertView.setType(getHeaderType(info));
    }

    public int getHeaderType(HeaderDTO headerDTO)
    {
        if (headerDTO == null ||
                headerDTO.headerForViewType == VIEW_TYPE_OPEN_LONG ||
                headerDTO.headerForViewType == VIEW_TYPE_LOCKED ||
                headerDTO.headerForViewType == VIEW_TYPE_PLACEHOLDER)
        {
            return PositionSectionHeaderItemView.INFO_TYPE_LONG;
        }
        else if (headerDTO.headerForViewType == VIEW_TYPE_OPEN_SHORT)
        {
            return PositionSectionHeaderItemView.INFO_TYPE_SHORT;
        }
        else if (headerDTO.headerForViewType == VIEW_TYPE_CLOSED)
        {
            return PositionSectionHeaderItemView.INFO_TYPE_CLOSED;
        }
        return -1;
    }

    public String getHeaderText(HeaderDTO headerDTO)
    {
        if (headerDTO == null ||
                headerDTO.headerForViewType == VIEW_TYPE_OPEN_LONG ||
                headerDTO.headerForViewType == VIEW_TYPE_LOCKED ||
                headerDTO.headerForViewType == VIEW_TYPE_PLACEHOLDER)
        {
            return getOpenLongHeaderText(headerDTO);
        }
        else if (headerDTO.headerForViewType == VIEW_TYPE_OPEN_SHORT)
        {
            return getOpenShortHeaderText(headerDTO);
        }
        else if (headerDTO.headerForViewType == VIEW_TYPE_CLOSED)
        {
            return getClosedHeaderText(headerDTO);
        }
        throw new IllegalArgumentException("Unhandled " + headerDTO.toString());
    }

    public String getOpenLongHeaderText(HeaderDTO headerDTO)
    {
        if (userProfileDTO != null && portfolioDTO != null && portfolioDTO.assetClass == AssetClass.FX)
        {
            if (headerDTO == null || headerDTO.count == null)
            {
                return getContext().getString(R.string.position_list_header_open_long_unsure);
            }
            return getContext().getString(R.string.position_list_header_open_long, (int) headerDTO.count);
        }
        else
        {
            if (headerDTO == null || headerDTO.count == null)
            {
                return getContext().getString(R.string.position_list_header_open_unsure);
            }
            return getContext().getString(R.string.position_list_header_open, (int) headerDTO.count);
        }
    }

    public String getOpenShortHeaderText(HeaderDTO headerDTO)
    {
        if (headerDTO == null || headerDTO.count == null)
        {
            return getContext().getString(R.string.position_list_header_open_short_unsure);
        }
        return getContext().getString(R.string.position_list_header_open_short, (int) headerDTO.count);
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

    public void linkWith(PortfolioDTO portfolioDTO)
    {
        this.portfolioDTO = portfolioDTO;
    }

    public void linkWith(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
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
