package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface AchievementMockService
{
    //<editor-fold desc="Get Mock Quest Bonus">
    @GET("/achievements/mockdaily/{contiguousCount}")
    BaseResponseDTO getMockQuestBonus(
            @Path("contiguousCount") int contiguousCount,
            @Query("xpEarned") int xpEarned,
            @Query("xpTotal") int xpTotal);
    //</editor-fold>
}
