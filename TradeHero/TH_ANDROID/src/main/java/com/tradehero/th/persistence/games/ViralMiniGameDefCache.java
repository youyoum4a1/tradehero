package com.tradehero.th.persistence.games;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.games.ViralMiniGameDefDTO;
import com.tradehero.th.api.games.ViralMiniGameDefKey;
import com.tradehero.th.network.service.MiniGameServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class ViralMiniGameDefCache extends BaseFetchDTOCacheRx<ViralMiniGameDefKey, ViralMiniGameDefDTO>
{
    private static final int DEFAULT_MAX_VALUE = 200;

    @NonNull protected final MiniGameServiceWrapper miniGameServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public ViralMiniGameDefCache(
            @NonNull DTOCacheUtilRx dtoCacheUtilRx,
            @NonNull MiniGameServiceWrapper miniGameServiceWrapper)
    {
        super(DEFAULT_MAX_VALUE, dtoCacheUtilRx);
        this.miniGameServiceWrapper = miniGameServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<ViralMiniGameDefDTO> fetch(@NonNull ViralMiniGameDefKey key)
    {
        return miniGameServiceWrapper.getViralGame(key);
    }

    public void onNext(@NonNull List<? extends ViralMiniGameDefDTO> miniGames)
    {
        for (ViralMiniGameDefDTO miniGame : miniGames)
        {
            onNext(miniGame.getDTOKey(), miniGame);
        }
    }
}