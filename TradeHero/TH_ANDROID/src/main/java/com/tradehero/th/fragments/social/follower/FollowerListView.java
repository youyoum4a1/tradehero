package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class FollowerListView extends ListView
{
    //<editor-fold desc="Constructors">
    public FollowerListView(Context context)
    {
        super(context);
    }

    public FollowerListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FollowerListView(Context context, AttributeSet attrs, int defStyle)
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
