package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.security.SecurityId;

public class WatchlistAdapter extends ArrayDTOAdapter<SecurityId, WatchlistItemView>
{
    private boolean showGainLossPercentage = true;

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

    public int getIndexOf(SecurityId securityId)
    {
        return items == null ? -1 : items.indexOf(securityId);
    }
}
