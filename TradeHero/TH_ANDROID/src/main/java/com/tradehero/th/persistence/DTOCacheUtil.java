package com.tradehero.th.persistence;

import com.tradehero.th.api.competition.ProviderListKey;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.security.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.googleplay.THIABPurchaseCache;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.models.security.WarrantSpecificKnowledgeFactory;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import com.tradehero.th.persistence.leaderboard.position.GetLeaderboardPositionsCache;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionCache;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionIdCache;
import com.tradehero.th.persistence.market.ExchangeListCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.position.PositionCompactCache;
import com.tradehero.th.persistence.position.PositionCompactIdCache;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.persistence.social.FollowerSummaryCache;
import com.tradehero.th.persistence.social.UserFollowerCache;
import com.tradehero.th.persistence.trade.TradeCache;
import com.tradehero.th.persistence.trade.TradeListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 11/20/13 Time: 6:57 PM To change this template use File | Settings | File Templates. */
@Singleton public class DTOCacheUtil
{
    public final String TAG = DTOCacheUtil.class.getSimpleName();

    @Inject protected CurrentUserId currentUserId;
    @Inject protected Lazy<AlertCache> alertCache;
    @Inject protected Lazy<AlertCompactCache> alertCompactCache;
    @Inject protected Lazy<AlertCompactListCache> alertCompactListCache;
    @Inject protected Lazy<FollowerSummaryCache> followerSummaryCache;
    @Inject protected Lazy<GetPositionsCache> getPositionsCache;
    @Inject protected Lazy<GetLeaderboardPositionsCache> getLeaderboardPositionsCache;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject protected Lazy<LeaderboardPositionCache> leaderboardPositionCache;
    @Inject protected Lazy<LeaderboardPositionIdCache> leaderboardPositionIdCache;
    @Inject protected Lazy<PortfolioCache> portfolioCache;
    @Inject protected Lazy<PortfolioCompactCache> portfolioCompactCache;
    @Inject protected Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject protected Lazy<PositionCache> positionCache;
    @Inject protected Lazy<PositionCompactCache> positionCompactCache;
    @Inject protected Lazy<PositionCompactIdCache> positionCompactIdCache;
    @Inject protected Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @Inject protected Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject protected Lazy<TradeCache> tradeCache;
    @Inject protected Lazy<TradeListCache> tradeListCache;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected Lazy<UserFollowerCache> userFollowerCache;
    @Inject protected Lazy<ExchangeListCache> exchangeListCache;
    @Inject protected Lazy<UserWatchlistPositionCache> watchlistPositionCache;
    @Inject protected Lazy<ProviderListCache> providerListCache;
    @Inject protected Lazy<ProviderCache> providerCache;
    @Inject protected Lazy<THIABPurchaseCache> thiabSubscriptionCache;

    @Inject protected Lazy<WarrantSpecificKnowledgeFactory> warrantSpecificKnowledgeFactoryLazy;

    @Inject public DTOCacheUtil()
    {
    }

    public void clearUserRelatedCaches()
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
        securityCompactListCache.get().invalidateAll();
        tradeCache.get().invalidateAll();
        tradeListCache.get().invalidateAll();
        userProfileCache.get().invalidateAll();
        userFollowerCache.get().invalidateAll();
        exchangeListCache.get().invalidateAll();
        watchlistPositionCache.get().invalidateAll();
        providerListCache.get().invalidateAll();
        providerCache.get().invalidateAll();
        thiabSubscriptionCache.get().invalidateAll();

        warrantSpecificKnowledgeFactoryLazy.get().clear();
    }

    public void initialPrefetches()
    {
        preFetchExchanges();
        preFetchWatchlist();
        preFetchProviders();
        preFetchTrending();
        preFetchAlerts();
    }
    
    public void preFetchExchanges()
    {
        exchangeListCache.get().autoFetch(new ExchangeListType());
    }
    
    public void preFetchWatchlist()
    {
        watchlistPositionCache.get().autoFetch(currentUserId.toUserBaseKey());
    }

    public void preFetchProviders()
    {
        this.providerListCache.get().autoFetch(new ProviderListKey());
    }

    public void preFetchTrending()
    {
        this.securityCompactListCache.get().autoFetch(new TrendingBasicSecurityListType(TrendingSecurityListType.ALL_EXCHANGES, 1, TrendingFragment.DEFAULT_PER_PAGE));
    }

    public void preFetchAlerts()
    {
        alertCompactListCache.get().autoFetch(currentUserId.toUserBaseKey());
    }
}
