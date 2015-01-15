package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.games.GameScore;
import com.tradehero.th.api.games.MiniGameDefDTO;
import com.tradehero.th.api.games.MiniGameDefDTOList;
import com.tradehero.th.api.games.MiniGameDefKey;
import com.tradehero.th.api.games.MiniGameScoreResponseDTO;
import com.tradehero.th.api.games.ViralMiniGameDefDTO;
import com.tradehero.th.api.games.ViralMiniGameDefDTOList;
import com.tradehero.th.api.games.ViralMiniGameDefKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class MiniGameServiceWrapper
{
    @NonNull private final MiniGameServiceRx miniGameServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public MiniGameServiceWrapper(@NonNull MiniGameServiceRx miniGameServiceRx)
    {
        this.miniGameServiceRx = miniGameServiceRx;
    }
    //</editor-fold>

    @NonNull public Observable<MiniGameDefDTOList> getAllGames()
    {
        // do preprocessing here, or since it is a GET request, let the http layer handle caching
        return miniGameServiceRx.getAllGames();
    }

    @NonNull public Observable<MiniGameDefDTO> getGame(@NonNull MiniGameDefKey gameId)
    {
        return miniGameServiceRx.getGame(gameId.key);
    }

    @NonNull public Observable<MiniGameScoreResponseDTO> recordScore(@NonNull MiniGameDefKey gameId, @NonNull GameScore score)
    {
        return miniGameServiceRx.recordScore(gameId.key, score.score, score.level);
    }

    @NonNull public Observable<ViralMiniGameDefDTOList> getAllViralGame()
    {
        return miniGameServiceRx.getViralGameList();
    }

    @NonNull public Observable<ViralMiniGameDefDTO> getViralGame(@NonNull ViralMiniGameDefKey gameId)
    {
        return miniGameServiceRx.getViralGameDetail(gameId.key);
    }
}
