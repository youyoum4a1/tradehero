package com.tradehero.th.fragments.achievement;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.AchievementCategoryDTO;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.achievement.AchievementDefDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.achievement.AchievementCategoryListCacheRx;
import com.tradehero.th.persistence.achievement.UserAchievementCacheRx;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;
import timber.log.Timber;

public class AchievementListTestingFragment extends DashboardFragment
{
    @InjectView(R.id.generic_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.generic_ptr_list) protected ListView listView;
    @InjectView(android.R.id.progress) protected ProgressBar progressBar;

    @Inject AchievementCategoryListCacheRx achievementCategoryListCache;
    @Inject CurrentUserId currentUserId;

    @Inject UserAchievementCacheRx userAchievementCache;

    private List<AchievementDefDTO> list = new ArrayList<>();
    private ArrayAdapter<AchievementDefDTO> arrayAdapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_generic_list, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        swipeRefreshLayout.setEnabled(false);
        initAdapter();
        listView.setOnItemClickListener(this::onListViewItemClicked);
    }

    protected void onListViewItemClicked(AdapterView<?> adapterView, View view, int i, long l)
    {
        AchievementDefDTO achievementDefDTO = list.get(i);
        UserAchievementDTO userAchievementDTO = new UserAchievementDTO();

        userAchievementDTO.id = i;

        userAchievementDTO.achievementDef = achievementDefDTO;

        userAchievementDTO.isReset = true;
        userAchievementDTO.xpEarned = 400;
        userAchievementDTO.xpTotal = 1030;

        userAchievementCache.onNextAndBroadcast(userAchievementDTO);
    }

    private void initAdapter()
    {
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);
    }

    @Override public void onStart()
    {
        attachAndFetchAchievementCategoryListener();
        super.onStart();
    }

    protected void attachAndFetchAchievementCategoryListener()
    {
        arrayAdapter.clear();

        UserBaseKey userBaseKey = currentUserId.toUserBaseKey();
        AppObservable.bindFragment(this,
                achievementCategoryListCache.get(userBaseKey))
                .subscribe(createAchievementCategoryListCacheObserver());
    }

    @Override public void onDestroyView()
    {
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
            list.clear();
            for (AchievementCategoryDTO achievementCategoryDTO : pair.second)
            {
                for (AchievementDefDTO achievementDefDTO : achievementCategoryDTO.achievementDefs)
                {
                    list.add(achievementDefDTO);
                }
            }
            arrayAdapter.notifyDataSetChanged();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(getString(R.string.error_fetch_achievements));
            Timber.e(e, "Error fetching the list of competition info cell");
        }
    }
}
