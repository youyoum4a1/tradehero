package com.tradehero.th.persistence.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOFactory;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.schedulers.Schedulers;

@Singleton @UserCache
public class LeaderboardDefListCacheRx extends BaseFetchDTOCacheRx<LeaderboardDefListKey, LeaderboardDefDTOList>
{
    private static final int DEFAULT_MAX_SIZE = 1000;

    @NonNull private final LeaderboardServiceWrapper leaderboardServiceWrapper;
    @NonNull private final LeaderboardDefCacheRx leaderboardDefCache;
    @NonNull private final LeaderboardDefDTOFactory leaderboardDefDTOFactory;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefListCacheRx(
            @NonNull LeaderboardServiceWrapper leaderboardServiceWrapper,
            @NonNull LeaderboardDefCacheRx leaderboardDefCache,
            @NonNull LeaderboardDefDTOFactory leaderboardDefDTOFactory,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, 5, 5, dtoCacheUtil);
        this.leaderboardServiceWrapper = leaderboardServiceWrapper;
        this.leaderboardDefCache = leaderboardDefCache;
        this.leaderboardDefDTOFactory = leaderboardDefDTOFactory;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<LeaderboardDefDTOList> fetch(@NonNull LeaderboardDefListKey listKey)
    {
        return leaderboardServiceWrapper.getLeaderboardDefinitionsRx()
                .observeOn(Schedulers.computation())
                .map(leaderboardDefDTOFactory::file)
                .doOnNext(this::put) // We have to do it here to avoid an infinite loop
                .map(filed -> {
                    LeaderboardDefDTOList value = filed.get(listKey);
                    if (value == null)
                    {
                        throw new IllegalArgumentException("Key " + listKey + " not found");
                    }
                    return value;
                });
    }

    @Override public void onNext(@NonNull LeaderboardDefListKey key, @NonNull LeaderboardDefDTOList value)
    {
        leaderboardDefCache.onNext(value);
        super.onNext(key, value);
    }

    public void put(@NonNull Map<LeaderboardDefListKey, LeaderboardDefDTOList> keyMap)
    {
        for (Map.Entry<LeaderboardDefListKey, LeaderboardDefDTOList> entry : keyMap.entrySet())
        {
            onNext(entry.getKey(), entry.getValue());
        }
    }
}
