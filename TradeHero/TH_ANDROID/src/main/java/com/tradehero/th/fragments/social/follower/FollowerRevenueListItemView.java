package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.utils.SecurityUtils;

public class FollowerRevenueListItemView extends FollowerListItemView
{
    private static final int INDEX_VIEW_REVENUE = 0;
    private static final int INDEX_VIEW_FREE = 1;

    @InjectView(R.id.revenue_switcher) ViewSwitcher typeSwitcher;
    @InjectView(R.id.follower_revenue) TextView revenueInfo;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public FollowerRevenueListItemView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public FollowerRevenueListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public FollowerRevenueListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    //<editor-fold desc="Display Methods">
    @Override public void display()
    {
        super.display();
        displayType();
        displayRevenue();
    }

    public void displayType()
    {
        if (typeSwitcher != null)
        {
            if (userFollowerDTO != null)
            {
                typeSwitcher.setDisplayedChild(userFollowerDTO.isFreeFollow ?
                INDEX_VIEW_FREE :
                INDEX_VIEW_REVENUE);
            }
        }
    }

    public void displayRevenue()
    {
        if (revenueInfo != null)
        {
            if (userFollowerDTO != null)
            {
                THSignedNumber revenue = THSignedMoney.builder(userFollowerDTO.totalRevenue)
                        .currency(SecurityUtils.getDefaultCurrency())
                        .build();
                revenueInfo.setText(revenue.toString());
            }
            else
            {
                revenueInfo.setText(R.string.na);
            }
        }
    }
    //</editor-fold>
}
