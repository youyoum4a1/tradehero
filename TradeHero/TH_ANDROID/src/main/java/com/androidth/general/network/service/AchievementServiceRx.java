package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.achievement.AchievementCategoryDTOList;
import com.androidth.general.api.achievement.QuestBonusDTOList;
import com.androidth.general.api.achievement.UserAchievementDTO;
import com.androidth.general.api.level.LevelDefDTOList;
import com.androidth.general.api.social.SocialShareReqFormDTO;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

interface AchievementServiceRx
{
    //<editor-fold desc="Get Trader Level Definition">
    @GET("api/achievements/traderleveldefs")
    Observable<LevelDefDTOList> getLevelDefs();
    //</editor-fold>

    //<editor-fold desc="Get User Achievement Details">
    @GET("api/achievements/achievement/{userAchievementId}")
    Observable<UserAchievementDTO> getUserAchievementDetails(
            @Path("userAchievementId") int userAchievementId);
    //</editor-fold>

    //<editor-fold desc="Get User Achievement List">
    @GET("api/achievements/categories/{userId}")
    Observable<AchievementCategoryDTOList> getAchievementCategories(@Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get Achievement Category">
    @GET("api/achievements/categories/{userId}")
    Observable<AchievementCategoryDTOList> getAchievementCategory(@Query("id") int categoryId, @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get Quest Bonuses">
    @GET("api/achievements/questbonus")
    Observable<QuestBonusDTOList> getQuestBonuses();
    //</editor-fold>

    //<editor-fold desc="Share Achievement">
    @POST("api/achievements/share/{userAchievementId}")
    Observable<BaseResponseDTO> shareUserAchievement(
            @Path("userAchievementId") int userAchievementId,
            @Body SocialShareReqFormDTO achievementShareFormDTO);
    //</editor-fold>
}
