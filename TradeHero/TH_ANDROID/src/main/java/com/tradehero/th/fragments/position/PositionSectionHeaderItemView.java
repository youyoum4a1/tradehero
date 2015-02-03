package com.tradehero.th.fragments.position;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.utils.AlertDialogUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PositionSectionHeaderItemView extends RelativeLayout
{
    public static final int INFO_TYPE_LONG = 0;
    public static final int INFO_TYPE_SHORT = 1;
    public static final int INFO_TYPE_CLOSED = 2;

    @InjectView(R.id.header_text) protected TextView headerText;
    @InjectView(R.id.header_time_base) protected TextView timeBaseText;
    protected SimpleDateFormat sdf;

    private int type = -1;

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
        sdf = new SimpleDateFormat(getContext().getString(R.string.data_format_dd_mmm_yyyy), Locale.ENGLISH);
        ButterKnife.inject(this);
    }

    public void setHeaderTextContent(String text)
    {
        if (headerText != null)
        {
            headerText.setText(text);
        }
    }

    public void setTimeBaseTextContent(@Nullable Date left, @Nullable Date right)
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

    public void setType(int type)
    {
        this.type = type;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.header_get_info)
    protected void handleInfoClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        int resInt = -1;
        if (type == PositionSectionHeaderItemView.INFO_TYPE_LONG)
        {
            resInt = R.string.position_long_info;
        }
        else if (type == PositionSectionHeaderItemView.INFO_TYPE_SHORT)
        {
            resInt = R.string.position_short_info;
        }
        else if (type == PositionSectionHeaderItemView.INFO_TYPE_CLOSED)
        {
            resInt = R.string.position_close_info;
        }

        if (resInt != -1)
        {
            AlertDialogUtil.popWithNegativeButton(getContext(), R.string.position_title_info, resInt, R.string.ok);
        }
    }
}
