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
    @InjectView(R.id.quest_indicator_group) QuestIndicatorGroupView questIndicatorGroupView;

    private DTOCacheNew.Listener<QuestBonusListId, QuestBonusDTOList> mQuestBonusListCacheListener;

    private QuestBonusListId questBonusListId = new QuestBonusListId();

    @Inject QuestBonusListCache questBonusListCache;

    protected QuestDialogFragment()
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
    }

    @Override protected void initView()
    {
        super.initView();
    }

    @Override public void onStart()
    {
        super.onStart();
        attachQuestBonusCacheListener();
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

    @Override public void onStop()
    {
        detachQuestBonusListener();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        mQuestBonusListCacheListener = null;
        super.onDestroy();
    }

    private class QuestBonusCacheListener implements DTOCacheNew.Listener<QuestBonusListId, QuestBonusDTOList>
    {
        @Override public void onDTOReceived(@NotNull QuestBonusListId key, @NotNull QuestBonusDTOList value)
        {
            List<QuestBonusDTO> questBonusDTOList = value.getInclusive(userAchievementDTO.contiguousCount, questIndicatorGroupView.getNumberOfIndicators());
            if(!questBonusDTOList.isEmpty() && questBonusDTOList.get(0).level == userAchievementDTO.contiguousCount)
            {
                //Get previous
                QuestBonusDTO questBonusDTO = value.getPrevious(userAchievementDTO.contiguousCount);
                if(questBonusDTO != null)
                {
                    questBonusDTOList.add(0, questBonusDTO);
                }
            }
            questIndicatorGroupView.setQuestBonusDef(questBonusDTOList, userAchievementDTO.contiguousCount);
            questIndicatorGroupView.revealNext();
        }

        @Override public void onErrorThrown(@NotNull QuestBonusListId key, @NotNull Throwable error)
        {

        }
    }
}
