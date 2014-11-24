package com.tradehero.th.fragments.achievement;

import android.os.Bundle;
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
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.achievement.AchievementCategoryListCacheRx;
import javax.inject.Inject;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

@Routable("achievements")
public class AchievementListFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_USER_ID = AchievementListFragment.class.getName() + ".userId";

    @InjectView(R.id.generic_swipe_refresh_layout) protected SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.generic_ptr_list) protected ListView listView;
    @InjectView(android.R.id.progress) protected ProgressBar progressBar;
    @InjectView(android.R.id.empty) protected View emptyView;

    protected AchievementListAdapter achievementListAdapter;

    @Inject AchievementCategoryListCacheRx achievementCategoryListCache;
    UserBaseKey shownUserId;

    public static void putUserId(Bundle bundle, UserBaseKey userBaseKey)
    {
        bundle.putBundle(BUNDLE_KEY_USER_ID, userBaseKey.getArgs());
    }

    private UserBaseKey getUserId()
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
        ButterKnife.inject(this, view);
        displayProgress();
        hideEmpty();
        init();
        initAdapter();
        listView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
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
        achievementListAdapter = new AchievementListAdapter(getActivity(), R.layout.achievement_cell_view);
        listView.setAdapter(achievementListAdapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            AchievementListFragment.this.attachAndFetchAchievementCategoryListener();
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
        AndroidObservable.bindFragment(this,
                achievementCategoryListCache.get(shownUserId))
                .subscribe(createAchievementCategoryListCacheObserver());
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

    @Override public void onDestroyView()
    {
        listView.setOnScrollListener(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected Observer<Pair<UserBaseKey, AchievementCategoryDTOList>> createAchievementCategoryListCacheObserver()
    {
        return new AchievementCategoryListCacheObserver();
    }

    protected class AchievementCategoryListCacheObserver implements Observer<Pair<UserBaseKey, AchievementCategoryDTOList>>
    {
        @Override public void onNext(Pair<UserBaseKey, AchievementCategoryDTOList> pair)
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

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            swipeRefreshLayout.setRefreshing(false);
            THToast.show(getString(R.string.error_fetch_achievements));
            Timber.e(e, "Error fetching the list of competition info cell");
            hideProgress();
            displayEmpty();
        }
    }
}
