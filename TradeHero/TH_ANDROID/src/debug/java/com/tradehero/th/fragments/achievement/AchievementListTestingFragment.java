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
import butterknife.OnItemClick;
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
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import rx.functions.Action1;

public class AchievementListTestingFragment extends DashboardFragment
{
    @InjectView(R.id.generic_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.generic_ptr_list) protected ListView listView;
    @InjectView(android.R.id.progress) protected ProgressBar progressBar;

    @Inject AchievementCategoryListCacheRx achievementCategoryListCache;
    @Inject CurrentUserId currentUserId;
    @Inject UserAchievementCacheRx userAchievementCache;

    private ArrayAdapter<AchievementDefDTO> arrayAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_generic_list, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        swipeRefreshLayout.setEnabled(false);
        listView.setAdapter(arrayAdapter);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchAchievementCategories();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        arrayAdapter = null;
        super.onDestroy();
    }

    protected void fetchAchievementCategories()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                achievementCategoryListCache.get(currentUserId.toUserBaseKey()))
                .subscribe(
                        new Action1<Pair<UserBaseKey, AchievementCategoryDTOList>>()
                        {
                            @Override public void call(Pair<UserBaseKey, AchievementCategoryDTOList> pair)
                            {
                                onAchievementReceived(pair);
                            }
                        },
                        new ToastAndLogOnErrorAction(
                                getString(R.string.error_fetch_achievements),
                                "Error fetching the list of competition info cell")
                ));
    }

    public void onAchievementReceived(Pair<UserBaseKey, AchievementCategoryDTOList> pair)
    {
        arrayAdapter.setNotifyOnChange(false);
        arrayAdapter.clear();
        for (AchievementCategoryDTO achievementCategoryDTO : pair.second)
        {
            for (AchievementDefDTO achievementDefDTO : achievementCategoryDTO.achievementDefs)
            {
                arrayAdapter.add(achievementDefDTO);
            }
        }
        arrayAdapter.setNotifyOnChange(true);
        arrayAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("unused")
    @OnItemClick(R.id.generic_ptr_list)
    protected void onListViewItemClicked(AdapterView<?> adapterView, View view, int i, long l)
    {
        AchievementDefDTO achievementDefDTO = (AchievementDefDTO) adapterView.getItemAtPosition(i);
        UserAchievementDTO userAchievementDTO = new UserAchievementDTO();

        userAchievementDTO.id = i;

        userAchievementDTO.achievementDef = achievementDefDTO;

        userAchievementDTO.isReset = true;
        userAchievementDTO.xpEarned = 400;
        userAchievementDTO.xpTotal = 1030;

        userAchievementCache.onNextAndBroadcast(userAchievementDTO);
    }
}
