package com.tradehero.th.persistence;

import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.models.security.WarrantSpecificKnowledgeFactory;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import com.tradehero.th.persistence.leaderboard.position.GetLeaderboardPositionsCache;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionCache;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionIdCache;
import com.tradehero.th.persistence.market.ExchangeListCache;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.notification.NotificationListCache;
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
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class DTOCacheUtil
{
    @Inject protected CurrentUserId currentUserId;
    @Inject protected Lazy<AlertCache> alertCache;
    @Inject protected Lazy<AlertCompactCache> alertCompactCache;
    @Inject protected Lazy<AlertCompactListCache> alertCompactListCache;
    @Inject protected Lazy<ExchangeListCache> exchangeListCache;
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
    @Inject protected Lazy<ProviderCache> providerCache;
    @Inject protected Lazy<ProviderListCache> providerListCache;
    @Inject protected Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @Inject protected Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject protected Lazy<TradeCache> tradeCache;
    @Inject protected Lazy<TradeListCache> tradeListCache;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected Lazy<UserFollowerCache> userFollowerCache;
    @Inject protected Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache;
    @Inject protected Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;
    @Inject protected Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject protected Lazy<ProductPurchaseCache> productPurchaseCache;

    @Inject protected Lazy<WarrantSpecificKnowledgeFactory> warrantSpecificKnowledgeFactoryLazy;
    @Inject @ServerEndpoint StringPreference serverEndpointPreference;

    @Inject Lazy<MessageHeaderListCache> messageListCache;
    @Inject Lazy<MessageHeaderCache> messageHeaderCache;
    @Inject Lazy<DiscussionListCacheNew> discussionListCache;
    @Inject Lazy<DiscussionCache> discussionCache;

    @Inject Lazy<NotificationCache> notificationCache;
    @Inject Lazy<NotificationListCache> notificationListCache;

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
        userMessagingRelationshipCache.get().invalidateAll();
        // exchange list will never change per user, and need to be preloaded. Beside, autoFetch will automatically update it (?)
        // exchangeListCache.get().invalidateAll();
        userWatchlistPositionCache.get().invalidateAll();
        watchlistPositionCache.get().invalidateAll();
        providerListCache.get().invalidateAll();
        providerCache.get().invalidateAll();
        productPurchaseCache.get().invalidateAll();

        warrantSpecificKnowledgeFactoryLazy.get().clear();
        serverEndpointPreference.delete();

        messageHeaderCache.get().invalidateAll();
        messageListCache.get().invalidateAll();
        discussionCache.get().invalidateAll();
        discussionListCache.get().invalidateAll();

        notificationCache.get().invalidateAll();
        notificationListCache.get().invalidateAll();
    }

    public void initialPrefetches()
    {
        preFetchExchanges();
        preFetchWatchlist();
        preFetchProviders();
//        preFetchTrending(); // It would be too heavy on the server as we now jump first to Trending.
        preFetchAlerts();
    }
    
    public void preFetchExchanges()
    {
        exchangeListCache.get().getOrFetchAsync(new ExchangeListType());
    }
    
    public void preFetchWatchlist()
    {
        userWatchlistPositionCache.get().autoFetch(currentUserId.toUserBaseKey());
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
