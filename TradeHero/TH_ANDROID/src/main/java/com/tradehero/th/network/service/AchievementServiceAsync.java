package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.achievement.AchievementShareReqFormDTO;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

interface AchievementServiceAsync
{
    //<editor-fold desc="Get Trader Level Definition">
    @GET("/checkDisplayNameAvailable")
    void getLevelDefs(Callback<LevelDefDTOList> callback);
    //</editor-fold>

    //<editor-fold desc="Get User Achievement Details">
    @GET("/achievements/achievement/{userAchievementId}")
    void getUserAchievementDetails(
            @Path("userAchievementId") int userAchievementId,
            Callback<UserAchievementDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get User Achievement List">
    @GET("/achievements/categories/{userId}")
    void getAchievementCategories(@Path("userId") int userId,
            Callback<AchievementCategoryDTOList> callback);
    //</editor-fold>

    //<editor-fold desc="Get Achievement Category">
    @GET("/achievements/categories/{userId}")
    void getAchievementCategory(@Query("id") int categoryId, @Path("userId") int userId,
            Callback<AchievementCategoryDTOList> callback);
    //</editor-fold>

    //<editor-fold desc="Get Quest Bonuses">
    @GET("/achievements/questbonus") void getQuestBonuses(Callback<QuestBonusDTOList> callback);
    //</editor-fold>

    //<editor-fold desc="Share Achievement">
    @POST("/achievements/share/{userAchievementId}")
    void shareUserAchievement(
            @Path("userAchievementId") int userAchievementId,
            @Body AchievementShareReqFormDTO achievementShareFormDTO,
            Callback<BaseResponseDTO> middleCallback);
    //</editor-fold>
}
