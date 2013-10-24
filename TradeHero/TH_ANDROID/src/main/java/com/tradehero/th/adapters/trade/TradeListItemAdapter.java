package com.tradehero.th.adapters.trade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.widget.trade.TradeListItemView;

/**
 * Created by julien on 23/10/13
 */
public class TradeListItemAdapter extends DTOAdapter<OwnedTradeId, TradeListItemView>
{
    public static final String TAG = TradeListItemAdapter.class.getName();

    public TradeListItemAdapter(Context context, LayoutInflater inflater)
    {
        super(context, inflater, R.layout.trade_list_item);
    }

    @Override protected void fineTune(int position, OwnedTradeId dto, TradeListItemView convertView)
    {
        // Nothing to do
    }
}
