package com.ayondo.academy.fragments.achievement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.ayondo.academy.R;
import com.ayondo.academy.adapters.DTOAdapterNew;
import com.ayondo.academy.api.achievement.AchievementCategoryDTO;
import com.ayondo.academy.api.achievement.AchievementCategoryDTOList;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.fragments.base.DashboardFragment;
import com.ayondo.academy.persistence.achievement.AchievementCategoryListCacheRx;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

@Routable("achievements")
public class AchievementListFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_USER_ID = AchievementListFragment.class.getName() + ".userId";

    @Bind(R.id.generic_swipe_refresh_layout) protected SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.generic_ptr_list) protected ListView listView;
    @Bind(android.R.id.progress) protected ProgressBar progressBar;
    @Bind(android.R.id.empty) protected View emptyView;

    protected DTOAdapterNew<AchievementCategoryDTO> achievementListAdapter;

    @Inject AchievementCategoryListCacheRx achievementCategoryListCache;
    private Subscription achievementListSubscription;
    UserBaseKey shownUserId;

    public static void putUserId(@NonNull Bundle bundle, @NonNull UserBaseKey userBaseKey)
    {
        bundle.putBundle(BUNDLE_KEY_USER_ID, userBaseKey.getArgs());
    }

    @NonNull private UserBaseKey getUserId()
    {
        return new UserBaseKey(getArguments().getBundle(BUNDLE_KEY_USER_ID));
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_generic_list, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        displayProgress();
        hideEmpty();
        init();
        initAdapter();
        listView.setOnScrollListener(fragmentElements.get().getListViewScrollListener());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.achievements);
    }

    protected void init()
    {
        this.shownUserId = getUserId();
    }

    private void initAdapter()
    {
        achievementListAdapter = new DTOAdapterNew<>(getActivity(), R.layout.achievement_cell_view);
        listView.setAdapter(achievementListAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                swipeRefreshLayout.setRefreshing(true);
                AchievementListFragment.this.attachAndFetchAchievementCategoryListener();
            }
        });
    }

    @Override public void onStart()
    {
        attachAndFetchAchievementCategoryListener();
        super.onStart();
    }

    protected void attachAndFetchAchievementCategoryListener()
    {
        hideEmpty();
        unsubscribe(achievementListSubscription);
        achievementListSubscription = AppObservable.bindSupportFragment(this,
                achievementCategoryListCache.get(shownUserId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<UserBaseKey, AchievementCategoryDTOList>>()
                        {
                            @Override public void call(Pair<UserBaseKey, AchievementCategoryDTOList> pair)
                            {
                                AchievementListFragment.this.onReceivedAchievementList(pair);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable error)
                            {
                                AchievementListFragment.this.onReceivedAchievementFailed(error);
                            }
                        });
    }

    private void displayProgress()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress()
    {
        progressBar.setVisibility(View.GONE);
    }

    private void displayEmpty()
    {
        emptyView.setVisibility(View.VISIBLE);
    }

    private void hideEmpty()
    {
        emptyView.setVisibility(View.GONE);
    }

    @Override public void onStop()
    {
        unsubscribe(achievementListSubscription);
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        listView.setOnScrollListener(null);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    protected void onReceivedAchievementList(Pair<UserBaseKey, AchievementCategoryDTOList> pair)
    {
        swipeRefreshLayout.setRefreshing(false);
        achievementListAdapter.clear();
        achievementListAdapter.addAll(pair.second);
        achievementListAdapter.notifyDataSetChanged();
        hideProgress();
        if (achievementListAdapter.isEmpty())
        {
            displayEmpty();
        }
    }

    protected void onReceivedAchievementFailed(Throwable e)
    {
        swipeRefreshLayout.setRefreshing(false);
        THToast.show(getString(R.string.error_fetch_achievements));
        Timber.e(e, "Error fetching the list of competition info cell");
        hideProgress();
        displayEmpty();
    }
}
