package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.achievement.key.MockQuestBonusId;
import javax.inject.Inject;
import rx.Observable;

public class AchievementMockServiceWrapper extends AchievementServiceWrapper
{
    @NonNull private final AchievementMockServiceRx achievementMockServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public AchievementMockServiceWrapper(
            @NonNull AchievementServiceRx achievementServiceRx,
            @NonNull AchievementMockServiceRx achievementMockServiceRx)
    {
        super(achievementServiceRx);
        this.achievementMockServiceRx = achievementMockServiceRx;
    }
    //</editor-fold>

    @NonNull public Observable<BaseResponseDTO> getMockBonusDTORx(
            @NonNull MockQuestBonusId mockQuestBonusId)
    {
        return achievementMockServiceRx.getMockQuestBonus(
                mockQuestBonusId.key,
                mockQuestBonusId.xpEarned,
                mockQuestBonusId.xpTotal);
    }
}
