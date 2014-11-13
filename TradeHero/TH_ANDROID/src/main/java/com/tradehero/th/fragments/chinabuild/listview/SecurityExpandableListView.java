package com.tradehero.th.fragments.chinabuild.listview;

import android.content.Context;
import android.util.AttributeSet;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshExpandableListView;
import com.tradehero.th.utils.DaggerUtils;

public class SecurityExpandableListView extends PullToRefreshExpandableListView
{
    //<editor-fold desc="Constructors">
    public SecurityExpandableListView(Context context)
    {
        super(context);
    }

    public SecurityExpandableListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SecurityExpandableListView(Context context, Mode mode)
    {
        super(context, mode);
    }

    public SecurityExpandableListView(Context context, Mode mode, AnimationStyle style)
    {
        super(context, mode, style);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        DaggerUtils.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        super.setOnItemClickListener(null);
        super.onDetachedFromWindow();
    }
}
