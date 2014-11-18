package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.games.GameId;
import com.tradehero.th.api.games.GameScore;
import com.tradehero.th.api.games.GamesListDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class GamesServiceWrapper
{
    @NonNull private final GamesServiceRx gamesServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public GamesServiceWrapper(@NonNull GamesServiceRx gamesServiceRx)
    {
        this.gamesServiceRx = gamesServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get Games list">
    @NonNull public Observable<GamesListDTO> getGamesRx()
    {
        return gamesServiceRx.getGames();
    }
    //</editor-fold>

    //<editor-fold desc="Record Score">
    @NonNull public Observable<BaseResponseDTO> recordScore(@NonNull GameId gameId, @NonNull GameScore score)
    {
        return gamesServiceRx.recordScore(gameId.key, score.score, score.level);
    }
    //</editor-fold>
}
