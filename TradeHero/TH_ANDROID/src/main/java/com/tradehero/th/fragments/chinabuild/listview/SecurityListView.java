package com.tradehero.th.fragments.chinabuild.listview;

import android.content.Context;
import android.util.AttributeSet;
import butterknife.ButterKnife;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.utils.DaggerUtils;

public class SecurityListView extends PullToRefreshListView
{
    //<editor-fold desc="Constructors">
    public SecurityListView(Context context)
    {
        super(context);
    }

    public SecurityListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SecurityListView(Context context, Mode mode)
    {
        super(context, mode);
    }

    public SecurityListView(Context context, Mode mode, AnimationStyle style)
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
