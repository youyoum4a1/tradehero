package com.tradehero.th.persistence.leaderboard;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import javax.inject.Inject;
import rx.Observable;

public class PagedLeaderboardWrapperCacheRx implements DTOCacheRx<PagedLeaderboardKey, LeaderboardDTO>
{
    @NonNull private final LeaderboardCacheRx leaderboardCache;

    //<editor-fold desc="Constructors">
    @Inject public PagedLeaderboardWrapperCacheRx(@NonNull LeaderboardCacheRx leaderboardCache)
    {
        super();
        this.leaderboardCache = leaderboardCache;
    }
    //</editor-fold>

    @Override @NonNull public Observable<Pair<PagedLeaderboardKey, LeaderboardDTO>> get(@NonNull PagedLeaderboardKey key)
    {
        return leaderboardCache.get(key)
                .map(pair -> Pair.create(key, pair.second));
    }

    @Override public void onNext(PagedLeaderboardKey key, LeaderboardDTO value)
    {
        leaderboardCache.onNext(key, value);
    }

    @Override public void invalidate(@NonNull PagedLeaderboardKey key)
    {
        leaderboardCache.invalidate(key);
    }

    @Override public void invalidateAll()
    {
        leaderboardCache.invalidateAll();
    }
}
