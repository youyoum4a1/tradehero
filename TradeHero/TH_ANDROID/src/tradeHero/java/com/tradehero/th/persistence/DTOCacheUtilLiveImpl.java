package com.androidth.general.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.androidth.general.common.annotation.ForUser;
import com.androidth.general.common.persistence.prefs.BooleanPreference;
import com.androidth.general.common.persistence.prefs.StringPreference;
import com.androidth.general.api.kyc.KYCFormOptionsDTO;
import com.androidth.general.api.kyc.KYCFormOptionsId;
import com.androidth.general.api.kyc.LiveAvailabilityDTO;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.network.ServerEndpoint;
import com.androidth.general.network.service.LiveServiceWrapper;
import com.androidth.general.persistence.achievement.QuestBonusListCacheRx;
import com.androidth.general.persistence.alert.AlertCompactListCacheRx;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.competition.ProviderListCacheRx;
import com.androidth.general.persistence.kyc.KYCFormOptionsCache;
import com.androidth.general.persistence.leaderboard.LeaderboardDefListCacheRx;
import com.androidth.general.persistence.level.LevelDefListCacheRx;
import com.androidth.general.persistence.market.ExchangeCompactListCacheRx;
import com.androidth.general.persistence.notification.NotificationCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCompactCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCompactListCacheRx;
import com.androidth.general.persistence.prefs.IsOnBoardShown;
import com.androidth.general.persistence.prefs.LiveAvailability;
import com.androidth.general.persistence.security.SecurityCompactListCacheRx;
import com.androidth.general.persistence.translation.TranslationTokenCacheRx;
import com.androidth.general.persistence.user.UserMessagingRelationshipCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.androidth.general.persistence.watchlist.WatchlistPositionCacheRx;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.utils.broadcast.BroadcastUtils;
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
