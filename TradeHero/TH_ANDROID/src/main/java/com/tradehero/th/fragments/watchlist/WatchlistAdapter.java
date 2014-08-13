package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import org.jetbrains.annotations.NotNull;

public class WatchlistAdapter extends ArrayDTOAdapterNew<WatchlistPositionDTO, WatchlistItemView>
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

    public void remove(@NotNull SecurityId object)
    {
        WatchlistPositionDTO item;
        for (int position = 0, size = getCount(); position < size; position++)
        {
            item = getItem(position);
            if (item.securityDTO.getSecurityId().equals(object))
            {
                remove(item);
            }
        }
    }
}
