package com.tradehero.th.persistence;

import com.tradehero.common.utils.THLog;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import com.tradehero.th.persistence.leaderboard.position.GetLeaderboardPositionsCache;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionCache;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionIdCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.position.PositionCompactCache;
import com.tradehero.th.persistence.position.PositionCompactIdCache;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.social.FollowerSummaryCache;
import com.tradehero.th.persistence.social.UserFollowerCache;
import com.tradehero.th.persistence.trade.TradeCache;
import com.tradehero.th.persistence.trade.TradeListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/20/13 Time: 6:57 PM To change this template use File | Settings | File Templates. */
public class DTOCacheUtil
{
    public static final String TAG = DTOCacheUtil.class.getSimpleName();

    @Inject static protected Lazy<AlertCache> alertCache;
    @Inject static protected Lazy<AlertCompactCache> alertCompactCache;
    @Inject static protected Lazy<AlertCompactListCache> alertCompactListCache;
    @Inject static protected Lazy<FollowerSummaryCache> followerSummaryCache;
    @Inject static protected Lazy<GetPositionsCache> getPositionsCache;
    @Inject static protected Lazy<GetLeaderboardPositionsCache> getLeaderboardPositionsCache;
    @Inject static protected Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject static protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject static protected Lazy<LeaderboardPositionCache> leaderboardPositionCache;
    @Inject static protected Lazy<LeaderboardPositionIdCache> leaderboardPositionIdCache;
    @Inject static protected Lazy<PortfolioCache> portfolioCache;
    @Inject static protected Lazy<PortfolioCompactCache> portfolioCompactCache;
    @Inject static protected Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject static protected Lazy<PositionCache> positionCache;
    @Inject static protected Lazy<PositionCompactCache> positionCompactCache;
    @Inject static protected Lazy<PositionCompactIdCache> positionCompactIdCache;
    @Inject static protected Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @Inject static protected Lazy<TradeCache> tradeCache;
    @Inject static protected Lazy<TradeListCache> tradeListCache;
    @Inject static protected Lazy<UserProfileCache> userProfileCache;
    @Inject static protected Lazy<UserFollowerCache> userFollowerCache;

    public static void clearUserRelatedCaches()
    {
        alertCache.get().invalidateAll();
        alertCompactCache.get().invalidateAll();
        alertCompactListCache.get().invalidateAll();
        followerSummaryCache.get().invalidateAll();
        getPositionsCache.get().invalidateAll();
        getLeaderboardPositionsCache.get().invalidateAll();
        leaderboardDefCache.get().invalidateAll();
        leaderboardDefListCache.get().invalidateAll();
        leaderboardPositionCache.get().invalidateAll();
        leaderboardPositionIdCache.get().invalidateAll();
        portfolioCache.get().invalidateAll();
        portfolioCompactCache.get().invalidateAll();
        portfolioCompactListCache.get().invalidateAll();
        positionCache.get().invalidateAll();
        positionCompactCache.get().invalidateAll();
        positionCompactIdCache.get().invalidateAll();
        securityPositionDetailCache.get().invalidateAll();
        THLog.d(TAG, "securityPositionDetailCache cleared");
        tradeCache.get().invalidateAll();
        tradeListCache.get().invalidateAll();
        userProfileCache.get().invalidateAll();
        userFollowerCache.get().invalidateAll();
    }
}
