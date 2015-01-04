package com.tradehero.th.fragments.position;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

public class PositionSectionHeaderItemView extends RelativeLayout
{
    public static final int INFO_TYPE_LONG = 0;
    public static final int INFO_TYPE_SHORT = 1;
    public static final int INFO_TYPE_CLOSED = 2;

    protected TextView headerText;
    protected TextView headerGetInfo;
    protected TextView timeBaseText;
    protected SimpleDateFormat sdf;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public PositionSectionHeaderItemView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PositionSectionHeaderItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PositionSectionHeaderItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        sdf = new SimpleDateFormat(getContext().getString(R.string.data_format_dd_mmm_yyyy));
        initViews();
    }

    protected void initViews()
    {
        headerText = (TextView) findViewById(R.id.header_text);
        timeBaseText = (TextView) findViewById(R.id.header_time_base);
        headerGetInfo = (TextView) findViewById(R.id.header_get_info);
    }

    public void setHeaderGetInfoVisable(int visable ,OnClickListener onClickListener)
    {
        headerGetInfo.setVisibility(visable);
        if(visable == View.VISIBLE)
        {
            headerGetInfo.setOnClickListener(onClickListener);
        }
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
