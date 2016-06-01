package com.ayondo.academy.fragments.trade;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.ayondo.academy.R;
import com.ayondo.academy.adapters.ExpandableListItem;
import com.ayondo.academy.api.position.PositionDTO;
import com.ayondo.academy.api.position.PositionInPeriodDTO;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.trade.TradeDTO;
import com.ayondo.academy.api.trade.TradeDTOList;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.fragments.position.view.PositionView;
import com.ayondo.academy.fragments.trade.view.TradeListItemView;
import com.ayondo.academy.widget.list.BaseListHeaderView;
import java.util.ArrayList;
import java.util.List;
import org.ocpsoft.prettytime.PrettyTime;

public class TradeListItemAdapter
        extends ArrayAdapter<Object>
{
    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_POSITION_SUMMARY = 1;
    public static final int ITEM_TYPE_TRADE = 2;

    @LayoutRes public static final int LAYOUT_RES_ID_ITEM_HEADER = R.layout.trade_list_item_header;
    @LayoutRes public static final int LAYOUT_RES_ID_ITEM_TRADE = R.layout.trade_list_item;

    @LayoutRes public static final int LAYOUT_RES_ID_POSITION_OPEN = R.layout.position_open_no_period;
    @LayoutRes public static final int LAYOUT_RES_ID_POSITION_CLOSED = R.layout.position_closed_no_period;
    @LayoutRes public static final int LAYOUT_RES_ID_POSITION_IN_PERIOD_OPEN = R.layout.position_open_in_period;
    @LayoutRes public static final int LAYOUT_RES_ID_POSITION_IN_PERIOD_CLOSED = R.layout.position_closed_in_period;

    //<editor-fold desc="Constructors">
    public TradeListItemAdapter(@NonNull final Context context)
    {
        super(context, LAYOUT_RES_ID_ITEM_TRADE);
    }
    //</editor-fold>

    @NonNull public static List<Object> createObjects(
            @NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @NonNull PositionDTO positionDTO,
            @NonNull SecurityCompactDTO securityCompactDTO,
            @Nullable Integer expandedTradeId,
            @Nullable TradeDTOList tradeDTOs,
            @NonNull PrettyTime prettyTime)
    {
        List<Object> objects = new ArrayList<>();

        Boolean isClosed = positionDTO.isClosed();
        if (isClosed != null && isClosed)
        {
            objects.add(R.string.trade_list_header_closed_summary);
        }
        else
        {
            objects.add(R.string.trade_list_header_open_summary);
        }

        objects.add(new PositionView.DTO(
                resources,
                currentUserId,
                new ExpandableListItem<>(true, positionDTO),
                securityCompactDTO));

        objects.add(R.string.trade_list_header_position_summary);
        if (tradeDTOs != null)
        {
            for (int i = 0; i < tradeDTOs.size(); i++)
            {
                TradeDTO dto = tradeDTOs.get(i);
                objects.add(
                        new TradeListItemView.DTO(
                                resources,
                                securityCompactDTO,
                                positionDTO,
                                expandedTradeId != null && expandedTradeId.equals(dto.id),
                                dto,
                                prettyTime));
            }
        }
        else
        {
            // TODO add loading
        }

        return objects;
    }

    @Override public int getViewTypeCount()
    {
        return 3;
    }

    @Override public int getItemViewType(int position)
    {
        Object item = getItem(position);
        if (item instanceof Integer)
        {
            return ITEM_TYPE_HEADER;
        }
        else if (item instanceof PositionView.DTO)
        {
            return ITEM_TYPE_POSITION_SUMMARY;
        }
        else if (item instanceof TradeListItemView.DTO)
        {
            return ITEM_TYPE_TRADE;
        }
        throw new IllegalStateException("Unhandled Item View Type Position: " + position);
    }

    @LayoutRes private int getLayoutFor(int position)
    {
        Object item = getItem(position);
        if (item instanceof Integer)
        {
            return LAYOUT_RES_ID_ITEM_HEADER;
        }
        else if (item instanceof PositionView.DTO)
        {
            PositionDTO positionDTO = ((PositionView.DTO) item).topViewDTO.positionDTO;
            Boolean isClosed = positionDTO.isClosed();
            if (isClosed != null && isClosed && positionDTO instanceof PositionInPeriodDTO)
            {
                return LAYOUT_RES_ID_POSITION_IN_PERIOD_CLOSED;
            }
            else if (isClosed != null && isClosed)
            {
                return LAYOUT_RES_ID_POSITION_CLOSED;
            }
            else if (positionDTO instanceof PositionInPeriodDTO)
            {
                return LAYOUT_RES_ID_POSITION_IN_PERIOD_OPEN;
            }
            else
            {
                return LAYOUT_RES_ID_POSITION_OPEN;
            }
        }
        else if (item instanceof TradeListItemView.DTO)
        {
            return LAYOUT_RES_ID_ITEM_TRADE;
        }
        throw new IllegalStateException("Unhandled Item Layout Id For: " + item);
    }

    @Override @NonNull public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        int itemViewType = getItemViewType(position);
        Object item = getItem(position);

        if (convertView == null)
        {
            int layoutResId = getLayoutFor(position);
            convertView = LayoutInflater.from(getContext()).inflate(layoutResId, viewGroup, false);
        }

        switch (itemViewType)
        {
            case ITEM_TYPE_HEADER:
                ((BaseListHeaderView) convertView).setHeaderTextContent((int) item);
                break;

            case ITEM_TYPE_POSITION_SUMMARY:
                ((PositionView) convertView).display((PositionView.DTO) item);
                ((PositionView) convertView).showCaret(false);
                break;

            case ITEM_TYPE_TRADE:
                TradeListItemView.DTO dto = (TradeListItemView.DTO) item;
                TradeListItemView tradeListItemView = ((TradeListItemView) convertView);
                tradeListItemView.display(dto);
                break;
            default:
                throw new IllegalStateException("Unknown ItemType " + itemViewType);
        }
        return convertView;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        int itemType = getItemViewType(position);
        switch (itemType)
        {
            case ITEM_TYPE_HEADER:
            case ITEM_TYPE_POSITION_SUMMARY:
                return false;

            case ITEM_TYPE_TRADE:
                return true;

            default:
                throw new IllegalStateException("Unknown Item Type " + itemType);
        }
    }
}
