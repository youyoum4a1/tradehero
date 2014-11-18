package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.games.GameScore;
import com.tradehero.th.api.games.MiniGameDefDTO;
import com.tradehero.th.api.games.MiniGameDefDTOList;
import com.tradehero.th.api.games.MiniGameDefKey;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton
public class MiniGameServiceWrapper
{
    private final MiniGameServiceRx miniGameServiceRx;

    @Inject public MiniGameServiceWrapper(MiniGameServiceRx miniGameServiceRx)
    {
        this.miniGameServiceRx = miniGameServiceRx;
    }

    @NonNull public Observable<MiniGameDefDTOList> getAllGames()
    {
        // do preprocessing here, or since it is a GET request, let the http layer handle caching
        return miniGameServiceRx.getAllGames();
    }

    @NonNull public Observable<MiniGameDefDTO> getGame(@NonNull MiniGameDefKey gameId)
    {
        return miniGameServiceRx.getGame(gameId.key);
    }

    @NonNull public Observable<BaseResponseDTO> recordScore(@NonNull MiniGameDefKey gameId, @NonNull GameScore score)
    {
        return miniGameServiceRx.recordScore(gameId.key, score.score, score.level);
    }
}
