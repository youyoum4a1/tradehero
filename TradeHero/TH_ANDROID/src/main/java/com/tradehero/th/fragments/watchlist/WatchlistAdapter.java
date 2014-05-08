package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.security.SecurityId;

public class WatchlistAdapter extends ArrayDTOAdapterNew<SecurityId, WatchlistItemView>
{
    private boolean showGainLossPercentage = true;

    public WatchlistAdapter(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }

    @Override public WatchlistItemView getView(int position, View convertView, ViewGroup viewGroup)
    {
        WatchlistItemView view = super.getView(position, convertView, viewGroup);
        view.displayPlPercentage(showGainLossPercentage);
        return view;
    }

    public void setShowGainLossPercentage(boolean show)
    {
        showGainLossPercentage = show;
    }
}
