package com.tradehero.th.fragments.portfolio;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class PortfolioListView extends ListView
{
    //<editor-fold desc="Constructors">
    public PortfolioListView(Context context)
    {
        super(context);
    }

    public PortfolioListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PortfolioListView(Context context, AttributeSet attrs, int defStyle)
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
