package com.tradehero.th.network.service;

import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import retrofit.http.GET;
import retrofit.http.Path;

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
}
