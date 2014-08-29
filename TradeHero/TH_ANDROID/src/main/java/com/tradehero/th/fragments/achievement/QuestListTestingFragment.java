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
import com.tradehero.th.api.achievement.AchievementDefDTO;
import com.tradehero.th.api.achievement.QuestBonusDTO;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.key.QuestBonusListId;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.achievement.QuestBonusListCache;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class QuestListTestingFragment extends DashboardFragment
{
    @InjectView(android.R.id.list) protected AbsListView listView;
    @InjectView(android.R.id.empty) protected ProgressBar emptyView;

    @Inject QuestBonusListCache questBonusListCache;
    @Inject AbstractAchievementDialogFragment.Creator creator;

    @Inject UserAchievementCache userAchievementCache;

    protected DTOCacheNew.Listener<QuestBonusListId, QuestBonusDTOList> questBonusListIdQuestBonusDTOListListener;
    private List<QuestBonusDTO> list = new ArrayList<>();
    private ArrayAdapter<QuestBonusDTO> arrayAdapter;

    private QuestBonusListId questBonusListId = new QuestBonusListId();

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_generic_list, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        questBonusListIdQuestBonusDTOListListener = createAchievementCategoryListCacheListener();

        initAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                QuestBonusDTO questBonusDTO = list.get(i);
                AchievementDefDTO achievementDefDTO = new AchievementDefDTO();

                achievementDefDTO.header = "Daily Login Bonus!";
                achievementDefDTO.thName = "Day " + questBonusDTO.level;
                achievementDefDTO.text = "You have earned " + questBonusDTO.bonus;
                achievementDefDTO.subText = "Come back tomorrow to earn another " + (i+1 >= list.size() ? "surprise " : list.get(i + 1).bonus);
                achievementDefDTO.virtualDollars = questBonusDTO.bonus;
                achievementDefDTO.visual =
                        "http://laliberteatoutprix.fr/wp-content/uploads/2013/11/Logo-MoneyManager.png";
                achievementDefDTO.isQuest = true;

                UserAchievementDTO userAchievementDTO = new UserAchievementDTO();

                userAchievementDTO.id = i;
                userAchievementDTO.contiguousCount = questBonusDTO.level;
                userAchievementDTO.achievementDef = achievementDefDTO;

                userAchievementDTO.isReset = false;
                userAchievementDTO.xpEarned = 400;
                userAchievementDTO.xpTotal = 1030;

                userAchievementCache.put(new UserAchievementId(i), userAchievementDTO);

                AbstractAchievementDialogFragment abstractAchievementDialogFragment = creator.newInstance(new UserAchievementId(i));
                if (abstractAchievementDialogFragment != null)
                {
                    abstractAchievementDialogFragment.show(getFragmentManager(), "testing");
                }
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
        questBonusListCache.register(questBonusListId, questBonusListIdQuestBonusDTOListListener);
        questBonusListCache.getOrFetchAsync(questBonusListId);
    }

    protected void detachAchievementCategoryListener()
    {
        questBonusListCache.unregister(questBonusListIdQuestBonusDTOListListener);
    }

    @Override public void onDestroy()
    {
        questBonusListIdQuestBonusDTOListListener = null;
        super.onDestroy();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected DTOCacheNew.Listener<QuestBonusListId, QuestBonusDTOList> createAchievementCategoryListCacheListener()
    {
        return new AchievementCategoryListCacheListener();
    }

    protected class AchievementCategoryListCacheListener implements DTOCacheNew.Listener<QuestBonusListId, QuestBonusDTOList>
    {

        @Override public void onDTOReceived(@NotNull QuestBonusListId key, @NotNull QuestBonusDTOList value)
        {
            list.clear();
            list.addAll(value);
            arrayAdapter.notifyDataSetChanged();
        }

        @Override public void onErrorThrown(@NotNull QuestBonusListId key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_achievements));
            Timber.e("Error fetching the list of competition info cell %s", key, error);
        }
    }
}
