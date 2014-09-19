package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.achievement.key.MockQuestBonusId;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

public class AchievementMockServiceWrapper extends AchievementServiceWrapper
{
    @NotNull private final AchievementMockServiceAsync achievementMockServiceAsync;
    @NotNull private final AchievementMockService achievementMockService;

    //<editor-fold desc="Constructors">
    @Inject public AchievementMockServiceWrapper(
            @NotNull AchievementService achievementService,
            @NotNull AchievementServiceAsync achievementServiceAsync,
            @NotNull AchievementMockService achievementMockService,
            @NotNull AchievementMockServiceAsync achievementMockServiceAsync)
    {
        super(achievementService, achievementServiceAsync);
        this.achievementMockService = achievementMockService;
        this.achievementMockServiceAsync = achievementMockServiceAsync;
    }
    //</editor-fold>

    @NotNull public BaseResponseDTO getMockBonusDTO(
            @NotNull MockQuestBonusId mockQuestBonusId)
    {
        return achievementMockService.getMockQuestBonus(
                mockQuestBonusId.key,
                mockQuestBonusId.xpEarned,
                mockQuestBonusId.xpTotal);
    }

    @NotNull public MiddleCallback<BaseResponseDTO> getMockBonusDTO(
            @NotNull MockQuestBonusId mockQuestBonusId,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        MiddleCallback<BaseResponseDTO> middleCallback = new BaseMiddleCallback<>(callback);
        achievementMockServiceAsync.getMockQuestBonus(
                mockQuestBonusId.key,
                mockQuestBonusId.xpEarned,
                mockQuestBonusId.xpTotal,
                middleCallback);
        return middleCallback;
    }
}
