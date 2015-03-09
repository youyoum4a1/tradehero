package com.tradehero.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;
import timber.log.Timber;

public class THListView extends ListView
{

    public THListView(Context context)
    {
        super(context);
    }

    public THListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public THListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void dispatchDraw(Canvas canvas)
    {
        try
        {
            super.dispatchDraw(canvas);
        }
        catch (Exception e)
        {
            Timber.d("log for listview dispathDraw:" + e.toString());
        }
    }
}