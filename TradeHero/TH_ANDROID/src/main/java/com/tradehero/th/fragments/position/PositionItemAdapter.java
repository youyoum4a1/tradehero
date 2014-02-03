package com.tradehero.th.fragments.position;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;

/** Created with IntelliJ IDEA. User: tho Date: 11/6/13 Time: 8:10 PM Copyright (c) TradeHero */
public class PositionItemAdapter extends AbstractPositionItemAdapter<PositionDTO>
{
    public PositionItemAdapter(Context context, LayoutInflater inflater, int headerLayoutId,
            int lockedPositionLayoutId, int openPositionLayoutId, int closedPositionLayoutId, int positionNothingId)
    {
        super(context, inflater, headerLayoutId, lockedPositionLayoutId, openPositionLayoutId, closedPositionLayoutId, positionNothingId);
    }
}
