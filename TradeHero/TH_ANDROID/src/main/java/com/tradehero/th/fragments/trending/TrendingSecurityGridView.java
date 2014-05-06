package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class TrendingSecurityGridView extends GridView
{
    //<editor-fold desc="Description">
    public TrendingSecurityGridView(Context context)
    {
        super(context);
    }

    public TrendingSecurityGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TrendingSecurityGridView(Context context, AttributeSet attrs, int defStyle)
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
