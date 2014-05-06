package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;

public class TradeListItemAdapter extends AbstractTradeListItemAdapter<PositionDTO>
{
    public static final int LAYOUT_RES_ID_POSITION_OPEN = R.layout.position_open_no_period;
    public static final int LAYOUT_RES_ID_POSITION_CLOSED = R.layout.position_closed_no_period;

    //<editor-fold desc="Constructors">
    public TradeListItemAdapter(Context context, LayoutInflater inflater)
    {
        super(context, inflater);
    }
    //</editor-fold>

    @Override public int getOpenPositionLayoutResId()
    {
        return LAYOUT_RES_ID_POSITION_OPEN;
    }

    @Override public int getClosedPositionLayoutResId()
    {
        return LAYOUT_RES_ID_POSITION_CLOSED;
    }
}
