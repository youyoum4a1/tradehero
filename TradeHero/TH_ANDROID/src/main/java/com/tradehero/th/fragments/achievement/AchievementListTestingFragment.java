package com.tradehero.th.fragments.achievement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.AchievementCategoryDTO;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.achievement.AchievementDefDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.achievement.AchievementCategoryListCache;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

// TODO move to debug?
public class AchievementListTestingFragment extends DashboardFragment
{
    @InjectView(android.R.id.list) protected AbsListView listView;
    @InjectView(android.R.id.empty) protected ProgressBar emptyView;

    @Inject AchievementCategoryListCache achievementCategoryListCache;
    @Inject CurrentUserId currentUserId;
    @Inject AbstractAchievementDialogFragment.Creator creator;

    @Inject UserAchievementCache userAchievementCache;

    protected DTOCacheNew.Listener<UserBaseKey, AchievementCategoryDTOList> achievementCategoryListCacheListener;
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
        achievementCategoryListCacheListener = createAchievementCategoryListCacheListener();

        initAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                AchievementDefDTO achievementDefDTO = list.get(i);
                UserAchievementDTO userAchievementDTO = new UserAchievementDTO();

                userAchievementDTO.id = i;

                userAchievementDTO.achievementDef = achievementDefDTO;

                userAchievementDTO.isReset = true;
                userAchievementDTO.xpEarned = 400;
                userAchievementDTO.xpTotal = 1030;

                userAchievementCache.putAndBroadcast(userAchievementDTO);
            }
        });
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

    @Override public void onStop()
    {
        detachAchievementCategoryListener();
        super.onStop();
    }

    protected void attachAndFetchAchievementCategoryListener()
    {
        arrayAdapter.clear();

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
            list.clear();
            for (AchievementCategoryDTO achievementCategoryDTO : value)
            {
                for (AchievementDefDTO achievementDefDTO : achievementCategoryDTO.achievementDefs)
                {
                    list.add(achievementDefDTO);
                }
            }
            arrayAdapter.notifyDataSetChanged();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_achievements));
            Timber.e("Error fetching the list of competition info cell %s", key, error);
        }
    }
}
