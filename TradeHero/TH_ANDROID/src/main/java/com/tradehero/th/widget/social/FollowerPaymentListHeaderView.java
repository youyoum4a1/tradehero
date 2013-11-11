package com.tradehero.th.widget.social;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 5:14 PM To change this template use File | Settings | File Templates. */
public class FollowerPaymentListHeaderView extends RelativeLayout
{
    private TextView headerTextView;

    //<editor-fold desc="Description">
    public FollowerPaymentListHeaderView(Context context)
    {
        super(context);
    }

    public FollowerPaymentListHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FollowerPaymentListHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
    }

    private void initViews()
    {
        headerTextView = (TextView) findViewById(R.id.header_text);
    }

    public void setHeaderTextContent(String text)
    {
        if (headerTextView != null)
        {
            headerTextView.setText(text);
        }
    }
}
