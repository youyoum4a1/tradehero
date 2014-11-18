package com.tradehero.th.network.service;

import com.tradehero.th.api.game.MiniGameDefDTO;
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

    public Observable<List<MiniGameDefDTO>> getAllGames()
    {
        // do preprocessing here
        return miniGameServiceRx.getAllGames();
    }
}
