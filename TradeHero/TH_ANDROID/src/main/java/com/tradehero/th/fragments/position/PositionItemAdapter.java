package com.tradehero.th.fragments.position;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.api.position.PositionDTO;

public class PositionItemAdapter extends AbstractPositionItemAdapter<PositionDTO>
{
    public PositionItemAdapter(Context context, LayoutInflater inflater, int headerLayoutId,
            int lockedPositionLayoutId, int openPositionLayoutId, int closedPositionLayoutId, int positionNothingId)
    {
        super(context, inflater, headerLayoutId, lockedPositionLayoutId, openPositionLayoutId, closedPositionLayoutId, positionNothingId);
    }
}
