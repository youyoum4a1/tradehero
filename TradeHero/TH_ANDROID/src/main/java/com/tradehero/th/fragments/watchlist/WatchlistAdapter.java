package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;

public class WatchlistAdapter extends ArrayDTOAdapterNew<WatchlistPositionDTO, WatchlistItemView>
{
    private boolean showGainLossPercentage = true;

    //<editor-fold desc="Constructors">
    public WatchlistAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>

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

    public void remove(@NonNull SecurityId object)
    {
        WatchlistPositionDTO item;
        for (int position = 0; position < getCount(); position++)
        {
            item = getItem(position);
            if (item.securityDTO.getSecurityId().equals(object))
            {
                remove(item);
            }
        }
    }
}
