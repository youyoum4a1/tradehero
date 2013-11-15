package com.tradehero.th.fragments.position;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 12:53 PM To change this template use File | Settings | File Templates. */
public class PositionNothingView extends RelativeLayout
{
    private TextView description;

    //<editor-fold desc="Constructors">
    public PositionNothingView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public PositionNothingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionNothingView(Context context)
    {
        super(context);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
    }

    private void initViews()
    {
        description = (TextView) findViewById(R.id.position_nothing_description);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        display();
    }

    public void display()
    {
        if (description != null)
        {
            description.setText(R.string.position_nothing_description);
        }
    }
}
