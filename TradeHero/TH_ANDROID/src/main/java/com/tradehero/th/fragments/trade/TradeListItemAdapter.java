package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.LoadingDTO;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableDTOAdapter;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.position.view.PositionView;
import com.tradehero.th.fragments.trade.view.TradeListItemView;
import com.tradehero.th.widget.list.BaseListHeaderView;
import java.util.ArrayList;
import java.util.List;

public class TradeListItemAdapter
        extends ExpandableDTOAdapter<
        PositionTradeDTOKey,
        TradeListItemAdapter.ExpandableTradeItem,
        TradeListItemView>
{
    public static final int ITEM_TYPE_HEADER_POSITION_SUMMARY = 0;
    public static final int ITEM_TYPE_POSITION_SUMMARY = 1;
    public static final int ITEM_TYPE_POSITION_LOADING = 2;
    public static final int ITEM_TYPE_HEADER_TRADE_HISTORY = 3;
    public static final int ITEM_TYPE_TRADE = 4;
    public static final int ITEM_TYPE_TRADE_LOADING = 5;

    @LayoutRes public static final int LAYOUT_RES_ID_ITEM_HEADER = R.layout.trade_list_item_header;
    @LayoutRes public static final int LAYOUT_RES_ID_ITEM_TRADE_LOADING = R.layout.loading_item;
    @LayoutRes public static final int LAYOUT_RES_ID_ITEM_TRADE = R.layout.trade_list_item;

    @LayoutRes public static final int LAYOUT_RES_ID_POSITION_LOADING = R.layout.loading_item;
    @LayoutRes public static final int LAYOUT_RES_ID_POSITION_OPEN = R.layout.position_open_no_period;
    @LayoutRes public static final int LAYOUT_RES_ID_POSITION_CLOSED = R.layout.position_closed_no_period;
    @LayoutRes public static final int LAYOUT_RES_ID_POSITION_IN_PERIOD_OPEN = R.layout.position_open_in_period;
    @LayoutRes public static final int LAYOUT_RES_ID_POSITION_IN_PERIOD_CLOSED = R.layout.position_closed_in_period;

    private List<Integer> itemTypes;
    private List<Object> objects;

    @Nullable protected Pair<PositionDTO, SecurityCompactDTO> shownPositionDTO;
    @Nullable protected List<PositionTradeDTOKey> underlyingItems;

    //<editor-fold desc="Constructors">
    public TradeListItemAdapter(final Context context)
    {
        super(context, LAYOUT_RES_ID_ITEM_TRADE);
        recreateObjects();
    }
    //</editor-fold>

    public void setShownPositionDTO(Pair<PositionDTO, SecurityCompactDTO> positionDTO)
    {
        this.shownPositionDTO = positionDTO;
        notifyDataSetChanged();
    }

    @Override public void setUnderlyingItems(final List<PositionTradeDTOKey> underlyingItems)
    {
        super.setUnderlyingItems(underlyingItems);
        this.underlyingItems = underlyingItems;
        notifyDataSetChanged();
    }

    @Override public void notifyDataSetChanged()
    {
        recreateObjects();
        super.notifyDataSetChanged();
    }

    protected void recreateObjects()
    {
        List<Integer> itemTypesTemp = new ArrayList<>();
        List<Object> objectsTemp = new ArrayList<>();
        this.items = new ArrayList<>();

        itemTypesTemp.add(ITEM_TYPE_HEADER_POSITION_SUMMARY);
        if (this.shownPositionDTO != null)
        {
            Boolean isClosed = this.shownPositionDTO.first.isClosed();
            if (isClosed != null && isClosed)
            {
                objectsTemp.add(R.string.trade_list_header_closed_summary);
            }
            else
            {
                objectsTemp.add(R.string.trade_list_header_open_summary);
            }

            itemTypesTemp.add(ITEM_TYPE_POSITION_SUMMARY);
            objectsTemp.add(this.shownPositionDTO);
        }
        else
        {
            objectsTemp.add(R.string.trade_list_header_position_summary);

            itemTypesTemp.add(ITEM_TYPE_POSITION_LOADING);
            objectsTemp.add(new LoadingDTO()
            {
            });
        }

        itemTypesTemp.add(ITEM_TYPE_HEADER_TRADE_HISTORY);
        objectsTemp.add(R.string.trade_list_header_history);
        if (underlyingItems != null)
        {
            int i = 0;
            for (final PositionTradeDTOKey id : underlyingItems)
            {
                ExpandableTradeItem item = new ExpandableTradeItem(id, i == 0);
                item.setExpanded(i == 0);
                items.add(item);
                ++i;
            }

            for (Object item : items)
            {
                itemTypesTemp.add(ITEM_TYPE_TRADE);
                objectsTemp.add(item);
            }
        }
        else
        {
            itemTypesTemp.add(ITEM_TYPE_TRADE_LOADING);
            objectsTemp.add(new LoadingDTO()
            {
            });
        }

        this.itemTypes = itemTypesTemp;
        this.objects = objectsTemp;
    }

    @Override protected ExpandableTradeItem wrap(final PositionTradeDTOKey underlyingItem)
    {
        return new ExpandableTradeItem(underlyingItem);
    }

    @Override public int getViewTypeCount()
    {
        return 6;
    }

    @Override public int getCount()
    {
        return itemTypes.size();
    }

    @Override public int getItemViewType(int position)
    {
        return itemTypes.get(position);
    }

    @Override public Object getItem(int position)
    {
        return objects.get(position);
    }

    public int getPositionLayoutResId(@NonNull PositionDTO position)
    {
        Boolean isClosed = position.isClosed();
        if (isClosed != null && isClosed && position instanceof PositionInPeriodDTO)
        {
            return LAYOUT_RES_ID_POSITION_IN_PERIOD_CLOSED;
        }
        else if (isClosed != null && isClosed)
        {
            return LAYOUT_RES_ID_POSITION_CLOSED;
        }
        else if (position instanceof PositionInPeriodDTO)
        {
            return LAYOUT_RES_ID_POSITION_IN_PERIOD_OPEN;
        }
        else
        {
            return LAYOUT_RES_ID_POSITION_OPEN;
        }
    }

    @Override @NonNull public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        int itemType = getItemViewType(position);
        Object item = getItem(position);

        switch (itemType)
        {
            case ITEM_TYPE_HEADER_POSITION_SUMMARY:
                if (convertView == null)
                {
                    convertView = getInflater().inflate(LAYOUT_RES_ID_ITEM_HEADER, viewGroup, false);
                }
                ((BaseListHeaderView) convertView).setHeaderTextContent((int) item);
                break;

            case ITEM_TYPE_POSITION_SUMMARY:
                if (shownPositionDTO == null)
                {
                    throw new IllegalArgumentException("Type cannot be PositionSummary when shownPosition is null");
                }
                if (convertView == null)
                {
                    convertView = getInflater().inflate(getPositionLayoutResId(shownPositionDTO.first), viewGroup, false);
                    View hintForward = convertView.findViewById(R.id.hint_forward);
                    if (hintForward != null)
                    {
                        hintForward.setVisibility(View.GONE);
                    }
                }

                // TODO better?
                ((PositionView) convertView).display(new PositionView.DTO(convertView.getResources(),
                        new ExpandableListItem<>(true, shownPositionDTO.first),
                        shownPositionDTO.second));
                break;

            case ITEM_TYPE_POSITION_LOADING:
                if (convertView == null)
                {
                    convertView = getInflater().inflate(LAYOUT_RES_ID_POSITION_LOADING, viewGroup, false);
                }
                break;

            case ITEM_TYPE_HEADER_TRADE_HISTORY:
                if (convertView == null)
                {
                    convertView = getInflater().inflate(LAYOUT_RES_ID_ITEM_HEADER, viewGroup, false);
                }
                ((BaseListHeaderView) convertView).setHeaderTextContent((int) item);
                break;

            case ITEM_TYPE_TRADE:
                convertView = conditionalInflate(position, convertView, viewGroup);
                ((TradeListItemView) convertView).display((ExpandableTradeItem) item);
                toggleExpanded((ExpandableTradeItem) item, convertView);
                break;

            case ITEM_TYPE_TRADE_LOADING:
                if (convertView == null)
                {
                    convertView = getInflater().inflate(LAYOUT_RES_ID_ITEM_TRADE_LOADING, viewGroup, false);
                }
                break;

            default:
                throw new IllegalStateException("Unknown ItemType " + itemType);
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
            case ITEM_TYPE_HEADER_POSITION_SUMMARY:
            case ITEM_TYPE_POSITION_SUMMARY:
            case ITEM_TYPE_POSITION_LOADING:
            case ITEM_TYPE_HEADER_TRADE_HISTORY:
            case ITEM_TYPE_TRADE_LOADING:
                return false;

            case ITEM_TYPE_TRADE:
                return true;

            default:
                throw new IllegalStateException("Unknown Item Type " + itemType);
        }
    }

    public static class ExpandableTradeItem extends ExpandableListItem<PositionTradeDTOKey>
    {
        private final boolean lastTrade;

        //<editor-fold desc="Constructors">
        public ExpandableTradeItem(final PositionTradeDTOKey key)
        {
            this(key, false);
        }

        public ExpandableTradeItem(final PositionTradeDTOKey key, final boolean lastTrade)
        {
            super(key);
            this.lastTrade = lastTrade;
        }
        //</editor-fold>

        public boolean isLastTrade()
        {
            return this.lastTrade;
        }
    }
}
