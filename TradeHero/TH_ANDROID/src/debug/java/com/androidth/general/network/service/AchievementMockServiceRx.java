package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface AchievementMockServiceRx
{
    //<editor-fold desc="Get Mock Quest Bonus">
    @GET("api/achievements/mockdaily/{contiguousCount}")
    Observable<BaseResponseDTO> getMockQuestBonus(
            @Path("contiguousCount") int contiguousCount,
            @Query("xpEarned") int xpEarned,
            @Query("xpTotal") int xpTotal);
    //</editor-fold>
}
