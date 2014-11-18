package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.games.GamesListDTO;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

interface GamesServiceRx
{
    @GET("/games/all")
    Observable<GamesListDTO> getGames();

    @POST("/games/{gameId}/recordscore")
    Observable<BaseResponseDTO> recordScore(
            @Path("gameId") int gameId,
            @Query("score") int score,
            @Query("level") int level);
}
