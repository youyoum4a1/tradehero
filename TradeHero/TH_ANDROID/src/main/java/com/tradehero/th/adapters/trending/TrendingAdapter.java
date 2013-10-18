package com.tradehero.th.adapters.trending;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.widget.trending.TrendingSecurityView;

public class TrendingAdapter extends DTOAdapter<SecurityCompactDTO, TrendingSecurityView>
        implements AbsListView.OnScrollListener
{
    private final static String TAG = TrendingAdapter.class.getSimpleName();

    /** The layout for the trending view */
    public static final int SECURITY_TRENDING_CELL_LAYOUT = R.layout.trending_grid_item;
    /** The layout for the searching view */
    public static final int SECURITY_SEARCH_CELL_LAYOUT = R.layout.search_stock_item;

    private int layoutResourceId;
    private int scrollState;

    public TrendingAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public long getItemId(int i)
    {
        long itemId = ((SecurityCompactDTO) getItem(i)).getSecurityId().hashCode();
        //THLog.d(TAG, "getItemId " + i + " - " + itemId);
        return itemId;
    }

    @Override protected View getView(int position, final TrendingSecurityView convertView)
    {
        //THLog.d(TAG, "getView position:" + position);
        convertView.post(new Runnable()
        {
            @Override public void run()
            {
                convertView.loadImages();
            }
        });
        return convertView;
    }

    @Override public void onScrollStateChanged(AbsListView absListView, int state)
    {
        scrollState = state;
    }

    @Override public void onScroll(AbsListView absListView, int i, int i2, int i3)
    {
    }
}