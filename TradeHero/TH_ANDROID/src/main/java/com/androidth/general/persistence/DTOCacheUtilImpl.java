package com.androidth.general.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.androidth.general.common.annotation.ForUser;
import com.androidth.general.common.persistence.DTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.SystemCache;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.common.persistence.prefs.BooleanPreference;
import com.androidth.general.common.persistence.prefs.StringPreference;
import com.androidth.general.api.achievement.key.QuestBonusListId;
import com.androidth.general.api.competition.key.ProviderListKey;
import com.androidth.general.api.leaderboard.key.LeaderboardDefListKey;
import com.androidth.general.api.level.key.LevelDefListId;
import com.androidth.general.api.market.Country;
import com.androidth.general.api.market.ExchangeCompactDTO;
import com.androidth.general.api.market.ExchangeCompactDTOList;
import com.androidth.general.api.market.ExchangeListType;
import com.androidth.general.api.security.key.TrendingBasicSecurityListType;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.fragments.trending.TrendingStockFragment;
import com.androidth.general.fragments.trending.filter.TrendingFilterTypeBasicDTO;
import com.androidth.general.models.market.ExchangeCompactSpinnerDTO;
import com.androidth.general.network.ServerEndpoint;
import com.androidth.general.persistence.achievement.QuestBonusListCacheRx;
import com.androidth.general.persistence.alert.AlertCompactListCacheRx;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.competition.ProviderListCacheRx;
import com.androidth.general.persistence.leaderboard.LeaderboardDefListCacheRx;
import com.androidth.general.persistence.level.LevelDefListCacheRx;
import com.androidth.general.persistence.market.ExchangeCompactListCacheRx;
import com.androidth.general.persistence.notification.NotificationCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCompactCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCompactListCacheRx;
import com.androidth.general.persistence.prefs.IsOnBoardShown;
import com.androidth.general.persistence.security.SecurityCompactListCacheRx;
import com.androidth.general.persistence.translation.TranslationTokenCacheRx;
import com.androidth.general.persistence.translation.TranslationTokenKey;
import com.androidth.general.persistence.user.UserMessagingRelationshipCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.androidth.general.persistence.watchlist.WatchlistPositionCacheRx;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.utils.broadcast.BroadcastUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DTOCacheUtilImpl implements DTOCacheUtilRx
{
    protected final CurrentUserId currentUserId;

    //<editor-fold desc="Caches">
    protected final Lazy<AlertCompactListCacheRx> alertCompactListCache;
    protected final Lazy<ExchangeCompactListCacheRx> exchangeCompactListCache;
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
            @IsOnBoardShown BooleanPreference isOnBoardShown,
            @NonNull BroadcastUtils broadcastUtils,
            @NonNull Context context)
    {
        this.userCacheRxs = new ArrayList<>();
        this.systemCacheRxs = new ArrayList<>();

        this.currentUserId = currentUserId;

        this.alertCompactListCache = alertCompactListCache;
        this.exchangeCompactListCache = exchangeCompactListCache; // Not added to list
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
        this.isOnboardShown = isOnBoardShown;
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

    @Override public void clearSystemCaches()
    {
//        CollectionUtils.apply(systemCacheNews, DTOCacheNew::invalidateAll);
//        CollectionUtils.apply(systemCacheRxs, DTOCacheRx::invalidateAll);

        for (int i = 0, length = systemCacheRxs.size(); i < length; i++)
        {
            systemCacheRxs.get(i).invalidateAll();
        }
    }

    @Override public void clearUserCaches()
    {
//        CollectionUtils.apply(userCacheNews, DTOCacheNew::invalidateAll);
//        CollectionUtils.apply(userCacheRxs, DTOCacheRx::invalidateAll);

        for (int i = 0, length = userCacheRxs.size(); i < length; i++)
        {
            try
            {
                userCacheRxs.get(i).invalidateAll();
            } catch (Throwable e)
            {
                Timber.e(e, "Failed to clear cache " + userCacheRxs.get(i).getClass().getSimpleName());
            }
        }

        serverEndpointPreference.delete();
        isOnboardShown.delete();
        userSharedPreferences.edit().clear().apply();

        broadcastUtils.clear();
    }

    @Override public void anonymousPrefetches()
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
                new Func2<Pair<UserBaseKey, UserProfileDTO>, Pair<ExchangeListType, ExchangeCompactDTOList>, Pair<UserProfileDTO, ExchangeCompactDTOList>>()
                {
                    @Override public Pair<UserProfileDTO, ExchangeCompactDTOList> call(Pair<UserBaseKey, UserProfileDTO> obs1,
                            Pair<ExchangeListType, ExchangeCompactDTOList> obs2)
                    {
                        return Pair.create(obs1.second, obs2.second);
                    }
                })
                .first()
                .subscribe(
                        new Action1<Pair<UserProfileDTO, ExchangeCompactDTOList>>()
                        {
                            @Override public void call(Pair<UserProfileDTO, ExchangeCompactDTOList> t1)
                            {
                                DTOCacheUtilImpl.this.preFetchTrending(t1);
                            }
                        },
                        new EmptyAction1<Throwable>());
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

    @Override public void prefetchesUponLogin(@Nullable final UserProfileDTO profile)
    {
        if (profile != null)
        {
            exchangeCompactListCache.get().getOne(new ExchangeListType())
                    .doOnNext(new Action1<Pair<ExchangeListType, ExchangeCompactDTOList>>()
                    {
                        @Override public void call(Pair<ExchangeListType, ExchangeCompactDTOList> pair)
                        {
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
                        }
                    })
                    .subscribe(
                            new EmptyAction1<Pair<ExchangeListType, ExchangeCompactDTOList>>(),
                            new EmptyAction1<Throwable>());
        }

        //initialPrefetches();
    }

    @Override public void initialPrefetches()
    {
        preFetchWatchlist();
        preFetchTranslationToken();

        // TODO move them so time after the others
        preFetchAlerts();
        preFetchLeaderboardDefs();
    }

    public void preFetchWatchlist()
    {
        userWatchlistPositionCache.get().getOne(currentUserId.toUserBaseKey());
    }

    public void preFetchProviders()
    {
        this.providerListCache.get()
                .getOne(new ProviderListKey());
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
}
