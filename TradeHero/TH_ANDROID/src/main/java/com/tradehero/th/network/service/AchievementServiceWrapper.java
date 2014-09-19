package com.tradehero.th.network.service;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.achievement.AchievementCategoryDTO;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.key.AchievementCategoryId;
import com.tradehero.th.api.achievement.key.QuestBonusListId;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.share.achievement.AchievementShareFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.key.MockQuestBonusId;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

public class AchievementServiceWrapper
{
    @NotNull private final AchievementServiceAsync achievementServiceAsync;
    @NotNull private final AchievementService achievementService;

    @Inject public AchievementServiceWrapper(
            @NotNull AchievementService achievementService,
            @NotNull AchievementServiceAsync achievementServiceAsync
    )
    {
        this.achievementService = achievementService;
        this.achievementServiceAsync = achievementServiceAsync;
    }

    public LevelDefDTOList getLevelDefs()
    {
        return achievementService.getLevelDefs();
    }

    @NotNull public MiddleCallback<LevelDefDTOList> getLevelDefs(
            @Nullable Callback<LevelDefDTOList> callback)
    {
        MiddleCallback<LevelDefDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        achievementServiceAsync.getLevelDefs(middleCallback);
        return middleCallback;
    }

    public UserAchievementDTO getUserAchievementDetails(UserAchievementId userAchievementId)
    {
        return achievementService.getUserAchievementDetails(userAchievementId.key);
    }

    @NotNull public MiddleCallback<UserAchievementDTO> getUserAchievementDetails(
            @NotNull UserAchievementId userAchievementId,
            @Nullable Callback<UserAchievementDTO> callback)
    {
        MiddleCallback<UserAchievementDTO> middleCallback = new BaseMiddleCallback<>(callback);
        achievementServiceAsync.getUserAchievementDetails(userAchievementId.key, middleCallback);
        return middleCallback;
    }

    public AchievementCategoryDTOList getAchievementCategories(@NotNull UserBaseKey key)
    {
        return achievementService.getAchievementCategories(key.getUserId());
    }

    public MiddleCallback<AchievementCategoryDTOList> getAchievementCategories(@NotNull UserBaseKey key, @Nullable Callback<AchievementCategoryDTOList> callback)
    {
        MiddleCallback<AchievementCategoryDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        achievementServiceAsync.getAchievementCategories(key.getUserId(), middleCallback);
        return middleCallback;
    }

    @Nullable public AchievementCategoryDTO getAchievementCategory(@NotNull AchievementCategoryId achievementCategoryId)
    {
        AchievementCategoryDTOList achievementCategoryDTOs = achievementService.getAchievementCategory(achievementCategoryId.categoryId, achievementCategoryId.userId);
        if(achievementCategoryDTOs != null && !achievementCategoryDTOs.isEmpty())
        {
            return achievementCategoryDTOs.get(0);
        }
        return null;
    }

    public QuestBonusDTOList getQuestBonuses(QuestBonusListId questBonusListId)
    {
        return achievementService.getQuestBonuses();
    }

    public DTO getMockBonusDTO(MockQuestBonusId mockQuestBonusId)
    {
        return achievementService.getMockQuestBonus(mockQuestBonusId.key, mockQuestBonusId.xpEarned, mockQuestBonusId.xpTotal);
    }

    public MiddleCallback<ExtendedDTO> getMockBonusDTO(MockQuestBonusId mockQuestBonusId, Callback<ExtendedDTO> callback)
    {
        MiddleCallback<ExtendedDTO> middleCallback = new BaseMiddleCallback<>(callback);
        achievementServiceAsync.getMockQuestBonus(mockQuestBonusId.key, mockQuestBonusId.xpEarned, mockQuestBonusId.xpTotal, middleCallback);
        return middleCallback;
    }

    public MiddleCallback<ExtendedDTO> shareAchievement(AchievementShareFormDTO achievementShareFormDTO, Callback<ExtendedDTO> callback)
    {
        MiddleCallback<ExtendedDTO> middleCallback = new BaseMiddleCallback<>(callback);
        achievementServiceAsync.shareUserAchievement(achievementShareFormDTO.userAchievementId.key, achievementShareFormDTO.achievementShareRequestDTO, middleCallback);
        return middleCallback;
    }
}
