package com.tradehero.th.fragments.achievement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.achievement.AchievementCategoryListCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class AchievementListFragment extends DashboardFragment
{
    private static final int MENU_TEST = 19841223;

    @InjectView(android.R.id.list) protected AbsListView listView;
    @InjectView(android.R.id.empty) protected ProgressBar emptyView;

    protected AchievementListAdapter achievementListAdapter;

    @Inject AchievementCategoryListCache achievementCategoryListCache;
    @Inject CurrentUserId currentUserId;

    protected DTOCacheNew.Listener<UserBaseKey, AchievementCategoryDTOList> achievementCategoryListCacheListener;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_generic_list, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        initAdapter();

        achievementCategoryListCacheListener = createAchievementCategoryListCacheListener();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, MENU_TEST, Menu.NONE, "Test");
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case MENU_TEST:
                getDashboardNavigator().pushFragment(AchievementListTestingFragment.class, null);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initAdapter()
    {
        achievementListAdapter = new AchievementListAdapter(getActivity(), R.layout.achievement_cell_view);
        listView.setAdapter(achievementListAdapter);
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
        achievementListAdapter.clear();

        UserBaseKey userBaseKey = currentUserId.toUserBaseKey();
        achievementCategoryListCache.register(userBaseKey, achievementCategoryListCacheListener);
        achievementCategoryListCache.getOrFetchAsync(userBaseKey);
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
            achievementListAdapter.clear();
            achievementListAdapter.addAll(value);
            achievementListAdapter.notifyDataSetChanged();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_achievements));
            Timber.e("Error fetching the list of competition info cell %s", key, error);
        }
    }
}
