package com.tradehero.th.fragments.position;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/10/14 Time: 4:39 PM Copyright (c) TradeHero
 */
public class WatchlistAdapter extends ArrayDTOAdapter<SecurityId, WatchlistItemView>
{
    public WatchlistAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(int position, SecurityId dto, WatchlistItemView dtoView)
    {

    }
}
