package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.achievement.AchievementCategoryDTO;
import com.androidth.general.api.achievement.AchievementCategoryDTOList;
import com.androidth.general.api.achievement.QuestBonusDTOList;
import com.androidth.general.api.achievement.UserAchievementDTO;
import com.androidth.general.api.achievement.key.AchievementCategoryId;
import com.androidth.general.api.achievement.key.QuestBonusListId;
import com.androidth.general.api.achievement.key.UserAchievementId;
import com.androidth.general.api.level.LevelDefDTOList;
import com.androidth.general.api.share.achievement.AchievementShareFormDTO;
import com.androidth.general.api.users.UserBaseKey;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AchievementServiceWrapper
{
    @NonNull private final AchievementServiceRx achievementServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public AchievementServiceWrapper(@NonNull AchievementServiceRx achievementServiceRx)
    {
        this.achievementServiceRx = achievementServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get Level Defs">
    @NonNull public Observable<LevelDefDTOList> getLevelDefsRx()
    {
        return achievementServiceRx.getLevelDefs().subscribeOn(Schedulers.io());//to avoid NetworkOnMainThreadException
    }
    //</editor-fold>

    //<editor-fold desc="Get User Achievement Details">
    @NonNull public Observable<UserAchievementDTO> getUserAchievementDetailsRx(@NonNull UserAchievementId userAchievementId)
    {
        return achievementServiceRx.getUserAchievementDetails(userAchievementId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Achievement Categories">
    @NonNull public Observable<AchievementCategoryDTOList> getAchievementCategoriesRx(
            @NonNull UserBaseKey key)
    {
        return achievementServiceRx.getAchievementCategories(key.getUserId());
    }
    //</editor-fold>

    //<editor-fold desc="Get Achievement Category">
    @NonNull public Observable<AchievementCategoryDTO> getAchievementCategoryRx(
            @NonNull AchievementCategoryId achievementCategoryId)
    {
        return achievementServiceRx.getAchievementCategory(
                achievementCategoryId.categoryId,
                achievementCategoryId.userId)
                .flatMap(new Func1<AchievementCategoryDTOList, Observable<? extends AchievementCategoryDTO>>()
                {
                    @Override public Observable<? extends AchievementCategoryDTO> call(AchievementCategoryDTOList achievementCategoryDTOs)
                    {
                        if (achievementCategoryDTOs != null && !achievementCategoryDTOs.isEmpty())
                        {
                            return Observable.just(achievementCategoryDTOs.get(0));
                        }
                        else
                        {
                            return Observable.empty();
                        }
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Get Quest Bonuses">
    @NonNull public Observable<QuestBonusDTOList> getQuestBonusesRx(@SuppressWarnings("UnusedParameters") @NonNull QuestBonusListId questBonusListId)
    {
        return achievementServiceRx.getQuestBonuses();
    }
    //</editor-fold>

    //<editor-fold desc="Share Achievement">
    @NonNull public Observable<BaseResponseDTO> shareAchievementRx(
            @NonNull AchievementShareFormDTO achievementShareFormDTO)
    {
        return achievementServiceRx.shareUserAchievement(
                achievementShareFormDTO.userAchievementId.key,
                achievementShareFormDTO.socialShareReqFormDTO);
    }
    //</editor-fold>
}
