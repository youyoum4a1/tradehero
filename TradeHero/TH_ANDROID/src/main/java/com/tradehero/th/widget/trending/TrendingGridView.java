package com.tradehero.th.widget.trending;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/** Created with IntelliJ IDEA. User: xavier Date: 9/13/13 Time: 4:36 PM To change this template use File | Settings | File Templates. */
public class TrendingGridView extends GridView
{
    public static final String TAG = TrendingGridView.class.getSimpleName();

    //<editor-fold desc="Description">
    public TrendingGridView(Context context)
    {
        super(context);
    }

    public TrendingGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TrendingGridView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    protected void init ()
    {
    }
}
