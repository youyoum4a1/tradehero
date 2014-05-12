package com.tradehero.th.fragments.position;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PositionSectionHeaderItemView extends RelativeLayout
{
    protected TextView headerText;
    protected TextView timeBaseText;
    protected SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");

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
        initViews();
    }

    protected void initViews()
    {
        headerText = (TextView) findViewById(R.id.header_text);
        timeBaseText = (TextView) findViewById(R.id.header_time_base);
    }

    public void setHeaderTextContent(String text)
    {
        if (headerText != null)
        {
            headerText.setText(text);
        }
    }

    public void setTimeBaseTextContent(Date left, Date right)
    {
        if (timeBaseText != null)
        {
            if (left != null || right != null)
            {
                timeBaseText.setText(getResources().getString(
                        R.string.position_list_header_time_base,
                        left != null ? sdf.format(left) : "",
                        right != null ? sdf.format(right) : ""));
            }
            else
            {
                timeBaseText.setText("");
            }
        }
    }
}
