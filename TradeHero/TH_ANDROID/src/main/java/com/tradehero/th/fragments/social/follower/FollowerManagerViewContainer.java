package com.tradehero.th.fragments.social.follower;

import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.utils.SecurityUtils;

/**
 * Created by xavier on 12/16/13.
 */
public class FollowerManagerViewContainer
{
    public static final String TAG = FollowerManagerViewContainer.class.getSimpleName();

    public final TextView totalRevenue;
    public final TextView totalAmountPaid;
    public final TextView followersCount;
    public final ListView followerList;
    public final ProgressBar progressBar;

    public FollowerManagerViewContainer(View view)
    {
        super();

        totalRevenue = (TextView) view.findViewById(R.id.manage_followers_total_revenue);
        totalAmountPaid = (TextView) view.findViewById(R.id.manage_followers_total_amount_paid);
        followersCount = (TextView) view.findViewById(R.id.manage_followers_number_followers);
        followerList = (FollowerListView) view.findViewById(R.id.followers_list);
        progressBar = (ProgressBar) view.findViewById(android.R.id.empty);
    }

    public void displayTotalRevenue(FollowerSummaryDTO followerSummaryDTO)
    {
        if (totalRevenue != null)
        {
            if (followerSummaryDTO != null)
            {
                totalRevenue.setText(String.format("%s %,.2f", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, followerSummaryDTO.totalRevenue));
            }
            else
            {
                totalRevenue.setText(R.string.na);
            }
        }
    }

    public void displayTotalAmountPaid(FollowerSummaryDTO followerSummaryDTO)
    {
        if (totalAmountPaid != null)
        {
            if (followerSummaryDTO != null && followerSummaryDTO.payoutSummary != null)
            {
                totalAmountPaid.setText(String.format("%s %,.2f", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, followerSummaryDTO.payoutSummary.totalPayout));
            }
            else if (followerSummaryDTO != null)
            {
                totalAmountPaid.setText("0");
            }
            else
            {
                totalAmountPaid.setText(R.string.na);
            }
        }
    }

    public void displayFollowersCount(FollowerSummaryDTO followerSummaryDTO)
    {
        if (followersCount != null)
        {
            if (followerSummaryDTO != null && followerSummaryDTO.userFollowers != null)
            {
                followersCount.setText(String.format("%d", followerSummaryDTO.userFollowers.size()));
            }
            else if (followerSummaryDTO != null)
            {
                followersCount.setText("0");
            }
            else
            {
                followersCount.setText(R.string.na);
            }
        }
    }
}
