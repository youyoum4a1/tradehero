package com.tradehero.th.network.service;

import com.tradehero.th.api.achievement.AchievementCategoryDTO;
import com.tradehero.th.api.achievement.AchievementCategoryDTOList;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
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
    @GET("/achievements/categories/{userId}")
    AchievementCategoryDTOList getAchievementCategories(@Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get User Achievement">
    @GET("/achievements/categories/{userId}")
    AchievementCategoryDTOList getAchievementCategory(@Query("id") int categoryId, @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get Quest Bonus">
    @GET("/achievements/questbonus")
    QuestBonusDTOList getQuestBonuses();
    //</editor-fold>
}
