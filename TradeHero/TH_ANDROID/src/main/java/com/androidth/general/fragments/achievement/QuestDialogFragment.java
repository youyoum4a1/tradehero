package com.androidth.general.fragments.achievement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import com.androidth.general.R;
import com.androidth.general.api.achievement.QuestBonusDTO;
import com.androidth.general.api.achievement.QuestBonusDTOList;
import com.androidth.general.api.achievement.UserAchievementDTO;
import com.androidth.general.api.achievement.key.QuestBonusListId;
import com.androidth.general.persistence.achievement.QuestBonusListCacheRx;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.widget.QuestIndicatorGroupView;
import java.util.List;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class QuestDialogFragment extends AbstractAchievementDialogFragment
{
    private static final int NO_OF_QUEST_BEFORE_CURRENT = 2;

    @BindView(R.id.quest_indicator_group) QuestIndicatorGroupView questIndicatorGroupView;

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

    @Override public void onStart()
    {
        super.onStart();
        fetchQuestBonusList();
    }

    private void fetchQuestBonusList()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                questBonusListCache.getOne(questBonusListId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<QuestBonusListId, QuestBonusDTOList>>()
                        {
                            @Override public void call(Pair<QuestBonusListId, QuestBonusDTOList> pair)
                            {
                                QuestDialogFragment.this.onReceivedQuestBonusList(pair);
                            }
                        },
                        new EmptyAction1<Throwable>()));
    }

    @Override protected void handleBadgeSuccess()
    {
        super.handleBadgeSuccess();
        questIndicatorGroupView.delayedColorUpdate(mCurrentColor);
    }

    protected void onReceivedQuestBonusList(Pair<QuestBonusListId, QuestBonusDTOList> pair)
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

    @Nullable private Boolean firstIsCurrentLevel(@NonNull List<QuestBonusDTO> questBonusDTOList)
    {
        UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
        if (userAchievementDTOCopy == null)
        {
            return null;
        }
        return !questBonusDTOList.isEmpty() && questBonusDTOList.get(0).level == userAchievementDTOCopy.contiguousCount;
    }
}
