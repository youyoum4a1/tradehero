package com.ayondo.academy.fragments.watchlist;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.ayondo.academy.R;
import com.ayondo.academy.adapters.ArrayDTOAdapterNew;
import com.ayondo.academy.api.security.SecurityId;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTO;
import com.ayondo.academy.utils.GraphicUtil;

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

        View front = view.findViewById(R.id.front);
        if (front != null)
        {
            GraphicUtil.setEvenOddBackground(position, front);
        }
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
