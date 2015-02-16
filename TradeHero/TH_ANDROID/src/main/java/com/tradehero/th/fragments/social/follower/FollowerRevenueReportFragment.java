package com.tradehero.th.fragments.social.follower;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.social.FollowerSummaryCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.widget.MultiScrollListener;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class FollowerRevenueReportFragment extends DashboardFragment
{
    @Inject CurrentUserId currentUserId;
    @Inject FollowerSummaryCacheRx followerSummaryCache;

    @InjectView(R.id.manage_followers_header) View headerView;
    @InjectView(R.id.follower_list) AbsListView followerListView;
    private FollowerManagerViewContainer followerManagerViewContainer;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        followerManagerViewContainer = new FollowerManagerViewContainer(getActivity());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_store_manage_followers_revenue, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (!view.isInEditMode())
        {
            ButterKnife.inject(this, view);
            followerManagerViewContainer.onCreateView(view);
            followerManagerViewContainer.display();
            headerView.post(new Runnable()
            {
                @Override public void run()
                {
                    FollowerRevenueReportFragment.this.adjustListPadding();
                }
            });
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.social_followers);
    }

    @Override public void onResume()
    {
        super.onResume();
        followerManagerViewContainer.displayChild(FollowerManagerViewContainer.INDEX_VIEW_PROGRESS);
        fetchFollowerSummary();
        AppObservable.bindFragment(
                this,
                followerManagerViewContainer.getClickedUserFollower())
                .subscribe(
                        new Action1<UserFollowerDTO>()
                        {
                            @Override public void call(UserFollowerDTO userFollowerDTO)
                            {
                                FollowerRevenueReportFragment.this.onListItemClick(userFollowerDTO);
                            }
                        },
                        new ToastOnErrorAction());
    }

    @Override public void onDestroyView()
    {
        headerView.removeCallbacks(null);
        ButterKnife.reset(this);
        followerManagerViewContainer.onDestroyView();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        followerManagerViewContainer = null;
        super.onDestroy();
    }

    protected void adjustListPadding()
    {
        if (headerView != null && followerListView != null)
        {
            int headerHeight = headerView.getMeasuredHeight();
            QuickReturnListViewOnScrollListener headerQuickReturnScrollListener =
                    new QuickReturnListViewOnScrollListener(QuickReturnType.HEADER, headerView,
                            -headerHeight, null, 0);

            followerListView.setPadding(
                    followerListView.getPaddingLeft(),
                    headerHeight,
                    followerListView.getPaddingRight(),
                    followerListView.getPaddingBottom());

            followerListView.setOnScrollListener(new MultiScrollListener(
                    dashboardBottomTabsListViewScrollListener.get(),
                    headerQuickReturnScrollListener));
        }
    }

    public void onListItemClick(UserFollowerDTO userFollowerDTO)
    {
        Bundle args = new Bundle();
        FollowerPayoutManagerFragment.put(
                args,
                new FollowerHeroRelationId(
                        currentUserId.get(),
                        userFollowerDTO.id));
        navigator.get().pushFragment(FollowerPayoutManagerFragment.class, args);
    }

    protected void fetchFollowerSummary()
    {
        AppObservable.bindFragment(
                this,
                followerSummaryCache.get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<UserBaseKey, FollowerSummaryDTO>>()
                {
                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                        THToast.show(R.string.error_fetch_follower);
                    }

                    @Override public void onNext(Pair<UserBaseKey, FollowerSummaryDTO> pair)
                    {
                        display(pair.second);
                    }
                });
    }

    protected void display(@NonNull FollowerSummaryDTO followerSummaryDTO)
    {
        followerManagerViewContainer.display(followerSummaryDTO);
        followerManagerViewContainer.displayChild(FollowerManagerViewContainer.INDEX_VIEW_LIST);
    }
}
