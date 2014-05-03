package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class LeaderboardManager
{
    @Inject protected DatabaseCache dbCache;
    @Inject protected LeaderboardStore leaderboardStore;

    public LeaderboardDTO firstOrDefault(Query query, boolean forceReload) throws IOException
    {
        List<LeaderboardDTO> dtos = getLeaderboards(query, forceReload);
        return (dtos != null && dtos.size() > 0) ? dtos.get(0) : null;
    }

    public List<LeaderboardDTO> getLeaderboards(Query query, boolean forceReload) throws IOException
    {
        leaderboardStore.setQuery(query);
        // TODO This is not thread-safe; the query could have been changed between these 2 calls.
        return forceReload ? dbCache.requestAndStore(leaderboardStore) : dbCache.loadOrRequest(leaderboardStore);
    }
}
