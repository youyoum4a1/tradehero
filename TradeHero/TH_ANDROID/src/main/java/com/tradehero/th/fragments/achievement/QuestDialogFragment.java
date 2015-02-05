package com.tradehero.th.fragments.achievement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.QuestBonusDTO;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.key.QuestBonusListId;
import com.tradehero.th.persistence.achievement.QuestBonusListCacheRx;
import com.tradehero.th.widget.QuestIndicatorGroupView;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;

public class QuestDialogFragment extends AbstractAchievementDialogFragment
{
    private static final int NO_OF_QUEST_BEFORE_CURRENT = 2;

    @InjectView(R.id.quest_indicator_group) QuestIndicatorGroupView questIndicatorGroupView;

    @NonNull private QuestBonusListId questBonusListId = new QuestBonusListId();

    @Inject QuestBonusListCacheRx questBonusListCache;

    //<editor-fold desc="Constructors">
    public QuestDialogFragment()
    {
        super();
    }
    //</editor-fold>

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.quest_dialog_fragment, container, false);
    }

    @Override protected void initView()
    {
        super.initView();
        fetchQuestBonusList();
    }

    private void fetchQuestBonusList()
    {
        AppObservable.bindFragment(
                this,
                questBonusListCache.getOne(questBonusListId))
                .subscribe(new QuestBonusCacheObserver());
    }

    @Override protected void handleBadgeSuccess()
    {
        super.handleBadgeSuccess();
        questIndicatorGroupView.delayedColorUpdate(mCurrentColor);
    }

    private class QuestBonusCacheObserver implements Observer<Pair<QuestBonusListId, QuestBonusDTOList>>
    {
        @Override public void onNext(Pair<QuestBonusListId, QuestBonusDTOList> pair)
        {
            UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
            if (userAchievementDTOCopy != null)
            {
                List<QuestBonusDTO> questBonusDTOList = pair.second.getInclusive(
                        userAchievementDTOCopy.contiguousCount,
                        questIndicatorGroupView.getNumberOfIndicators());
                Boolean first = firstIsCurrentLevel(questBonusDTOList);
                if (first != null && first)
                {
                    //Get previous
                    List<QuestBonusDTO> questBonusDTO = pair.second.getPrevious(userAchievementDTO.contiguousCount, NO_OF_QUEST_BEFORE_CURRENT);
                    if (!questBonusDTO.isEmpty())
                    {
                        questBonusDTOList.addAll(0, questBonusDTO);
                    }
                }
                questIndicatorGroupView.setQuestBonusDef(questBonusDTOList, userAchievementDTO.contiguousCount);
                questIndicatorGroupView.revealNext();
            }
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
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
    }
}
