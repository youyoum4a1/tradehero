package com.tradehero.th.fragments.achievement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.achievement.AchievementCategoryListCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class AchievementListFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_USER_ID = AchievementListFragment.class.getName() + ".userId";

    @InjectView(R.id.generic_ptr_list) protected PullToRefreshListView listView;
    @InjectView(android.R.id.progress) protected ProgressBar progressBar;
    @InjectView(android.R.id.empty) protected View emptyView;

    protected AchievementListAdapter achievementListAdapter;

    @Inject AchievementCategoryListCache achievementCategoryListCache;
    UserBaseKey shownUserId;

    protected DTOCacheNew.Listener<UserBaseKey, AchievementCategoryDTOList> achievementCategoryListCacheListener;

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
        achievementCategoryListCacheListener = createAchievementCategoryListCacheListener();
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

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>()
        {
            @Override public void onRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase)
            {
                listView.setRefreshing();
                attachAndFetchAchievementCategoryListener(true);
            }
        });
    }

    @Override public void onStart()
    {
        attachAndFetchAchievementCategoryListener();
        super.onStart();
    }

    @Override public void onStop()
    {
        detachAchievementCategoryListener();
        super.onStop();
    }

    protected void attachAndFetchAchievementCategoryListener()
    {
        attachAndFetchAchievementCategoryListener(false);
    }

    protected void attachAndFetchAchievementCategoryListener(boolean forceUpdate)
    {
        hideEmpty();
        detachAchievementCategoryListener();
        achievementCategoryListCache.register(shownUserId, achievementCategoryListCacheListener);
        achievementCategoryListCache.getOrFetchAsync(shownUserId, forceUpdate);
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

    protected void detachAchievementCategoryListener()
    {
        achievementCategoryListCache.unregister(achievementCategoryListCacheListener);
    }

    @Override public void onDestroy()
    {
        achievementCategoryListCacheListener = null;
        super.onDestroy();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected DTOCacheNew.Listener<UserBaseKey, AchievementCategoryDTOList> createAchievementCategoryListCacheListener()
    {
        return new AchievementCategoryListCacheListener();
    }

    protected class AchievementCategoryListCacheListener implements DTOCacheNew.Listener<UserBaseKey, AchievementCategoryDTOList>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull AchievementCategoryDTOList value)
        {
            listView.onRefreshComplete();
            achievementListAdapter.clear();
            achievementListAdapter.addAll(value);
            achievementListAdapter.notifyDataSetChanged();
            hideProgress();
            if(achievementListAdapter.isEmpty())
            {
                displayEmpty();
            }
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            listView.onRefreshComplete();
            THToast.show(getString(R.string.error_fetch_achievements));
            Timber.e("Error fetching the list of competition info cell %s", key, error);
            hideProgress();
            displayEmpty();
        }
    }
}
