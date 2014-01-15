package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.adapters.OnSizeChangedListener;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.WatchlistService;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import dagger.Lazy;
import javax.inject.Inject;

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
        dtoView.setAdapterOnSizeChangedListener(onSizeChangedListener);
    }

    public void setShowGainLossPercentage(boolean show)
    {
        showGainLossPercentage = show;
    }

    private OnSizeChangedListener onSizeChangedListener = new OnSizeChangedListener()
    {
        @Override public void onSizeChanged(int newHeight)
        {
            notifyDataSetChanged();
        }
    };

    public static interface OnSwipeItemDeletedListener
    {
        void onSwipeItemDeleted(int position);
    }
}
