package com.tradehero.th.network.service;

import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface AchievementService
{
    //<editor-fold desc="Get Trader Level Definition">
    @GET("/achievements/traderleveldefs")
    LevelDefDTOList getLevelDefs();
    //</editor-fold>

    //<editor-fold desc="Get User Achievement Details">
    @GET("/achievements/achievement/{userAchievementId}")
    UserAchievementDTO getUserAchievementDetails(
            @Path("userAchievementId") int userAchievementId);
    //</editor-fold>

    //<editor-fold desc="Get User Achievement List">
    @GET("/achievements/categories")
    AchievementCategoryDTOList getAchievementCategories(@Query("userId") int userId);
    //</editor-fold>
}
