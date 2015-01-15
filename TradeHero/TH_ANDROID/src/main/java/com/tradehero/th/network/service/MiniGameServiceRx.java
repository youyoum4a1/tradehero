package com.tradehero.th.network.service;

import com.tradehero.th.api.games.MiniGameDefDTO;
import com.tradehero.th.api.games.MiniGameDefDTOList;
import com.tradehero.th.api.games.MiniGameScoreResponseDTO;
import com.tradehero.th.api.games.ViralMiniGameDefDTO;
import com.tradehero.th.api.games.ViralMiniGameDefDTOList;
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
    Observable<MiniGameScoreResponseDTO> recordScore(
            @Path("gameId") int gameId,
            @Query("score") int score,
            @Query("level") int level);

    @GET("/games/viral/list")
    Observable<ViralMiniGameDefDTOList> getViralGameList();

    @GET("/games/viral/MiniGameDetail/{viralMiniGameId}")
    Observable<ViralMiniGameDefDTO> getViralGameDetail(@Path("viralMiniGameId")int viralGameId);
}
