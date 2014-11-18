package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.games.MiniGameDefDTO;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

interface MiniGameServiceRx
{
    @GET("/games/all")
    Observable<List<MiniGameDefDTO>> getAllGames();

    @POST("/games/{gameId}/recordscore")
    Observable<BaseResponseDTO> recordScore(
            @Path("gameId") int gameId,
            @Query("score") int score,
            @Query("level") int level);
}
