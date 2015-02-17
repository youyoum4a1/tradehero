package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.utils.SecurityUtils;
import rx.Observable;
import rx.android.widget.OnItemClickEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Func1;

public class FollowerManagerViewContainer
{
    public static final int INDEX_VIEW_PROGRESS = 0;
    public static final int INDEX_VIEW_LIST = 1;

    @InjectView(R.id.manage_followers_total_revenue) TextView totalRevenue;
    @InjectView(R.id.manage_followers_total_amount_paid) TextView totalAmountPaid;
    @InjectView(R.id.manage_followers_number_followers) TextView followersCount;
    @InjectView(android.R.id.content) ViewSwitcher contentSwitcher;
    // TODO this view and the one in FollowerRevenueReportFragment point to same thing, should be merged.
    @InjectView(R.id.follower_list) AbsListView followerListView;

    private FollowerSummaryDTO followerSummaryDTO;
    private UserFollowerDTOSetAdapter adapter;

    public FollowerManagerViewContainer(@NonNull Context context)
    {
        super();
        adapter = new UserFollowerDTOSetAdapter(context);
    }

    public void onCreateView(@NonNull View view)
    {
        ButterKnife.inject(this, view);
        followerListView.setAdapter(adapter);
    }

    public void onDestroyView()
    {
        ButterKnife.reset(this);
    }

    public void display(@NonNull FollowerSummaryDTO followerSummaryDTO)
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
        }
    }

    public void displayChild(int index)
    {
        if (contentSwitcher != null)
        {
            contentSwitcher.setDisplayedChild(index);
        }
    }

    @NonNull public Observable<UserFollowerDTO> getClickedUserFollower()
    {
        return WidgetObservable.itemClicks(followerListView)
                .map(new Func1<OnItemClickEvent, UserFollowerDTO>()
                {
                    @Override public UserFollowerDTO call(OnItemClickEvent event)
                    {
                        return (UserFollowerDTO) event.parent().getItemAtPosition(event.position());
                    }
                });
    }
}
