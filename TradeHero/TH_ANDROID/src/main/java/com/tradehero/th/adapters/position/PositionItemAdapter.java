package com.tradehero.th.adapters.position;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.FiledPositionId;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.widget.portfolio.PortfolioHeaderItemView;
import com.tradehero.th.widget.position.PositionQuickView;
import com.tradehero.th.widget.position.PositionQuickViewHolder;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class PositionItemAdapter extends DTOAdapter<FiledPositionId, PositionQuickView>
{
    public static final String TAG = PositionItemAdapter.class.getName();

    public PositionItemAdapter(Context context, LayoutInflater inflater)
    {
        super(context, inflater, R.layout.position_quick);
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

    @Override protected View getView(int position, PositionQuickView convertView)
    {
        return convertView;
    }
}
