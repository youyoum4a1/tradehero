package com.tradehero.th.adapters.portfolio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.widget.portfolio.PortfolioHeaderItemView;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class PortfolioItemHeaderAdapter extends DTOAdapter<OwnedPortfolioId, PortfolioHeaderItemView>
{
    public static final String TAG = PortfolioItemHeaderAdapter.class.getName();

    public PortfolioItemHeaderAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public long getItemId(int i)
    {
        long itemId = getItem(i).hashCode();
        THLog.d(TAG, "getItemId " + i + " - " + itemId);
        return itemId;
    }

    @Override protected View getView(int position, PortfolioHeaderItemView convertView)
    {
        return convertView;
    }
}
