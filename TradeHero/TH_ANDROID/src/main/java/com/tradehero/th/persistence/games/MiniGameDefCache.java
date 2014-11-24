package com.tradehero.th.persistence.games;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.games.MiniGameDefDTO;
import com.tradehero.th.api.games.MiniGameDefKey;
import com.tradehero.th.network.service.MiniGameServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class MiniGameDefCache extends BaseFetchDTOCacheRx<MiniGameDefKey, MiniGameDefDTO>
{
    private static final int DEFAULT_MAX_VALUE = 200;
    private static final int DEFAULT_MAX_SUBJECT = 10;

    @NonNull protected final MiniGameServiceWrapper miniGameServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public MiniGameDefCache(
            @NonNull DTOCacheUtilRx dtoCacheUtilRx,
            @NonNull MiniGameServiceWrapper miniGameServiceWrapper)
    {
        super(DEFAULT_MAX_VALUE, DEFAULT_MAX_SUBJECT, DEFAULT_MAX_SUBJECT, dtoCacheUtilRx);
        this.miniGameServiceWrapper = miniGameServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<MiniGameDefDTO> fetch(@NonNull MiniGameDefKey key)
    {
        return miniGameServiceWrapper.getGame(key);
    }

    public void onNext(@NonNull List<? extends MiniGameDefDTO> miniGames)
    {
        for (MiniGameDefDTO miniGame : miniGames)
        {
            onNext(miniGame.getDTOKey(), miniGame);
        }
    }
}
