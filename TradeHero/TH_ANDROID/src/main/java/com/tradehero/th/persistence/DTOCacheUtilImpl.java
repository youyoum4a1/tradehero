package com.tradehero.th.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.SystemCache;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
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
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.trending.TrendingStockFragment;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeBasicDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.persistence.achievement.QuestBonusListCacheRx;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCacheRx;
import com.tradehero.th.persistence.level.LevelDefListCacheRx;
import com.tradehero.th.persistence.market.ExchangeCompactListCacheRx;
import com.tradehero.th.persistence.notification.NotificationCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.prefs.IsOnBoardShown;
import com.tradehero.th.persistence.security.SecurityCompactListCacheRx;
import com.tradehero.th.persistence.translation.TranslationTokenCacheRx;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCacheRx;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Actions;

@Singleton public class DTOCacheUtilImpl
    implements DTOCacheUtilRx
{
    protected final CurrentUserId currentUserId;

    //<editor-fold desc="Caches">
    protected final Lazy<AlertCompactListCacheRx> alertCompactListCache;
    protected final Lazy<ExchangeCompactListCacheRx> exchangeCompactListCache;
    protected final Lazy<HomeContentCacheRx> homeContentCache;
    protected final Lazy<LeaderboardDefListCacheRx> leaderboardDefListCache;
    protected final Lazy<LevelDefListCacheRx> levelDefListCache;
    protected final Lazy<NotificationCacheRx> notificationCache;
    protected final Lazy<PortfolioCacheRx> portfolioCache;
    protected final Lazy<PortfolioCompactCacheRx> portfolioCompactCache;
    protected final Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache;
    protected final Lazy<ProviderCacheRx> providerCache;
    protected final Lazy<ProviderListCacheRx> providerListCache;
    protected final Lazy<QuestBonusListCacheRx> questBonusListCacheLazy;
    protected final Lazy<SecurityCompactListCacheRx> securityCompactListCache;
    protected final Lazy<TranslationTokenCacheRx> translationTokenCache;
    protected final Lazy<UserProfileCacheRx> userProfileCache;
    protected final Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache;
    protected final Lazy<UserWatchlistPositionCacheRx> userWatchlistPositionCache;
    protected final Lazy<WatchlistPositionCacheRx> watchlistPositionCache;
    //</editor-fold>

    protected final StringPreference serverEndpointPreference;
    protected final SharedPreferences userSharedPreferences;
    private final BooleanPreference isOnboardShown;
    @NonNull protected final BroadcastUtils broadcastUtils;
    @NonNull protected final Context context;

    @NonNull private List<DTOCacheRx> userCacheRxs;
    @NonNull private List<DTOCacheRx> systemCacheRxs;

    //<editor-fold desc="Constructors">
    @Inject public DTOCacheUtilImpl(
            CurrentUserId currentUserId,
            Lazy<AlertCompactListCacheRx> alertCompactListCache,
            Lazy<ExchangeCompactListCacheRx> exchangeCompactListCache,
            Lazy<HomeContentCacheRx> homeContentCache,
            Lazy<LeaderboardDefListCacheRx> leaderboardDefListCache,
            Lazy<LevelDefListCacheRx> levelDefListCacheLazy,
            Lazy<NotificationCacheRx> notificationCache,
            Lazy<PortfolioCacheRx> portfolioCache,
            Lazy<PortfolioCompactCacheRx> portfolioCompactCache,
            Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            Lazy<ProviderCacheRx> providerCache,
            Lazy<ProviderListCacheRx> providerListCache,
            Lazy<SecurityCompactListCacheRx> securityCompactListCache,
            Lazy<TranslationTokenCacheRx> translationTokenCache,
            Lazy<UserProfileCacheRx> userProfileCache,
            Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache,
            Lazy<UserWatchlistPositionCacheRx> userWatchlistPositionCache,
            Lazy<WatchlistPositionCacheRx> watchlistPositionCache,
            Lazy<QuestBonusListCacheRx> questBonusListCacheLazy,
            @ServerEndpoint StringPreference serverEndpointPreference,
            @ForUser SharedPreferences userSharedPreferences,
            @IsOnBoardShown BooleanPreference isOnboardShown,
            @NonNull BroadcastUtils broadcastUtils,
            @NonNull Context context)
    {
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

        this.serverEndpointPreference = serverEndpointPreference;
        this.userSharedPreferences = userSharedPreferences;
        this.isOnboardShown = isOnboardShown;
        this.broadcastUtils = broadcastUtils;
        this.context = context;
    }
    //</editor-fold>

    @Override public void addCache(@NonNull DTOCacheRx dtoCacheRx)
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

    public void clearAllCaches()
    {
        clearSystemCaches();
        clearUserCaches();
    }

    public void clearSystemCaches()
    {
//        CollectionUtils.apply(systemCacheNews, DTOCacheNew::invalidateAll);
//        CollectionUtils.apply(systemCacheRxs, DTOCacheRx::invalidateAll);

        for(int i = 0, length = systemCacheRxs.size(); i < length; i++)
        {
            systemCacheRxs.get(i).invalidateAll();
        }
    }

    public void clearUserCaches()
    {
//        CollectionUtils.apply(userCacheNews, DTOCacheNew::invalidateAll);
//        CollectionUtils.apply(userCacheRxs, DTOCacheRx::invalidateAll);

        for(int i = 0, length = userCacheRxs.size(); i < length; i++)
        {
            userCacheRxs.get(i).invalidateAll();
        }

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
        exchangeCompactListCache.get().getOne(new ExchangeListType());
    }

    public void preFetchTrending()
    {
        Observable.zip(
                userProfileCache.get().getOne(currentUserId.toUserBaseKey()),
                exchangeCompactListCache.get().getOne(new ExchangeListType()),
                (obs1, obs2) -> Pair.create(obs1.second, obs2.second))
                .first()
                .doOnNext(this::preFetchTrending)
                .subscribe(Actions.empty(), Actions.empty());
    }

    protected void preFetchTrending(@NonNull Pair<? extends UserBaseDTO, ExchangeCompactDTOList> pair)
    {
        if (pair.first != null && pair.second != null)
        {
            preFetchTrending(pair.first, pair.second);
        }
    }

    protected void preFetchTrending(
            @NonNull UserBaseDTO userBaseDTO,
            @NonNull ExchangeCompactDTOList exchangeCompactDTOs)
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

        this.securityCompactListCache.get().getOne(
                filterTypeBasicDTO.getSecurityListType(1, TrendingStockFragment.DEFAULT_PER_PAGE));
    }

    private void preFetchTraderLevels()
    {
        this.levelDefListCache.get().getOne(new LevelDefListId());
    }

    private void preFetchQuestBonus()
    {
        this.questBonusListCacheLazy.get().getOne(new QuestBonusListId());
    }

    public void prefetchesUponLogin(@Nullable UserProfileDTO profile)
    {
        if (profile != null)
        {
            exchangeCompactListCache.get().getOne(new ExchangeListType())
                    .doOnNext(pair -> {
                        Country country = profile.getCountry();
                        if (pair.second != null && country != null)
                        {
                            ExchangeCompactDTO initialExchange = pair.second.findFirstDefaultFor(country);
                            if (initialExchange != null)
                            {
                                securityCompactListCache.get().getOne(
                                        new TrendingBasicSecurityListType(
                                                initialExchange.name,
                                                1,
                                                TrendingStockFragment.DEFAULT_PER_PAGE));
                            }
                        }
                    })
                    .subscribe(Actions.empty(), Actions.empty());
        }

        //initialPrefetches();
    }

    public void initialPrefetches()
    {
        preFetchWatchlist();
        preFetchTranslationToken();

        conveniencePrefetches(); // TODO move them so time after the others
    }

    public void preFetchWatchlist()
    {
        userWatchlistPositionCache.get().getOne(currentUserId.toUserBaseKey());
    }

    public void preFetchProviders()
    {
        this.providerListCache.get().getOne(new ProviderListKey());
    }

    public void conveniencePrefetches()
    {
        preFetchAlerts();
        preFetchTranslationToken();
        preFetchLeaderboardDefs();
        //preFetchHomeContent();
    }

    public void preFetchAlerts()
    {
        alertCompactListCache.get().getOne(currentUserId.toUserBaseKey());
    }

    public void preFetchTranslationToken()
    {
        translationTokenCache.get().getOne(new TranslationTokenKey());
    }

    public void preFetchLeaderboardDefs()
    {
        leaderboardDefListCache.get().getOne(new LeaderboardDefListKey(1));
    }

    public void preFetchHomeContent()
    {
        homeContentCache.get().getOne(currentUserId.toUserBaseKey());
    }
}
