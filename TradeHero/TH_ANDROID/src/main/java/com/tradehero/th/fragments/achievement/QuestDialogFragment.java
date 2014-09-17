package com.tradehero.th.fragments.achievement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.widget.QuestIndicatorGroupView;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.QuestBonusDTO;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
import com.tradehero.th.api.achievement.key.QuestBonusListId;
import com.tradehero.th.persistence.achievement.QuestBonusListCache;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class QuestDialogFragment extends AbstractAchievementDialogFragment
{
    private static final int NO_OF_QUEST_BEFORE_CURRENT = 2;

    @InjectView(R.id.quest_indicator_group) QuestIndicatorGroupView questIndicatorGroupView;

    private DTOCacheNew.Listener<QuestBonusListId, QuestBonusDTOList> mQuestBonusListCacheListener;

    private QuestBonusListId questBonusListId = new QuestBonusListId();

    @Inject QuestBonusListCache questBonusListCache;

    public QuestDialogFragment()
    {
        super();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.quest_dialog_fragment, container, false);
    }

    @Override protected void init()
    {
        super.init();
        mQuestBonusListCacheListener = new QuestBonusCacheListener();
        attachQuestBonusCacheListener();
    }

    @Override public void onDestroyView()
    {
        detachQuestBonusListener();
        super.onDestroyView();
    }

    private void attachQuestBonusCacheListener()
    {
        questBonusListCache.register(questBonusListId, mQuestBonusListCacheListener);
        questBonusListCache.getOrFetchAsync(questBonusListId);
    }

    private void detachQuestBonusListener()
    {
        questBonusListCache.unregister(mQuestBonusListCacheListener);
    }

    @Override public void onDestroy()
    {
        mQuestBonusListCacheListener = null;
        super.onDestroy();
    }

    @Override protected void handleBadgeSuccess()
    {
        super.handleBadgeSuccess();
        questIndicatorGroupView.delayedColorUpdate(mCurrentColor);
    }

    private class QuestBonusCacheListener implements DTOCacheNew.Listener<QuestBonusListId, QuestBonusDTOList>
    {
        @Override public void onDTOReceived(@NotNull QuestBonusListId key, @NotNull QuestBonusDTOList value)
        {
            List<QuestBonusDTO> questBonusDTOList = value.getInclusive(userAchievementDTO.contiguousCount, questIndicatorGroupView.getNumberOfIndicators());
            if(firstIsCurrentLevel(questBonusDTOList))
            {
                //Get previous
                List<QuestBonusDTO> questBonusDTO = value.getPrevious(userAchievementDTO.contiguousCount, NO_OF_QUEST_BEFORE_CURRENT);
                if(!questBonusDTO.isEmpty())
                {
                    questBonusDTOList.addAll(0, questBonusDTO);
                }
            }
            questIndicatorGroupView.setQuestBonusDef(questBonusDTOList, userAchievementDTO.contiguousCount);
            questIndicatorGroupView.revealNext();
        }

        private boolean firstIsCurrentLevel(List<QuestBonusDTO> questBonusDTOList)
        {
            return !questBonusDTOList.isEmpty() && questBonusDTOList.get(0).level == userAchievementDTO.contiguousCount;
        }

        @Override public void onErrorThrown(@NotNull QuestBonusListId key, @NotNull Throwable error)
        {

        }
    }
}
