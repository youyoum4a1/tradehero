package com.tradehero.th.network.service;

import com.tradehero.th.api.game.MiniGameDefDTO;
import java.util.List;
import retrofit.http.GET;
import rx.Observable;

public interface MiniGameServiceRx
{
    @GET("/games/all")
    Observable<List<MiniGameDefDTO>> getAllGames();
}
