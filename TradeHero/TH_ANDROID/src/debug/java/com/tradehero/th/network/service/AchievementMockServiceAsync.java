package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

interface AchievementMockServiceAsync
{
    //<editor-fold desc="Get Mock Quest Bonus">
    @GET("/achievements/mockdaily/{contiguousCount}")
    void getMockQuestBonus(
            @Path("contiguousCount") int contiguousCount,
            @Query("xpEarned") int xpEarned,
            @Query("xpTotal") int xpTotal,
            Callback<BaseResponseDTO> middleCallback);
    //</editor-fold>
}
