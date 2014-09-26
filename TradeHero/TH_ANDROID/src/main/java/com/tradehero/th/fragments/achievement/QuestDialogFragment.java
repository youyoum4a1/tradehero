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
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.key.QuestBonusListId;
import com.tradehero.th.persistence.achievement.QuestBonusListCache;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuestDialogFragment extends AbstractAchievementDialogFragment
{
    private static final int NO_OF_QUEST_BEFORE_CURRENT = 2;

    @InjectView(R.id.quest_indicator_group) QuestIndicatorGroupView questIndicatorGroupView;

    @Nullable private DTOCacheNew.Listener<QuestBonusListId, QuestBonusDTOList> mQuestBonusListCacheListener;

    @NotNull private QuestBonusListId questBonusListId = new QuestBonusListId();

    @Inject QuestBonusListCache questBonusListCache;

    //<editor-fold desc="Constructors">
    public QuestDialogFragment()
    {
        super();
    }
    //</editor-fold>

    @Override public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.quest_dialog_fragment, container, false);
    }

    @Override protected void init()
    {
        super.init();
        mQuestBonusListCacheListener = new QuestBonusCacheListener();
    }

    @Override protected void initView()
    {
        super.initView();
        attachQuestBonusCacheListener();
    }

    private void attachQuestBonusCacheListener()
    {
        questBonusListCache.register(questBonusListId, mQuestBonusListCacheListener);
        questBonusListCache.getOrFetchAsync(questBonusListId);
    }

    @Override public void onDestroyView()
    {
        detachQuestBonusListener();
        super.onDestroyView();
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
            UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
            if (userAchievementDTOCopy != null)
            {
                List<QuestBonusDTO> questBonusDTOList = value.getInclusive(
                        userAchievementDTOCopy.contiguousCount,
                        questIndicatorGroupView.getNumberOfIndicators());
                Boolean first = firstIsCurrentLevel(questBonusDTOList);
                if(first != null && first)
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
        }

        @Nullable private Boolean firstIsCurrentLevel(List<QuestBonusDTO> questBonusDTOList)
        {
            UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
            if (userAchievementDTOCopy == null)
            {
                return null;
            }
            return !questBonusDTOList.isEmpty() && questBonusDTOList.get(0).level == userAchievementDTOCopy.contiguousCount;
        }

        @Override public void onErrorThrown(@NotNull QuestBonusListId key, @NotNull Throwable error)
        {
        }
    }
}
