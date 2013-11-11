package com.tradehero.th.widget.social;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.social.FollowerTransactionDTO;
import com.tradehero.th.utils.SecurityUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:28 PM To change this template use File | Settings | File Templates. */
public class FollowerPaymentListItemView extends RelativeLayout implements DTOView<FollowerTransactionDTO>
{
    public static final String TAG = FollowerPaymentListItemView.class.getName();

    private TextView durationInfo;
    private TextView dateStart;
    private TextView revenueInfo;

    private FollowerTransactionDTO userFollowerDTO;

    //<editor-fold desc="Constructors">
    public FollowerPaymentListItemView(Context context)
    {
        super(context);
    }

    public FollowerPaymentListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FollowerPaymentListItemView(Context context, AttributeSet attrs, int defStyle)
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
        durationInfo = (TextView) findViewById(R.id.duration_info);
        dateStart = (TextView) findViewById(R.id.date_start);
        revenueInfo = (TextView) findViewById(R.id.revenue_info);
    }

    public void display(FollowerTransactionDTO followerDTO)
    {
        linkWith(followerDTO, true);
    }

    public void linkWith(FollowerTransactionDTO followerDTO, boolean andDisplay)
    {
        this.userFollowerDTO = followerDTO;
        if (andDisplay)
        {
            displayDurationInfo();
            displayDateStart();
            displayRevenue();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayDurationInfo();
        displayDateStart();
        displayRevenue();
    }

    public void displayDurationInfo()
    {
        if (durationInfo != null)
        {
            if (userFollowerDTO != null)
            {
                // TODO get the duration from somewhere
                durationInfo.setText(String.format(getResources().getString(R.string.manage_follower_payment_duration), 90));
            }
            else
            {
                durationInfo.setText(String.format(getResources().getString(R.string.manage_follower_payment_duration), 0));
            }
        }
    }

    public void displayDateStart()
    {
        if (dateStart != null)
        {
            if (userFollowerDTO != null)
            {
                SimpleDateFormat sdf = new SimpleDateFormat(getContext().getString(R.string.leaderboard_datetime_format));
                dateStart.setText(sdf.format(userFollowerDTO.paidAt));
            }
            else
            {
                dateStart.setText(R.string.na);
            }
        }
    }

    public void displayRevenue()
    {
        if (revenueInfo != null)
        {
            if (userFollowerDTO != null)
            {
                revenueInfo.setText(String.format(getResources().getString(R.string.manage_followers_revenue_follower), SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, userFollowerDTO.revenue));
            }
            else
            {
                revenueInfo.setText(R.string.na);
            }
        }
    }
    //</editor-fold>
}
