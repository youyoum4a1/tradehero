package com.tradehero.th.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.SystemCache;
import com.tradehero.common.persistence.UserCache;
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
import com.tradehero.th.persistence.achievement.QuestBonusListCacheRx;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import com.tradehero.th.persistence.level.LevelDefListCacheRx;
import com.tradehero.th.persistence.market.ExchangeCompactListCache;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.prefs.IsOnBoardShown;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
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

@Singleton public class DTOCacheUtilImpl
    implements DTOCacheUtilNew, DTOCacheUtilRx
{
    protected final CurrentUserId currentUserId;

    //<editor-fold desc="Caches">
    protected final Lazy<AlertCompactListCache> alertCompactListCache;
    protected final Lazy<ExchangeCompactListCache> exchangeCompactListCache;
    protected final Lazy<HomeContentCache> homeContentCache;
    protected final Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    protected final Lazy<LevelDefListCacheRx> levelDefListCache;
    protected final Lazy<NotificationCache> notificationCache;
    protected final Lazy<PortfolioCache> portfolioCache;
    protected final Lazy<PortfolioCompactCache> portfolioCompactCache;
    protected final Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    protected final Lazy<ProviderCache> providerCache;
    protected final Lazy<ProviderListCache> providerListCache;
    protected final Lazy<QuestBonusListCacheRx> questBonusListCacheLazy;
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

    @NotNull private List<DTOCacheNew> userCacheNews;
    @NotNull private List<DTOCacheNew> systemCacheNews;
    @NotNull private List<DTOCacheRx> userCacheRxs;
    @NotNull private List<DTOCacheRx> systemCacheRxs;

    //<editor-fold desc="Constructors">
    @Inject public DTOCacheUtilImpl(
            CurrentUserId currentUserId,
            Lazy<AlertCompactListCache> alertCompactListCache,
            Lazy<ExchangeCompactListCache> exchangeCompactListCache,
            Lazy<HomeContentCache> homeContentCache,
            Lazy<LeaderboardDefListCache> leaderboardDefListCache,
            Lazy<LevelDefListCacheRx> levelDefListCacheLazy,
            Lazy<NotificationCache> notificationCache,
            Lazy<PortfolioCache> portfolioCache,
            Lazy<PortfolioCompactCache> portfolioCompactCache,
            Lazy<PortfolioCompactListCache> portfolioCompactListCache,
            Lazy<ProviderCache> providerCache,
            Lazy<ProviderListCache> providerListCache,
            Lazy<SecurityCompactListCache> securityCompactListCache,
            Lazy<TranslationTokenCache> translationTokenCache,
            Lazy<UserProfileCache> userProfileCache,
            Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache,
            Lazy<UserWatchlistPositionCache> userWatchlistPositionCache,
            Lazy<WatchlistPositionCache> watchlistPositionCache,
            Lazy<WarrantSpecificKnowledgeFactory> warrantSpecificKnowledgeFactoryLazy,
            Lazy<QuestBonusListCacheRx> questBonusListCacheLazy,
            @ServerEndpoint StringPreference serverEndpointPreference,
            @ForUser SharedPreferences userSharedPreferences,
            @IsOnBoardShown BooleanPreference isOnboardShown,
            @NotNull BroadcastUtils broadcastUtils,
            @NotNull UserBaseDTOUtil userBaseDTOUtil,
            @NotNull Context context)
    {
        this.userCacheNews = new ArrayList<>();
        this.systemCacheNews = new ArrayList<>();
        this.userCacheRxs = new ArrayList<>();
        this.systemCacheRxs = new ArrayList<>();

        this.currentUserId = currentUserId;

        this.alertCompactListCache = alertCompactListCache;
        this.exchangeCompactListCache = exchangeCompactListCache; // Not added to list
        this.homeContentCache = homeContentCache;
        this.leaderboardDefListCache = leaderboardDefListCache;
        this.levelDefListCache = levelDefListCacheLazy;
        this.notificationCache = notificationCache;
        this.portfolioCache = portfolioCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.providerCache = providerCache;
        this.providerListCache = providerListCache;
        this.questBonusListCacheLazy = questBonusListCacheLazy;
        this.securityCompactListCache = securityCompactListCache;
        this.translationTokenCache = translationTokenCache;
        this.userProfileCache = userProfileCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.userWatchlistPositionCache = userWatchlistPositionCache;
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

    @Override public void addCache(@NotNull DTOCacheNew dtoCacheNew)
    {
        if (dtoCacheNew.getClass().isAnnotationPresent(UserCache.class))
        {
            userCacheNews.add(dtoCacheNew);
        }
        else if (dtoCacheNew.getClass().isAnnotationPresent(SystemCache.class))
        {
            systemCacheNews.add(dtoCacheNew);
        }
        else
        {
            throw new IllegalStateException(dtoCacheNew.getClass() + " needs to be either UserCache or SystemCache");
        }
    }

    @Override public void addCache(@NotNull DTOCacheRx dtoCacheRx)
    {
        if (dtoCacheRx.getClass().isAnnotationPresent(UserCache.class))
        {
            userCacheRxs.add(dtoCacheRx);
        }
        else if (dtoCacheRx.getClass().isAnnotationPresent(SystemCache.class))
        {
            systemCacheRxs.add(dtoCacheRx);
        }
        else
        {
            throw new IllegalStateException(dtoCacheRx.getClass() + " needs to be either UserCache or SystemCache");
        }
    }

    @Override public void clearAllCaches()
    {
        clearSystemCaches();
        clearUserCaches();
    }

    @Override public void clearSystemCaches()
    {
        CollectionUtils.apply(systemCacheNews, DTOCacheNew::invalidateAll);
        CollectionUtils.apply(systemCacheRxs, DTOCacheRx::invalidateAll);
    }

    @Override public void clearUserCaches()
    {
        CollectionUtils.apply(userCacheNews, DTOCacheNew::invalidateAll);
        CollectionUtils.apply(userCacheRxs, DTOCacheRx::invalidateAll);

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
        this.levelDefListCache.get().get(new LevelDefListId());
    }

    private void preFetchQuestBonus()
    {
        this.questBonusListCacheLazy.get().get(new QuestBonusListId());
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
