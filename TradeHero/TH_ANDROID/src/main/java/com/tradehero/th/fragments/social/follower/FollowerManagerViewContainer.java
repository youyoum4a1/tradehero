package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.R;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.rx.view.list.ItemClickDTO;
import com.tradehero.th.rx.view.list.ListViewObservable;
import com.tradehero.th.utils.SecurityUtils;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public class FollowerManagerViewContainer
{
    public static final int INDEX_VIEW_PROGRESS = 0;
    public static final int INDEX_VIEW_LIST = 1;

    @InjectView(R.id.manage_followers_total_revenue) TextView totalRevenue;
    @InjectView(R.id.manage_followers_total_amount_paid) TextView totalAmountPaid;
    @InjectView(R.id.manage_followers_number_followers) TextView followersCount;
    @InjectView(android.R.id.content) ViewSwitcher contentSwitcher;
    @InjectView(R.id.follower_list) PullToRefreshListView pullToRefreshListView;

    private FollowerSummaryDTO followerSummaryDTO;
    private UserFollowerDTOSetAdapter adapter;

    public FollowerManagerViewContainer(@NotNull Context context)
    {
        super();
        adapter = new UserFollowerDTOSetAdapter(context);
    }

    public void display(@NotNull FollowerSummaryDTO followerSummaryDTO)
    {
        this.followerSummaryDTO = followerSummaryDTO;

        adapter.clear();
        adapter.appendTail(followerSummaryDTO.userFollowers);
        adapter.notifyDataSetChanged();
        displayChild(INDEX_VIEW_LIST);

        display();
    }

    public void display()
    {
        if (followerSummaryDTO != null)
        {
            if (totalRevenue != null)
            {
                totalRevenue.setText(
                        THSignedMoney.builder(followerSummaryDTO.totalRevenue)
                                .currency(SecurityUtils.getDefaultCurrency())
                                .build()
                                .toString());
            }

            if (totalAmountPaid != null)
            {
                totalAmountPaid.setText(
                        THSignedMoney.builder(followerSummaryDTO.payoutSummary.totalPayout)
                                .currency(SecurityUtils.getDefaultCurrency())
                                .build()
                                .toString());
            }

            if (followersCount != null)
            {
                followersCount.setText(
                        THSignedNumber.builder(followerSummaryDTO.getPaidFollowerCount())
                                .build()
                                .toString());
            }

            if (pullToRefreshListView != null)
            {
                pullToRefreshListView.setAdapter(adapter);
            }
        }
    }

    public void displayChild(int index)
    {
        if (contentSwitcher != null)
        {
            contentSwitcher.setDisplayedChild(index);
        }
    }

    public Observable<ItemClickDTO> getOnItemClickObservable()
    {
        return ListViewObservable.itemClicks(pullToRefreshListView);
    }

    public Observable<UserFollowerDTO> getClickedUserFollower()
    {
        return getOnItemClickObservable()
                .map(itemClick -> (UserFollowerDTO) itemClick.parent.getItemAtPosition(itemClick.position));
    }
}
