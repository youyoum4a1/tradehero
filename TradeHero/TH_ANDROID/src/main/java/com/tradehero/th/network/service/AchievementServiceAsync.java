package com.tradehero.th.network.service;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface AchievementServiceAsync
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

    //<editor-fold desc="Get User Achievement">
    @GET("/achievements/categories/{userId}")
    void getAchievementCategory(@Query("id") int categoryId, @Path("userId") int userId,
            Callback<AchievementCategoryDTOList> callback);

    //<editor-fold desc="Get Mock Quest Bonus">
    @GET("/achievements/mockdaily/{contiguousCount}")
    void getMockQuestBonus(@Path("contiguousCount") int contiguousCount, @Query("xpEarned") int xpEarned, @Query("xpTotal") int xpTotal, Callback<DTO> middleCallback);
    //</editor-fold>
}
