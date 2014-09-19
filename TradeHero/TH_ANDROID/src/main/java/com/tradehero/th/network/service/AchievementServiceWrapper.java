package com.tradehero.th.network.service;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.BaseResponseDTO;
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

    //<editor-fold desc="Constructors">
    @Inject public AchievementServiceWrapper(
            @NotNull AchievementService achievementService,
            @NotNull AchievementServiceAsync achievementServiceAsync)
    {
        this.achievementService = achievementService;
        this.achievementServiceAsync = achievementServiceAsync;
    }
    //</editor-fold>

    //<editor-fold desc="Get Level Defs">
    @NotNull public LevelDefDTOList getLevelDefs()
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
    //</editor-fold>

    //<editor-fold desc="Get User Achievement Details">
    @NotNull public UserAchievementDTO getUserAchievementDetails(@NotNull UserAchievementId userAchievementId)
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
    //</editor-fold>

    //<editor-fold desc="Get Achievement Categories">
    @NotNull public AchievementCategoryDTOList getAchievementCategories(
            @NotNull UserBaseKey key)
    {
        return achievementService.getAchievementCategories(key.getUserId());
    }

    @NotNull public MiddleCallback<AchievementCategoryDTOList> getAchievementCategories(
            @NotNull UserBaseKey key,
            @Nullable Callback<AchievementCategoryDTOList> callback)
    {
        MiddleCallback<AchievementCategoryDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        achievementServiceAsync.getAchievementCategories(key.getUserId(), middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Achievement Category">
    @Nullable public AchievementCategoryDTO getAchievementCategory(
            @NotNull AchievementCategoryId achievementCategoryId)
    {
        AchievementCategoryDTOList achievementCategoryDTOs = achievementService.getAchievementCategory(
                achievementCategoryId.categoryId,
                achievementCategoryId.userId);
        if(achievementCategoryDTOs != null && !achievementCategoryDTOs.isEmpty())
        {
            return achievementCategoryDTOs.get(0);
        }
        return null;
    }

    // TODO add Async
    //</editor-fold>

    //<editor-fold desc="Get Quest Bonuses">
    @NotNull public QuestBonusDTOList getQuestBonuses(@SuppressWarnings("UnusedParameters") @NotNull QuestBonusListId questBonusListId)
    {
        return achievementService.getQuestBonuses();
    }

    @NotNull public MiddleCallback<QuestBonusDTOList> getQuestBonuses(
            @SuppressWarnings("UnusedParameters") @NotNull QuestBonusListId questBonusListId,
            @Nullable Callback<QuestBonusDTOList> callback)
    {
        MiddleCallback<QuestBonusDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        achievementServiceAsync.getQuestBonuses(middleCallback);
        return middleCallback;
    }
    //</editor-fold>

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

    //<editor-fold desc="Share Achievement">
    @NotNull public BaseResponseDTO shareAchievement(
            @NotNull AchievementShareFormDTO achievementShareFormDTO)
    {
        return achievementService.shareUserAchievement(
                achievementShareFormDTO.userAchievementId.key,
                achievementShareFormDTO.achievementShareReqFormDTO);
    }

    @NotNull public MiddleCallback<BaseResponseDTO> shareAchievement(
            @NotNull AchievementShareFormDTO achievementShareFormDTO,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        MiddleCallback<BaseResponseDTO> middleCallback = new BaseMiddleCallback<>(callback);
        achievementServiceAsync.shareUserAchievement(achievementShareFormDTO.userAchievementId.key, achievementShareFormDTO.achievementShareReqFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
