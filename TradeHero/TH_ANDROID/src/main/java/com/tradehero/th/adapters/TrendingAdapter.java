package com.tradehero.th.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.fedorvlasov.lazylist.ImageLoader;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.widget.trending.TrendingSecurityView;
import java.util.List;

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
        // TODO ensure this helps have a smoother experience
        return true;
    }

    @Override protected View getView(int position, final TrendingSecurityView convertView)
    {
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
        //To change body of implemented methods use File | Settings | File Templates.
    }
}