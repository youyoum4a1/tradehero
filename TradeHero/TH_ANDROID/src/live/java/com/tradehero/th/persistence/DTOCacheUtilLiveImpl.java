package com.ayondo.academy.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.ayondo.academy.api.kyc.KYCFormOptionsDTO;
import com.ayondo.academy.api.kyc.KYCFormOptionsId;
import com.ayondo.academy.api.kyc.LiveAvailabilityDTO;
import com.ayondo.academy.api.live.LiveBrokerSituationDTO;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.network.ServerEndpoint;
import com.ayondo.academy.network.service.LiveServiceWrapper;
import com.ayondo.academy.persistence.achievement.QuestBonusListCacheRx;
import com.ayondo.academy.persistence.alert.AlertCompactListCacheRx;
import com.ayondo.academy.persistence.competition.ProviderCacheRx;
import com.ayondo.academy.persistence.competition.ProviderListCacheRx;
import com.ayondo.academy.persistence.kyc.KYCFormOptionsCache;
import com.ayondo.academy.persistence.leaderboard.LeaderboardDefListCacheRx;
import com.ayondo.academy.persistence.level.LevelDefListCacheRx;
import com.ayondo.academy.persistence.market.ExchangeCompactListCacheRx;
import com.ayondo.academy.persistence.notification.NotificationCacheRx;
import com.ayondo.academy.persistence.portfolio.PortfolioCacheRx;
import com.ayondo.academy.persistence.portfolio.PortfolioCompactCacheRx;
import com.ayondo.academy.persistence.portfolio.PortfolioCompactListCacheRx;
import com.ayondo.academy.persistence.prefs.IsOnBoardShown;
import com.ayondo.academy.persistence.prefs.LiveAvailability;
import com.ayondo.academy.persistence.security.SecurityCompactListCacheRx;
import com.ayondo.academy.persistence.translation.TranslationTokenCacheRx;
import com.ayondo.academy.persistence.user.UserMessagingRelationshipCacheRx;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import com.ayondo.academy.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.ayondo.academy.persistence.watchlist.WatchlistPositionCacheRx;
import com.ayondo.academy.rx.TimberOnErrorAction1;
import com.ayondo.academy.utils.broadcast.BroadcastUtils;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class DTOCacheUtilLiveImpl extends DTOCacheUtilImpl
{
    //<editor-fold desc="Caches">
    protected final Lazy<LiveServiceWrapper> liveServiceWrapper;
    protected final Lazy<KYCFormOptionsCache> kycFormOptionsCache;
    @NonNull private final Lazy<BooleanPreference> liveAvailabilityPreference;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    @Inject public DTOCacheUtilLiveImpl(
            @NonNull CurrentUserId currentUserId,
            @NonNull Lazy<AlertCompactListCacheRx> alertCompactListCache,
            @NonNull Lazy<ExchangeCompactListCacheRx> exchangeCompactListCache,
            @NonNull Lazy<LeaderboardDefListCacheRx> leaderboardDefListCache,
            @NonNull Lazy<LevelDefListCacheRx> levelDefListCacheLazy,
            @NonNull Lazy<NotificationCacheRx> notificationCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull Lazy<PortfolioCompactCacheRx> portfolioCompactCache,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<ProviderCacheRx> providerCache,
            @NonNull Lazy<ProviderListCacheRx> providerListCache,
            @NonNull Lazy<SecurityCompactListCacheRx> securityCompactListCache,
            @NonNull Lazy<TranslationTokenCacheRx> translationTokenCache,
            @NonNull Lazy<UserProfileCacheRx> userProfileCache,
            @NonNull Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache,
            @NonNull Lazy<UserWatchlistPositionCacheRx> userWatchlistPositionCache,
            @NonNull Lazy<WatchlistPositionCacheRx> watchlistPositionCache,
            @NonNull Lazy<QuestBonusListCacheRx> questBonusListCacheLazy,
            @ServerEndpoint @NonNull StringPreference serverEndpointPreference,
            @ForUser @NonNull SharedPreferences userSharedPreferences,
            @IsOnBoardShown @NonNull BooleanPreference isOnBoardShown,
            @NonNull BroadcastUtils broadcastUtils,
            @NonNull Context context,
            @NonNull Lazy<LiveServiceWrapper> liveServiceWrapper,
            @NonNull Lazy<KYCFormOptionsCache> kycFormOptionsCache,
            @NonNull @LiveAvailability Lazy<BooleanPreference> liveAvailabilityPreference)
    {
        super(currentUserId,
                alertCompactListCache,
                exchangeCompactListCache,
                leaderboardDefListCache,
                levelDefListCacheLazy,
                notificationCache,
                portfolioCache,
                portfolioCompactCache,
                portfolioCompactListCache,
                providerCache,
                providerListCache,
                securityCompactListCache,
                translationTokenCache,
                userProfileCache,
                userMessagingRelationshipCache,
                userWatchlistPositionCache,
                watchlistPositionCache,
                questBonusListCacheLazy,
                serverEndpointPreference,
                userSharedPreferences,
                isOnBoardShown,
                broadcastUtils,
                context);
        this.liveServiceWrapper = liveServiceWrapper;
        this.kycFormOptionsCache = kycFormOptionsCache;
        this.liveAvailabilityPreference = liveAvailabilityPreference;
    }
    //</editor-fold>

    @Override public void prefetchesUponLogin(@Nullable UserProfileDTO profile)
    {
        super.prefetchesUponLogin(profile);
        prefetchLiveBrokerSituation();
        prefetchLiveAvailability();
    }

    private void prefetchLiveAvailability()
    {
        liveServiceWrapper.get().getAvailability()
                .subscribe(new Action1<LiveAvailabilityDTO>()
                {
                    @Override public void call(LiveAvailabilityDTO liveAvailabilityDTO)
                    {
                        liveAvailabilityPreference.get().set(liveAvailabilityDTO.isAvailable());
                    }
                }, new TimberOnErrorAction1("Error on fetching live availability"));
    }

    public void prefetchLiveBrokerSituation()
    {
        liveServiceWrapper.get().getBrokerSituation()
                .flatMap(new Func1<LiveBrokerSituationDTO, Observable<Pair<KYCFormOptionsId, KYCFormOptionsDTO>>>()
                {
                    @Override
                    public Observable<Pair<KYCFormOptionsId, KYCFormOptionsDTO>> call(LiveBrokerSituationDTO situationDTO)
                    {
                        return kycFormOptionsCache.get().getOne(new KYCFormOptionsId(situationDTO.broker.id));
                    }
                })
                .subscribe(
                        new Action1<Pair<KYCFormOptionsId, KYCFormOptionsDTO>>()
                        {
                            @Override public void call(Pair<KYCFormOptionsId, KYCFormOptionsDTO> kycFormOptionsIdKYCFormOptionsDTOPair)
                            {
                                // Nothing to do
                            }
                        },
                        new TimberOnErrorAction1("Failed to prefetch live broker situation"));
    }
}
