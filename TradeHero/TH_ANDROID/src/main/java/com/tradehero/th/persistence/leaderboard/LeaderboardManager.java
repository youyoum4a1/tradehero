package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.th.persistence.user.UserStore;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:39 PM Copyright (c) TradeHero */
@Singleton
public class LeaderboardManager
{
    @Inject protected DatabaseCache dbCache;
    @Inject protected LeaderboardStore leaderboardStore;

    public LeaderboardDTO getLeaderboard(int leaderboardId, boolean forceReload) throws IOException
    {
        Query query = new Query();
        query.setId(leaderboardId);
        List<LeaderboardDTO> dtos = getLeaderboards(query, forceReload);
        return (dtos != null && dtos.size()>0) ? dtos.get(0) : null;
    }

    public List<LeaderboardDTO> getLeaderboards(Query query, boolean forceReload) throws IOException
    {
        leaderboardStore.setQuery(query);
        return forceReload ? dbCache.requestAndStore(leaderboardStore) : dbCache.loadOrRequest(leaderboardStore);
    }
}
