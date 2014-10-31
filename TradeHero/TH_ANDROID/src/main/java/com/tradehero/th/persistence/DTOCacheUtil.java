package com.tradehero.th.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.th.api.achievement.key.QuestBonusListId;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeBasicDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.models.security.WarrantSpecificKnowledgeFactory;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.persistence.achievement.AchievementCategoryCache;
import com.tradehero.th.persistence.achievement.AchievementCategoryListCache;
import com.tradehero.th.persistence.achievement.QuestBonusListCache;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.competition.CompetitionCache;
import com.tradehero.th.persistence.competition.CompetitionListCache;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCacheRx;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionIdCache;
import com.tradehero.th.persistence.level.LevelDefListCache;
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
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import com.tradehero.th.persistence.prefs.IsOnBoardShown;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.persistence.security.SecurityCompactListCacheRx;
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
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class DTOCacheUtil
{
    protected final CurrentUserId currentUserId;

    //<editor-fold desc="Caches">
    protected final Lazy<AlertCompactListCache> alertCompactListCache;
    protected final Lazy<ExchangeCompactListCache> exchangeCompactListCache;
    protected final Lazy<HomeContentCache> homeContentCache;
    protected final Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    protected final Lazy<LevelDefListCache> levelDefListCache;
    protected final Lazy<NotificationCache> notificationCache;
    protected final Lazy<PortfolioCache> portfolioCache;
    protected final Lazy<PortfolioCompactCache> portfolioCompactCache;
    protected final Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    protected final Lazy<ProviderCache> providerCache;
    protected final Lazy<ProviderListCache> providerListCache;
    protected final Lazy<QuestBonusListCache> questBonusListCacheLazy;
    protected final Lazy<SecurityCompactListCache> securityCompactListCache;
    protected final Lazy<TranslationTokenCache> translationTokenCache;
    protected final Lazy<UserProfileCache> userProfileCache;
    protected final Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache;
    protected final Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;
    protected final Lazy<WatchlistPositionCache> watchlistPositionCache;
    //</editor-fold>

    protected final Lazy<WarrantSpecificKnowledgeFactory> warrantSpecificKnowledgeFactoryLazy;
    protected final StringPreference serverEndpointPreference;
    protected final SharedPreferences userSharedPreferences;
    private final BooleanPreference isOnboardShown;
    @NotNull protected final BroadcastUtils broadcastUtils;
    @NotNull protected final UserBaseDTOUtil userBaseDTOUtil;
    @NotNull protected final Context context;

    @NotNull List<Lazy<? extends DTOCacheNew>> userCacheNews;
    @NotNull List<Lazy<? extends DTOCacheRx>> userCacheRxs;

    //<editor-fold desc="Constructors">
    @Inject public DTOCacheUtil(
            CurrentUserId currentUserId,
            Lazy<AchievementCategoryCache> achievementCategoryCacheLazy,
            Lazy<AchievementCategoryListCache> achievementCategoryListCacheLazy,
            Lazy<AlertCache> alertCache,
            Lazy<AlertCompactCache> alertCompactCache,
            Lazy<AlertCompactListCache> alertCompactListCache,
            Lazy<CompetitionListCache> competitionListCache,
            Lazy<CompetitionCache> competitionCache,
            Lazy<DiscussionCache> discussionCache,
            Lazy<DiscussionListCacheNew> discussionListCache,
            Lazy<ExchangeCompactListCache> exchangeCompactListCache,
            Lazy<FollowerSummaryCache> followerSummaryCache,
            Lazy<GetPositionsCache> getPositionsCache,
            Lazy<HomeContentCache> homeContentCache,
            Lazy<LeaderboardDefCache> leaderboardDefCache,
            Lazy<LeaderboardDefListCache> leaderboardDefListCache,
            Lazy<LeaderboardPositionIdCache> leaderboardPositionIdCache,
            Lazy<LeaderboardFriendsCacheRx> leaderboardFriendsCacheRx,
            Lazy<MessageHeaderCache> messageHeaderCache,
            Lazy<LevelDefListCache> levelDefListCacheLazy,
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
            Lazy<SecurityPositionDetailCacheRx> securityPositionDetailCache,
            Lazy<SecurityCompactListCache> securityCompactListCache,
            Lazy<SecurityCompactListCacheRx> securityCompactListCacheRx,
            Lazy<SystemStatusCache> systemStatusCache,
            Lazy<TradeCache> tradeCache,
            Lazy<TradeListCache> tradeListCache,
            Lazy<TranslationTokenCache> translationTokenCache,
            Lazy<UserAchievementCache> userAchievementCache,
            Lazy<UserProfileCache> userProfileCache,
            Lazy<UserFollowerCache> userFollowerCache,
            Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache,
            Lazy<UserWatchlistPositionCache> userWatchlistPositionCache,
            Lazy<WatchlistPositionCache> watchlistPositionCache,
            Lazy<WarrantSpecificKnowledgeFactory> warrantSpecificKnowledgeFactoryLazy,
            Lazy<QuestBonusListCache> questBonusListCacheLazy,
            @ServerEndpoint StringPreference serverEndpointPreference,
            @ForUser SharedPreferences userSharedPreferences,
            @IsOnBoardShown BooleanPreference isOnboardShown,
            @NotNull BroadcastUtils broadcastUtils,
            @NotNull UserBaseDTOUtil userBaseDTOUtil,
            @NotNull Context context)
    {
        this.currentUserId = currentUserId;

        this.userCacheNews = new ArrayList<>();
        this.userCacheRxs = new ArrayList<>();

        this.userCacheNews.add(achievementCategoryCacheLazy);
        this.userCacheNews.add(achievementCategoryListCacheLazy);
        this.userCacheNews.add(alertCache);
        this.userCacheNews.add(alertCompactCache);
        this.alertCompactListCache = alertCompactListCache;
        this.userCacheNews.add(alertCompactListCache);
        this.userCacheNews.add(competitionListCache);
        this.userCacheNews.add(competitionCache);
        this.userCacheNews.add(discussionCache);
        this.userCacheNews.add(discussionListCache);
        this.exchangeCompactListCache = exchangeCompactListCache; // Not added to list
        this.userCacheNews.add(followerSummaryCache);
        this.userCacheNews.add(getPositionsCache);
        this.homeContentCache = homeContentCache;
        this.userCacheNews.add(homeContentCache);
        this.userCacheNews.add(leaderboardDefCache);
        this.leaderboardDefListCache = leaderboardDefListCache;
        this.userCacheNews.add(leaderboardDefListCache);
        this.userCacheNews.add(leaderboardPositionIdCache);
        this.userCacheRxs.add(leaderboardFriendsCacheRx);
        this.levelDefListCache = levelDefListCacheLazy;
        this.userCacheNews.add(levelDefListCacheLazy);
        this.userCacheNews.add(messageHeaderCache);
        this.userCacheNews.add(messageListCache);
        this.notificationCache = notificationCache;
        this.userCacheNews.add(notificationCache);
        this.userCacheNews.add(notificationListCache);
        this.portfolioCache = portfolioCache;
        this.userCacheNews.add(portfolioCache);
        this.portfolioCompactCache = portfolioCompactCache;
        this.userCacheNews.add(portfolioCompactCache);
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.userCacheNews.add(portfolioCompactListCache);
        this.userCacheNews.add(positionCache);
        this.userCacheNews.add(positionCompactCache);
        this.userCacheNews.add(positionCompactIdCache);
        this.userCacheNews.add(productPurchaseCache);
        this.providerCache = providerCache;
        this.userCacheNews.add(providerCache);
        this.providerListCache = providerListCache;
        this.userCacheNews.add(providerListCache);
        this.questBonusListCacheLazy = questBonusListCacheLazy;
        this.userCacheNews.add(questBonusListCacheLazy);
        this.userCacheRxs.add(securityPositionDetailCache);
        this.securityCompactListCache = securityCompactListCache;
        this.userCacheNews.add(securityCompactListCache);
        this.userCacheRxs.add(securityCompactListCacheRx);
        this.userCacheRxs.add(systemStatusCache);
        this.userCacheNews.add(tradeCache);
        this.userCacheNews.add(tradeListCache);
        this.translationTokenCache = translationTokenCache;
        this.userCacheNews.add(translationTokenCache);
        this.userCacheNews.add(userAchievementCache);
        this.userProfileCache = userProfileCache;
        this.userCacheNews.add(userProfileCache);
        this.userCacheNews.add(userFollowerCache);
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.userCacheNews.add(userMessagingRelationshipCache);
        this.userWatchlistPositionCache = userWatchlistPositionCache;
        this.userCacheNews.add(userWatchlistPositionCache);
        this.userCacheNews.add(watchlistPositionCache);
        this.watchlistPositionCache = watchlistPositionCache;

        this.warrantSpecificKnowledgeFactoryLazy = warrantSpecificKnowledgeFactoryLazy;
        this.serverEndpointPreference = serverEndpointPreference;
        this.userSharedPreferences = userSharedPreferences;
        this.isOnboardShown = isOnboardShown;
        this.broadcastUtils = broadcastUtils;
        this.userBaseDTOUtil = userBaseDTOUtil;
        this.context = context;
    }
    //</editor-fold>

    public void clearUserRelatedCaches()
    {
        CollectionUtils.apply(userCacheNews, dtoCacheNew -> dtoCacheNew.get().invalidateAll());
        CollectionUtils.apply(userCacheRxs, dtoCacheRx -> dtoCacheRx.get().invalidateAll());

        warrantSpecificKnowledgeFactoryLazy.get().clear();
        serverEndpointPreference.delete();
        isOnboardShown.delete();
        userSharedPreferences.edit().clear().apply();

        broadcastUtils.clear();
    }

    public void anonymousPrefetches()
    {
        preFetchExchanges();
        preFetchProviders();
        preFetchTraderLevels();
        preFetchQuestBonus();
    }

    public void preFetchExchanges()
    {
        exchangeCompactListCache.get().getOrFetchAsync(new ExchangeListType());
    }

    public void preFetchTrending()
    {
        UserProfileDTO currentUserProfile = userProfileCache.get().get(currentUserId.toUserBaseKey());
        ExchangeCompactDTOList exchangeCompactDTOs = exchangeCompactListCache.get().get(new ExchangeListType());
        if (currentUserProfile != null && exchangeCompactDTOs != null)
        {
            preFetchTrending(currentUserProfile, exchangeCompactDTOs);
        }
    }

    protected void preFetchTrending(
            @NotNull UserBaseDTO userBaseDTO,
            @NotNull ExchangeCompactDTOList exchangeCompactDTOs)
    {
        Country country = userBaseDTO.getCountry();
        ExchangeCompactDTO initialExchange = null;
        if (country != null)
        {
            initialExchange = exchangeCompactDTOs.findFirstDefaultFor(country);
        }
        ExchangeCompactSpinnerDTO initialExchangeSpinner;
        if (initialExchange == null)
        {
            initialExchangeSpinner = new ExchangeCompactSpinnerDTO(
                    context.getResources());
        }
        else
        {
            initialExchangeSpinner = new ExchangeCompactSpinnerDTO(
                    context.getResources(),
                    initialExchange);
        }
        TrendingFilterTypeBasicDTO filterTypeBasicDTO = new TrendingFilterTypeBasicDTO(initialExchangeSpinner);

        this.securityCompactListCache.get().getOrFetchAsync(
                filterTypeBasicDTO.getSecurityListType(1, TrendingFragment.DEFAULT_PER_PAGE));
    }

    private void preFetchTraderLevels()
    {
        this.levelDefListCache.get().getOrFetchAsync(new LevelDefListId(), true); //Should it be forceUpdate?
    }

    private void preFetchQuestBonus()
    {
        this.questBonusListCacheLazy.get().getOrFetchAsync(new QuestBonusListId(), true);
    }

    public void prefetchesUponLogin(@Nullable UserProfileDTO profile)
    {
        if (profile != null)
        {
            ExchangeCompactDTOList exchangeCompacts = exchangeCompactListCache.get().get(new ExchangeListType());
            Country country = profile.getCountry();
            if (exchangeCompacts != null && country != null)
            {
                ExchangeCompactDTO initialExchange = exchangeCompacts.findFirstDefaultFor(country);
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

        //initialPrefetches();
    }

    public void initialPrefetches()
    {
        preFetchWatchlist();

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
