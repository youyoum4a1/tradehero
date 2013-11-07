package com.tradehero.th.adapters.position;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.api.position.InPeriodPositionDTO;

/** Created with IntelliJ IDEA. User: tho Date: 11/6/13 Time: 8:11 PM Copyright (c) TradeHero */
public class InPeriodPositionItemAdapter extends AbstractPositionItemAdapter<InPeriodPositionDTO>
{
    public InPeriodPositionItemAdapter(Context context, LayoutInflater inflater, int headerLayoutId,
            int lockedPositionLayoutId, int openPositionLayoutId, int closedPositionLayoutId, int positionNothingId)
    {
        super(context, inflater, headerLayoutId, lockedPositionLayoutId, openPositionLayoutId, closedPositionLayoutId, positionNothingId);
    }
}
