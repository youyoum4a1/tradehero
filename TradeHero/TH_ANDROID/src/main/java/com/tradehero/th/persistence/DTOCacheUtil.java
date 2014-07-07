package com.tradehero.th.persistence;

import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserProfileDTO;
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
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCache;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class DTOCacheUtil
{
    protected final CurrentUserId currentUserId;

    //<editor-fold desc="Caches">
    protected final Lazy<AlertCache> alertCache;
    protected final Lazy<AlertCompactCache> alertCompactCache;
    protected final Lazy<AlertCompactListCache> alertCompactListCache;
    protected final Lazy<DiscussionCache> discussionCache;
    protected final Lazy<DiscussionListCacheNew> discussionListCache;
    protected final Lazy<ExchangeCompactListCache> exchangeCompactListCache;
    protected final Lazy<FollowerSummaryCache> followerSummaryCache;
    protected final Lazy<GetPositionsCache> getPositionsCache;
    protected final Lazy<HomeContentCache> homeContentCache;
    protected final Lazy<LeaderboardDefCache> leaderboardDefCache;
    protected final Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    protected final Lazy<LeaderboardPositionIdCache> leaderboardPositionIdCache;
    protected final Lazy<LeaderboardFriendsCache> leaderboardFriendsCache;
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
    @NotNull protected final UserBaseDTOUtil userBaseDTOUtil;

    //<editor-fold desc="Constructors">
    @Inject public DTOCacheUtil(
            CurrentUserId currentUserId,
            Lazy<AlertCache> alertCache,
            Lazy<AlertCompactCache> alertCompactCache,
            Lazy<AlertCompactListCache> alertCompactListCache,
            Lazy<DiscussionCache> discussionCache,
            Lazy<DiscussionListCacheNew> discussionListCache,
            Lazy<ExchangeCompactListCache> exchangeCompactListCache,
            Lazy<FollowerSummaryCache> followerSummaryCache,
            Lazy<GetPositionsCache> getPositionsCache,
            Lazy<HomeContentCache> homeContentCache,
            Lazy<LeaderboardDefCache> leaderboardDefCache,
            Lazy<LeaderboardDefListCache> leaderboardDefListCache,
            Lazy<LeaderboardPositionIdCache> leaderboardPositionIdCache,
            Lazy<LeaderboardFriendsCache> leaderboardFriendsCache, Lazy<MessageHeaderCache> messageHeaderCache,
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
            @ServerEndpoint StringPreference serverEndpointPreference,
            @NotNull UserBaseDTOUtil userBaseDTOUtil)
    {
        this.currentUserId = currentUserId;
        this.alertCache = alertCache;
        this.alertCompactCache = alertCompactCache;
        this.alertCompactListCache = alertCompactListCache;
        this.discussionCache = discussionCache;
        this.discussionListCache = discussionListCache;
        this.exchangeCompactListCache = exchangeCompactListCache;
        this.followerSummaryCache = followerSummaryCache;
        this.getPositionsCache = getPositionsCache;
        this.homeContentCache = homeContentCache;
        this.leaderboardDefCache = leaderboardDefCache;
        this.leaderboardDefListCache = leaderboardDefListCache;
        this.leaderboardPositionIdCache = leaderboardPositionIdCache;
        this.leaderboardFriendsCache = leaderboardFriendsCache;
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
        this.userBaseDTOUtil = userBaseDTOUtil;
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
        homeContentCache.get().invalidateAll();
        leaderboardDefCache.get().invalidateAll();
        leaderboardDefListCache.get().invalidateAll();
        leaderboardPositionIdCache.get().invalidateAll();
        leaderboardFriendsCache.get().invalidateAll();
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

    public void anonymousPrefetches()
    {
        preFetchExchanges();
        preFetchTrending();
        preFetchProviders();
    }

    public void preFetchExchanges()
    {
        exchangeCompactListCache.get().getOrFetchAsync(new ExchangeListType());
    }

    public void preFetchTrending()
    {
        // TODO Make it take care of the users's default stock exchange.
        this.securityCompactListCache.get().getOrFetchAsync(new TrendingBasicSecurityListType(1, TrendingFragment.DEFAULT_PER_PAGE));
    }

    public void prefetchesUponLogin(@Nullable UserProfileDTO profile)
    {
        if (profile != null)
        {
            ExchangeCompactDTOList exchangeCompacts = exchangeCompactListCache.get().get(new ExchangeListType());
            if (exchangeCompacts != null)
            {
                ExchangeCompactDTO initialExchange = userBaseDTOUtil.getInitialExchange(profile, exchangeCompacts);
                if (initialExchange != null)
                {
                    securityCompactListCache.get().getOrFetchAsync(
                            new TrendingBasicSecurityListType(
                                    initialExchange.name,
                                    1,
                                    TrendingFragment.DEFAULT_PER_PAGE));
                }
            }
        }

        initialPrefetches();
    }

    public void initialPrefetches()
    {
        preFetchWatchlist();
        //preFetchProviders();

        conveniencePrefetches(); // TODO move them so time after the others
    }
    
    public void preFetchWatchlist()
    {
        userWatchlistPositionCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    public void preFetchProviders()
    {
        this.providerListCache.get().getOrFetchAsync(new ProviderListKey());
    }

    public void conveniencePrefetches()
    {
        preFetchAlerts();
        preFetchTranslationToken();
        preFetchLeaderboardDefs();
        preFetchHomeContent();
    }

    public void preFetchAlerts()
    {
        alertCompactListCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    public void preFetchTranslationToken()
    {
        translationTokenCache.get().getOrFetchAsync(new TranslationTokenKey());
    }

    public void preFetchLeaderboardDefs()
    {
        leaderboardDefListCache.get().getOrFetchAsync(new LeaderboardDefListKey());
    }

    public void preFetchHomeContent()
    {
        homeContentCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
    }
}
