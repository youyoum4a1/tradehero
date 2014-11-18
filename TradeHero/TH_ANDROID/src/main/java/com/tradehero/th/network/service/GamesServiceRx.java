package com.tradehero.th.network.service;

import com.tradehero.th.api.games.GamesListDTO;
import retrofit.http.GET;
import rx.Observable;

interface GamesServiceRx
{
    @GET("/games/all")
    Observable<GamesListDTO> getGames();

}
