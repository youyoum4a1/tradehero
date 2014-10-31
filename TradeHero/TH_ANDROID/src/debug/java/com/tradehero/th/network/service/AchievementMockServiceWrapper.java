package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.achievement.key.MockQuestBonusId;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public class AchievementMockServiceWrapper extends AchievementServiceWrapper
{
    @NotNull private final AchievementMockServiceRx achievementMockServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public AchievementMockServiceWrapper(
            @NotNull AchievementServiceRx achievementServiceRx,
            @NotNull AchievementMockServiceRx achievementMockServiceRx)
    {
        super(achievementServiceRx);
        this.achievementMockServiceRx = achievementMockServiceRx;
    }
    //</editor-fold>

    @NotNull public Observable<BaseResponseDTO> getMockBonusDTORx(
            @NotNull MockQuestBonusId mockQuestBonusId)
    {
        return achievementMockServiceRx.getMockQuestBonus(
                mockQuestBonusId.key,
                mockQuestBonusId.xpEarned,
                mockQuestBonusId.xpTotal);
    }
}
