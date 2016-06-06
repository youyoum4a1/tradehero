package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.achievement.AchievementCategoryDTOList;
import com.androidth.general.api.achievement.QuestBonusDTOList;
import com.androidth.general.api.achievement.UserAchievementDTO;
import com.androidth.general.api.level.LevelDefDTOList;
import com.androidth.general.api.social.SocialShareReqFormDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

interface AchievementServiceRx
{
    //<editor-fold desc="Get Trader Level Definition">
    @GET("/achievements/traderleveldefs")
    Observable<LevelDefDTOList> getLevelDefs();
    //</editor-fold>

    //<editor-fold desc="Get User Achievement Details">
    @GET("/achievements/achievement/{userAchievementId}")
    Observable<UserAchievementDTO> getUserAchievementDetails(
            @Path("userAchievementId") int userAchievementId);
    //</editor-fold>

    //<editor-fold desc="Get User Achievement List">
    @GET("/achievements/categories/{userId}")
    Observable<AchievementCategoryDTOList> getAchievementCategories(@Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get Achievement Category">
    @GET("/achievements/categories/{userId}")
    Observable<AchievementCategoryDTOList> getAchievementCategory(@Query("id") int categoryId, @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get Quest Bonuses">
    @GET("/achievements/questbonus")
    Observable<QuestBonusDTOList> getQuestBonuses();
    //</editor-fold>

    //<editor-fold desc="Share Achievement">
    @POST("/achievements/share/{userAchievementId}")
    Observable<BaseResponseDTO> shareUserAchievement(
            @Path("userAchievementId") int userAchievementId,
            @Body SocialShareReqFormDTO achievementShareFormDTO);
    //</editor-fold>
}
