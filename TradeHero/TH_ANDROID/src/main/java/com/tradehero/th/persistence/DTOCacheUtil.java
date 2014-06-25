package com.tradehero.th.persistence;

import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
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
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionIdCache;
import com.tradehero.th.persistence.market.ExchangeCompactListCache;
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
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.trade.TradeCache;
import com.tradehero.th.persistence.trade.TradeListCache;
import com.tradehero.th.persistence.translation.TranslationTokenCache;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class DTOCacheUtil
{
    protected final CurrentUserId currentUserId;

    //<editor-fold desc="Caches">
    protected final Lazy<AlertCache> alertCache;
    protected final Lazy<AlertCompactCache> alertCompactCache;
    protected final Lazy<AlertCompactListCache> alertCompactListCache;
    protected final Lazy<DiscussionCache> discussionCache;
    protected final Lazy<DiscussionListCacheNew> discussionListCache;
    protected final Lazy<ExchangeCompactListCache> exchangeListCache;
    protected final Lazy<FollowerSummaryCache> followerSummaryCache;
    protected final Lazy<GetPositionsCache> getPositionsCache;
    protected final Lazy<LeaderboardDefCache> leaderboardDefCache;
    protected final Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    protected final Lazy<LeaderboardPositionIdCache> leaderboardPositionIdCache;
    protected final Lazy<MessageHeaderCache> messageHeaderCache;
    protected final Lazy<MessageHeaderListCache> messageListCache;
    protected final Lazy<NotificationCache> notificationCache;
    protected final Lazy<NotificationListCache> notificationListCache;
    protected final Lazy<PortfolioCache> portfolioCache;
    protected final Lazy<PortfolioCompactCache> portfolioCompactCache;
    protected final Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    protected final Lazy<PositionCache> positionCache;
    protected final Lazy<PositionCompactCache> positionCompactCache;
    protected final Lazy<PositionCompactIdCache> positionCompactIdCache;
    protected final Lazy<ProductPurchaseCache> productPurchaseCache;
    protected final Lazy<ProviderCache> providerCache;
    protected final Lazy<ProviderListCache> providerListCache;
    protected final Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    protected final Lazy<SecurityCompactListCache> securityCompactListCache;
    protected final Lazy<SystemStatusCache> systemStatusCache;
    protected final Lazy<TradeCache> tradeCache;
    protected final Lazy<TradeListCache> tradeListCache;
    protected final Lazy<TranslationTokenCache> translationTokenCache;
    protected final Lazy<UserProfileCache> userProfileCache;
    protected final Lazy<UserFollowerCache> userFollowerCache;
    protected final Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache;
    protected final Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;
    protected final Lazy<WatchlistPositionCache> watchlistPositionCache;
    //</editor-fold>

    protected final Lazy<WarrantSpecificKnowledgeFactory> warrantSpecificKnowledgeFactoryLazy;
    protected final StringPreference serverEndpointPreference;

    //<editor-fold desc="Constructors">
    @Inject public DTOCacheUtil(
            CurrentUserId currentUserId,
            Lazy<AlertCache> alertCache,
            Lazy<AlertCompactCache> alertCompactCache,
            Lazy<AlertCompactListCache> alertCompactListCache,
            Lazy<DiscussionCache> discussionCache,
            Lazy<DiscussionListCacheNew> discussionListCache,
            Lazy<ExchangeCompactListCache> exchangeListCache,
            Lazy<FollowerSummaryCache> followerSummaryCache,
            Lazy<GetPositionsCache> getPositionsCache,
            Lazy<LeaderboardDefCache> leaderboardDefCache,
            Lazy<LeaderboardDefListCache> leaderboardDefListCache,
            Lazy<LeaderboardPositionIdCache> leaderboardPositionIdCache,
            Lazy<MessageHeaderCache> messageHeaderCache,
            Lazy<MessageHeaderListCache> messageListCache,
            Lazy<NotificationCache> notificationCache,
            Lazy<NotificationListCache> notificationListCache,
            Lazy<PortfolioCache> portfolioCache,
            Lazy<PortfolioCompactCache> portfolioCompactCache,
            Lazy<PortfolioCompactListCache> portfolioCompactListCache,
            Lazy<PositionCache> positionCache,
            Lazy<PositionCompactCache> positionCompactCache,
            Lazy<PositionCompactIdCache> positionCompactIdCache,
            Lazy<ProductPurchaseCache> productPurchaseCache,
            Lazy<ProviderCache> providerCache,
            Lazy<ProviderListCache> providerListCache,
            Lazy<SecurityPositionDetailCache> securityPositionDetailCache,
            Lazy<SecurityCompactListCache> securityCompactListCache,
            Lazy<SystemStatusCache> systemStatusCache,
            Lazy<TradeCache> tradeCache,
            Lazy<TradeListCache> tradeListCache,
            Lazy<TranslationTokenCache> translationTokenCache,
            Lazy<UserProfileCache> userProfileCache,
            Lazy<UserFollowerCache> userFollowerCache,
            Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache,
            Lazy<UserWatchlistPositionCache> userWatchlistPositionCache,
            Lazy<WatchlistPositionCache> watchlistPositionCache,
            Lazy<WarrantSpecificKnowledgeFactory> warrantSpecificKnowledgeFactoryLazy,
            @ServerEndpoint StringPreference serverEndpointPreference)
    {
        this.currentUserId = currentUserId;
        this.alertCache = alertCache;
        this.alertCompactCache = alertCompactCache;
        this.alertCompactListCache = alertCompactListCache;
        this.discussionCache = discussionCache;
        this.discussionListCache = discussionListCache;
        this.exchangeListCache = exchangeListCache;
        this.followerSummaryCache = followerSummaryCache;
        this.getPositionsCache = getPositionsCache;
        this.leaderboardDefCache = leaderboardDefCache;
        this.leaderboardDefListCache = leaderboardDefListCache;
        this.leaderboardPositionIdCache = leaderboardPositionIdCache;
        this.messageHeaderCache = messageHeaderCache;
        this.messageListCache = messageListCache;
        this.notificationCache = notificationCache;
        this.notificationListCache = notificationListCache;
        this.portfolioCache = portfolioCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.positionCache = positionCache;
        this.positionCompactCache = positionCompactCache;
        this.positionCompactIdCache = positionCompactIdCache;
        this.productPurchaseCache = productPurchaseCache;
        this.providerCache = providerCache;
        this.providerListCache = providerListCache;
        this.securityPositionDetailCache = securityPositionDetailCache;
        this.securityCompactListCache = securityCompactListCache;
        this.systemStatusCache = systemStatusCache;
        this.tradeCache = tradeCache;
        this.tradeListCache = tradeListCache;
        this.translationTokenCache = translationTokenCache;
        this.userProfileCache = userProfileCache;
        this.userFollowerCache = userFollowerCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.userWatchlistPositionCache = userWatchlistPositionCache;
        this.watchlistPositionCache = watchlistPositionCache;
        this.warrantSpecificKnowledgeFactoryLazy = warrantSpecificKnowledgeFactoryLazy;
        this.serverEndpointPreference = serverEndpointPreference;
    }
    //</editor-fold>

    public void clearUserRelatedCaches()
    {
        alertCache.get().invalidateAll();
        alertCompactCache.get().invalidateAll();
        alertCompactListCache.get().invalidateAll();
        discussionCache.get().invalidateAll();
        discussionListCache.get().invalidateAll();
        followerSummaryCache.get().invalidateAll();
        getPositionsCache.get().invalidateAll();
        leaderboardDefCache.get().invalidateAll();
        leaderboardDefListCache.get().invalidateAll();
        leaderboardPositionIdCache.get().invalidateAll();
        messageHeaderCache.get().invalidateAll();
        messageListCache.get().invalidateAll();
        notificationCache.get().invalidateAll();
        notificationListCache.get().invalidateAll();
        portfolioCache.get().invalidateAll();
        portfolioCompactCache.get().invalidateAll();
        portfolioCompactListCache.get().invalidateAll();
        positionCache.get().invalidateAll();
        positionCompactCache.get().invalidateAll();
        positionCompactIdCache.get().invalidateAll();
        productPurchaseCache.get().invalidateAll();
        providerCache.get().invalidateAll();
        providerListCache.get().invalidateAll();
        securityPositionDetailCache.get().invalidateAll();
        securityCompactListCache.get().invalidateAll();
        systemStatusCache.get().invalidateAll();
        tradeCache.get().invalidateAll();
        tradeListCache.get().invalidateAll();
        userProfileCache.get().invalidateAll();
        userFollowerCache.get().invalidateAll();
        userMessagingRelationshipCache.get().invalidateAll();
        // exchange list will never change per user, and need to be preloaded. Beside, autoFetch will automatically update it (?)
        // exchangeListCache.get().invalidateAll();
        userWatchlistPositionCache.get().invalidateAll();
        watchlistPositionCache.get().invalidateAll();

        warrantSpecificKnowledgeFactoryLazy.get().clear();
        serverEndpointPreference.delete();
    }

    // TODO split between those that need authentication and those that do not
    public void initialPrefetches()
    {
        preFetchExchanges();
        preFetchWatchlist();
        preFetchProviders();
        //preFetchTrending();
        preFetchAlerts();
        preFetchTranslationToken();
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
        // TODO Make it take care of the users's default stock exchange.
        this.securityCompactListCache.get().getOrFetchAsync(new TrendingBasicSecurityListType(1, TrendingFragment.DEFAULT_PER_PAGE));
    }

    public void preFetchAlerts()
    {
        alertCompactListCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    public void preFetchTranslationToken()
    {
        translationTokenCache.get().getOrFetchAsync(new TranslationTokenKey());
    }
}
