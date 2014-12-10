package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.StocksLeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardCacheRx;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class LeaderboardMarkUserStocksLoader extends LeaderboardMarkUserLoader<StocksLeaderboardUserDTO>
{
    @Inject LeaderboardCacheRx leaderboardCache;

    public LeaderboardMarkUserStocksLoader(Context context, PagedLeaderboardKey pagedLeaderboardKey)
    {
        super(context, pagedLeaderboardKey);
    }

    @Override public List<StocksLeaderboardUserDTO> loadInBackground()
    {
        Timber.d("loadInBackground %s", pagedLeaderboardKey);
        Timber.d("Loader with id = %d", getId());

        try
        {
            LeaderboardDTO fetched = leaderboardCache.get(pagedLeaderboardKey).toBlocking().first().second;

            if (fetched == null)
            {
                return null;
            }

            markUtc = fetched.markUtc;
            Timber.d("Leaderboard marked at %s", fetched.markUtc);
            return fetched.users;
        }
        catch (Throwable throwable)
        {
            Timber.e("Error loading Leaderboard ranking", throwable);
            return null;
        }
    }

}
