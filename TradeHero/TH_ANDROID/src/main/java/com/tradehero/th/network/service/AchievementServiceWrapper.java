package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
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
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public class AchievementServiceWrapper
{
    @NotNull private final AchievementServiceRx achievementServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public AchievementServiceWrapper(@NotNull AchievementServiceRx achievementServiceRx)
    {
        this.achievementServiceRx = achievementServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get Level Defs">
    @NotNull public Observable<LevelDefDTOList> getLevelDefsRx()
    {
        return achievementServiceRx.getLevelDefs();
    }
    //</editor-fold>

    //<editor-fold desc="Get User Achievement Details">
    @NotNull public Observable<UserAchievementDTO> getUserAchievementDetailsRx(@NotNull UserAchievementId userAchievementId)
    {
        return achievementServiceRx.getUserAchievementDetails(userAchievementId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Achievement Categories">
    @NotNull public Observable<AchievementCategoryDTOList> getAchievementCategoriesRx(
            @NotNull UserBaseKey key)
    {
        return achievementServiceRx.getAchievementCategories(key.getUserId());
    }
    //</editor-fold>

    //<editor-fold desc="Get Achievement Category">
    @NotNull public Observable<AchievementCategoryDTO> getAchievementCategoryRx(
            @NotNull AchievementCategoryId achievementCategoryId)
    {
        return achievementServiceRx.getAchievementCategory(
                achievementCategoryId.categoryId,
                achievementCategoryId.userId)
                .flatMap(achievementCategoryDTOs -> {
                    if (achievementCategoryDTOs != null && !achievementCategoryDTOs.isEmpty())
                    {
                        return Observable.just(achievementCategoryDTOs.get(0));
                    }
                    else
                    {
                        return Observable.empty();
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Get Quest Bonuses">
    @NotNull public Observable<QuestBonusDTOList> getQuestBonusesRx(@SuppressWarnings("UnusedParameters") @NotNull QuestBonusListId questBonusListId)
    {
        return achievementServiceRx.getQuestBonuses();
    }
    //</editor-fold>

    //<editor-fold desc="Share Achievement">
    @NotNull public Observable<BaseResponseDTO> shareAchievementRx(
            @NotNull AchievementShareFormDTO achievementShareFormDTO)
    {
        return achievementServiceRx.shareUserAchievement(
                achievementShareFormDTO.userAchievementId.key,
                achievementShareFormDTO.achievementShareReqFormDTO);
    }
    //</editor-fold>
}
