package com.tradehero.th.fragments.billing.management;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 11:42 AM To change this template use File | Settings | File Templates. */
public class HeroListView extends ListView
{
    //<editor-fold desc="Constructors">
    public HeroListView(Context context)
    {
        super(context);
    }

    public HeroListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HeroListView(Context context, AttributeSet attrs, int defStyle)
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


    public static interface HeroListItemListener extends OnItemClickListener
    {
        public void onFollowButtonClicked(AdapterView<?> parent, View view, int position, long id);
    }
}
