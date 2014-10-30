package com.tradehero.th.fragments.social.follower;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.social.FollowerSummaryCacheRx;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;

public class FollowerRevenueReportFragment extends DashboardFragment
{
    @Inject CurrentUserId currentUserId;
    @Inject FollowerSummaryCacheRx followerSummaryCache;

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
            ButterKnife.inject(followerManagerViewContainer, view);
            followerManagerViewContainer.display();
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
        AndroidObservable.bindFragment(
                this,
                followerManagerViewContainer.getClickedUserFollower())
                .subscribe(this::onListItemClick);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        ButterKnife.reset(followerManagerViewContainer);
        super.onDestroyView();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override public void onDestroy()
    {
        followerManagerViewContainer = null;
        super.onDestroy();
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
        AndroidObservable.bindFragment(
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

    protected void display(@NotNull FollowerSummaryDTO followerSummaryDTO)
    {
        followerManagerViewContainer.display(followerSummaryDTO);
        followerManagerViewContainer.displayChild(FollowerManagerViewContainer.INDEX_VIEW_LIST);
    }
}
