package com.tradehero.th.widget.position;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 10/17/13 Time: 7:09 PM To change this template use File | Settings | File Templates. */
public class PositionSectionHeaderItemView extends RelativeLayout
{
    private TextView headerText;

    //<editor-fold desc="Constructors">
    public PositionSectionHeaderItemView(Context context)
    {
        super(context);
    }

    public PositionSectionHeaderItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionSectionHeaderItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        headerText = (TextView) findViewById(R.id.header_text);
    }

    public void setHeaderTextContent(String text)
    {
        if (headerText != null)
        {
            headerText.setText(text);
        }
    }
}
