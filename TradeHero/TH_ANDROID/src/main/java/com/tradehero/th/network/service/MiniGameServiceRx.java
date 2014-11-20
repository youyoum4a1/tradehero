package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.games.MiniGameDefDTO;
import com.tradehero.th.api.games.MiniGameDefDTOList;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

interface MiniGameServiceRx
{
    @GET("/games/all")
    Observable<MiniGameDefDTOList> getAllGames();

    @GET("/games/{gameId}")
    Observable<MiniGameDefDTO> getGame(
            @Path("gameId") int gameId);

    @POST("/games/{gameId}/recordscore")
    Observable<BaseResponseDTO> recordScore(
            @Path("gameId") int gameId,
            @Query("score") int score,
            @Query("level") int level);
}
