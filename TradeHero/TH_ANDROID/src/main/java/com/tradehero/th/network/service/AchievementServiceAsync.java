package com.tradehero.th.network.service;

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
    @GET("/achievements/categories")
    void getAchievementCategories(@Query("userId") int userId,
            Callback<AchievementCategoryDTOList> callback);
    //</editor-fold>
}
