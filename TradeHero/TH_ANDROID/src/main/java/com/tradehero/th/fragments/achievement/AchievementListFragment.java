package com.tradehero.th.fragments.achievement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.achievement.AchievementCategoryListCache;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class AchievementListFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_USER_ID = AchievementListFragment.class.getName() + ".userId";

    @InjectView(android.R.id.list) protected AbsListView listView;
    @InjectView(android.R.id.empty) protected ProgressBar emptyView;

    protected AchievementListAdapter achievementListAdapter;

    @Inject AchievementCategoryListCache achievementCategoryListCache;
    @Inject DashboardNavigator navigator;
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

        init();

        initAdapter();

        achievementCategoryListCacheListener = createAchievementCategoryListCacheListener();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.achievements);
        if (!Constants.RELEASE)
        {
            inflater.inflate(R.menu.achievement_testing, menu);
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_test_achievement:
                navigator.pushFragment(AchievementListTestingFragment.class, null);
                break;
            case R.id.menu_test_achievement_daily:
                navigator.pushFragment(QuestListTestingFragment.class, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void init()
    {
        this.shownUserId = getUserId();
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

        achievementCategoryListCache.register(shownUserId, achievementCategoryListCacheListener);
        achievementCategoryListCache.getOrFetchAsync(shownUserId);
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
