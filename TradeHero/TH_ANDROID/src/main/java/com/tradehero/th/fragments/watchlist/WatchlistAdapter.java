package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.security.SecurityId;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/10/14 Time: 4:39 PM Copyright (c) TradeHero
 */
public class WatchlistAdapter extends ArrayDTOAdapter<SecurityId, WatchlistItemView>
{
    private boolean showGainLossPercentage;

    public WatchlistAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(int position, SecurityId dto, WatchlistItemView dtoView)
    {
        dtoView.displayPlPercentage(showGainLossPercentage);
    }

    public void setShowGainLossPercentage(boolean show)
    {
        showGainLossPercentage = show;
    }
}
