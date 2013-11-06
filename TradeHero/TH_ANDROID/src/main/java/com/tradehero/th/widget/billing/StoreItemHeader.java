package com.tradehero.th.widget.billing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 7:09 PM To change this template use File | Settings | File Templates. */
public class StoreItemHeader extends LinearLayout
{
    public static final String TAG = StoreItemHeader.class.getSimpleName();

    private TextView title;
    private int titleResId;

    //<editor-fold desc="Constructors">
    public StoreItemHeader(Context context)
    {
        super(context);
    }

    public StoreItemHeader(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StoreItemHeader(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
    }

    protected void initViews()
    {
        title = (TextView) findViewById(R.id.title);
    }

    public void setTitleResId(int titleResId)
    {
        this.titleResId = titleResId;
        displayTitle();
    }

    protected void displayTitle()
    {
        if (title != null)
        {
            title.setText(titleResId);
        }
    }
}
