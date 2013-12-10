package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableDTOAdapter;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.widget.list.BaseListHeaderView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by julien on 23/10/13
 */
public class TradeListItemAdapter extends ExpandableDTOAdapter<OwnedTradeId, TradeListItemAdapter.ExpandableTradeItem, TradeListItemView>
{
    public static final String TAG = TradeListItemAdapter.class.getName();

    public static final int ITEM_TYPE_BUTTONS = 0;
    public static final int ITEM_TYPE_HEADER_OPEN_POSITION = 1;
    public static final int ITEM_TYPE_OPEN_POSITION = 2;
    public static final int ITEM_TYPE_HEADER_TRADE_HISTORY = 3;
    public static final int ITEM_TYPE_TRADE = 4;

    public static final int LAYOUT_RES_ID_BUTTONS = R.layout.trade_list_header;
    public static final int LAYOUT_RES_ID_ITEM_HEADER = R.layout.trade_list_item_header;

    private List<Integer> itemTypes;
    private List<Object> objects;

    private OwnedPositionId shownPositionId;
    private WeakReference<TradeListHeaderView.TradeListHeaderClickListener> parentTradeListHeaderClickListenerWeak = new WeakReference<>(null);

    public TradeListItemAdapter(Context context, LayoutInflater inflater)
    {
        super(context, inflater, R.layout.trade_list_item);
        itemTypes = new ArrayList<>();
        objects = new ArrayList<>();
    }

    @Override public void setUnderlyingItems(final List<OwnedTradeId> underlyingItems)
    {
        super.setUnderlyingItems(underlyingItems);

        List<Integer> itemTypesTemp = new ArrayList<>();
        List<Object> objectsTemp = new ArrayList<>();
        if (underlyingItems == null)
        {
            this.items = null;
        }
        else
        {
            this.items = new ArrayList<>(underlyingItems.size());
            int i = 0;
            for (final OwnedTradeId id : underlyingItems)
            {
                ExpandableTradeItem item = new ExpandableTradeItem(id, i == 0);
                item.setExpanded(i == 0);
                items.add(item);
                ++i;
            }

            itemTypesTemp.add(ITEM_TYPE_BUTTONS);
            objectsTemp.add("Buttons");

            itemTypesTemp.add(ITEM_TYPE_HEADER_TRADE_HISTORY);
            objectsTemp.add(R.string.trade_list_header_history);

            for (Object item : items)
            {
                itemTypesTemp.add(ITEM_TYPE_TRADE);
                objectsTemp.add(item);
            }
        }
        this.itemTypes = itemTypesTemp;
        this.objects = objectsTemp;
    }

    @Override protected void fineTune(int position, ExpandableTradeItem dto, TradeListItemView convertView)
    {
    }

    @Override protected ExpandableTradeItem wrap(final OwnedTradeId underlyingItem)
    {
        return new ExpandableTradeItem(underlyingItem);
    }

    public OwnedPositionId getShownPositionId()
    {
        return shownPositionId;
    }

    public void setShownPositionId(OwnedPositionId shownPositionId)
    {
        this.shownPositionId = shownPositionId;
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param tradeListHeaderClickListener
     */
    public void setTradeListHeaderClickListener(TradeListHeaderView.TradeListHeaderClickListener tradeListHeaderClickListener)
    {
        this.parentTradeListHeaderClickListenerWeak = new WeakReference<>(tradeListHeaderClickListener);
    }

    @Override public int getViewTypeCount()
    {
        return 5;
    }

    @Override public int getCount()
    {
        return itemTypes.size();
    }

    @Override public int getItemViewType(int position)
    {
        if (position < itemTypes.size())
        {
            return itemTypes.get(position);
        }
        return ITEM_TYPE_BUTTONS;
    }

    @Override public Object getItem(int position)
    {
        if (position < objects.size())
        {
            return objects.get(position);
        }
        return "Buttons";
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        int itemType = getItemViewType(position);
        Object item = getItem(position);

        switch (itemType)
        {
            case ITEM_TYPE_BUTTONS:
                if (!(convertView instanceof TradeListHeaderView))
                {
                    convertView = inflater.inflate(LAYOUT_RES_ID_BUTTONS, viewGroup, false);
                }
                ((TradeListHeaderView) convertView).setListener(parentTradeListHeaderClickListenerWeak.get());
                ((TradeListHeaderView) convertView).bindOwnedPositionId(shownPositionId);
                break;

            case ITEM_TYPE_HEADER_OPEN_POSITION:
                break;

            case ITEM_TYPE_OPEN_POSITION:
                break;

            case ITEM_TYPE_HEADER_TRADE_HISTORY:
                if (!(convertView instanceof BaseListHeaderView))
                {
                    convertView = inflater.inflate(LAYOUT_RES_ID_ITEM_HEADER, viewGroup, false);
                }
                ((BaseListHeaderView) convertView).setHeaderTextContent((int) item);
                break;

            case ITEM_TYPE_TRADE:
                if (!(convertView instanceof TradeListItemView))
                {
                    convertView = inflater.inflate(layoutResourceId, viewGroup, false);
                }
                ((TradeListItemView) convertView).display((ExpandableTradeItem) item);
                toggleExpanded((ExpandableTradeItem) item, convertView);
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
            case ITEM_TYPE_BUTTONS:
            case ITEM_TYPE_HEADER_OPEN_POSITION:
            case ITEM_TYPE_HEADER_TRADE_HISTORY:
                return false;

            case ITEM_TYPE_OPEN_POSITION:
            case ITEM_TYPE_TRADE:
                return true;

            default:
                throw new IllegalStateException("Unknown Item Type " + itemType);
        }
    }

    public static class ExpandableTradeItem extends ExpandableListItem<OwnedTradeId>
    {
        private boolean lastTrade;

        public ExpandableTradeItem(final OwnedTradeId model)
        {
            this(model, false);
        }

        public ExpandableTradeItem(final OwnedTradeId model, final boolean lastTrade)
        {
            super(model);
            this.lastTrade = lastTrade;
        }

        public boolean isLastTrade()
        {
            return this.lastTrade;
        }
    }
}
