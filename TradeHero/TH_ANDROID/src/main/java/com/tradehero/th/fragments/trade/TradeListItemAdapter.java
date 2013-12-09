package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableDTOAdapter;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.trade.OwnedTradeId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by julien on 23/10/13
 */
public class TradeListItemAdapter extends ExpandableDTOAdapter<OwnedTradeId, TradeListItemAdapter.ExpandableTradeItem, TradeListItemView>
{
    public static final String TAG = TradeListItemAdapter.class.getName();

    public TradeListItemAdapter(Context context, LayoutInflater inflater)
    {
        super(context, inflater, R.layout.trade_list_item);
    }

    @Override public void setUnderlyingItems(final List<OwnedTradeId> underlyingItems)
    {
        super.setUnderlyingItems(underlyingItems);

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
                TradeListItemAdapter.ExpandableTradeItem item = new TradeListItemAdapter.ExpandableTradeItem(id, i == 0);
                item.setExpanded(i == 0);
                items.add(item);
                ++i;
            }
        }
    }

    @Override protected void fineTune(int position, ExpandableTradeItem dto, TradeListItemView convertView)
    {
    }

    @Override protected ExpandableTradeItem wrap(final OwnedTradeId underlyingItem)
    {
        return new ExpandableTradeItem(underlyingItem);
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
