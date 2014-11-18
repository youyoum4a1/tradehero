package com.tradehero.th.persistence.games;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.games.GamesListDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.GamesServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class GamesListCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, GamesListDTO>
{
    public static final int DEFAULT_SUBJECT_SIZE = 1;

    @NonNull private final Lazy<GamesServiceWrapper> gamesServiceWrapperLazy;

    //<editor-fold desc="Constructors">
    @Inject public GamesListCacheRx(
            @NonNull Lazy<GamesServiceWrapper> gamesServiceWrapperLazy,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_SUBJECT_SIZE, DEFAULT_SUBJECT_SIZE, DEFAULT_SUBJECT_SIZE, dtoCacheUtil);
        this.gamesServiceWrapperLazy = gamesServiceWrapperLazy;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<GamesListDTO> fetch(@NonNull UserBaseKey key)
    {
        return gamesServiceWrapperLazy.get().getGamesRx();
    }

    @Override public void onNext(@NonNull UserBaseKey key, @NonNull GamesListDTO gamesListDTO)
    {
        super.onNext(key, gamesListDTO);
    }
}
