package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.compact.WarrantDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.portfolio.DTOProcessorPortfolioListReceived;
import com.tradehero.th.models.portfolio.DTOProcessorPortfolioReceived;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func3;

@Singleton public class PortfolioServiceWrapper
{
    @NonNull private final PortfolioServiceRx portfolioServiceRx;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache;
    @NonNull private final Lazy<PortfolioCompactCacheRx> portfolioCompactCache;
    @NonNull private final Lazy<PortfolioCacheRx> portfolioCache;
    @NonNull private final Lazy<SecurityPositionDetailCacheRx> securityPositionDetailCache;
    @NonNull private final Lazy<HomeContentCacheRx> homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioServiceWrapper(
            @NonNull PortfolioServiceRx portfolioServiceRx,
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<PortfolioCompactCacheRx> portfolioCompactCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull Lazy<SecurityPositionDetailCacheRx> securityPositionDetailCache,
            @NonNull Lazy<HomeContentCacheRx> homeContentCache)
    {
        super();
        this.portfolioServiceRx = portfolioServiceRx;
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
        this.securityPositionDetailCache = securityPositionDetailCache;
        this.homeContentCache = homeContentCache;
    }
    //</editor-fold>

    //<editor-fold desc="DTO Processors">
    protected DTOProcessorPortfolioReceived<PortfolioCompactDTO> createPortfolioCompactReceivedProcessor(@NonNull UserBaseKey userBaseKey)
    {
        return new DTOProcessorPortfolioReceived<>(userBaseKey);
    }
    //</editor-fold>

    //<editor-fold desc="Get User Portfolio List">
    protected DTOProcessorPortfolioListReceived<PortfolioCompactDTOList> createPortfolioCompactListReceivedProcessor(@NonNull UserBaseKey userBaseKey)
    {
        return new DTOProcessorPortfolioListReceived<>(userBaseKey);
    }

    @NonNull public Observable<PortfolioCompactDTOList> getPortfoliosRx(
            @NonNull UserBaseKey userBaseKey,
            @Nullable Boolean includeWatchList)
    {
        return portfolioServiceRx.getPortfolios(userBaseKey.key, includeWatchList)
                .map(createPortfolioCompactListReceivedProcessor(userBaseKey));
    }

    @NonNull public Observable<OwnedPortfolioIdList> getApplicablePortfoliosRx(
            @NonNull SecurityId securityId)
    {
        return portfolioServiceRx.getApplicablePortfolios(
                securityId.getExchange(),
                securityId.getPathSafeSymbol())
                .onErrorResumeNext(
                        Observable.combineLatest(
                                userProfileCache.get(currentUserId.toUserBaseKey())
                                        .map(new Func1<Pair<UserBaseKey, UserProfileDTO>, UserProfileDTO>()
                                        {
                                            @Override public UserProfileDTO call(Pair<UserBaseKey, UserProfileDTO> pair)
                                            {
                                                return pair.second;
                                            }
                                        }),
                                portfolioCompactListCache.get().get(currentUserId.toUserBaseKey())
                                        .map(new Func1<Pair<UserBaseKey, PortfolioCompactDTOList>, PortfolioCompactDTOList>()
                                        {
                                            @Override public PortfolioCompactDTOList call(Pair<UserBaseKey, PortfolioCompactDTOList> pair)
                                            {
                                                return pair.second;
                                            }
                                        }),
                                securityPositionDetailCache.get().get(securityId)
                                        .map(new Func1<Pair<SecurityId, SecurityPositionDetailDTO>, SecurityPositionDetailDTO>()
                                        {
                                            @Override public SecurityPositionDetailDTO call(
                                                    Pair<SecurityId, SecurityPositionDetailDTO> pair)
                                            {
                                                return pair.second;
                                            }
                                        }),
                                new Func3<UserProfileDTO, PortfolioCompactDTOList, SecurityPositionDetailDTO, Triplet>()
                                {
                                    @Override public Triplet call(UserProfileDTO dto, PortfolioCompactDTOList dto2, SecurityPositionDetailDTO dto3)
                                    {
                                        return new Triplet(dto, dto2, dto3);
                                    }
                                })
                                .take(1)
                                .map(triplet -> {
                                    OwnedPortfolioIdList portfolioIds = new OwnedPortfolioIdList();
                                    if (triplet.securityPositionDetail.providers != null)
                                    {
                                        for (ProviderDTO provider : triplet.securityPositionDetail.providers)
                                        {
                                            if (provider != null && provider.associatedPortfolio != null)
                                            {
                                                portfolioIds.add(provider.associatedPortfolio.getOwnedPortfolioId());
                                            }
                                        }
                                    }
                                    SecurityCompactDTO security = triplet.securityPositionDetail.security;
                                    if (security instanceof FxSecurityCompactDTO)
                                    {
                                        if (triplet.userProfile.fxPortfolio != null)
                                        {
                                            portfolioIds.add(triplet.userProfile.fxPortfolio.getOwnedPortfolioId());
                                        }
                                    }
                                    else if (!(security instanceof WarrantDTO))
                                    {
                                        if (triplet.userProfile.portfolio != null)
                                        {
                                            portfolioIds.add(triplet.userProfile.portfolio.getOwnedPortfolioId());
                                        }
                                    }
                                    return portfolioIds;
                                }));
    }

    private static class Triplet
    {
        @NonNull final UserProfileDTO userProfile;
        @NonNull final PortfolioCompactDTOList portfolioCompactDTOs;
        @NonNull final SecurityPositionDetailDTO securityPositionDetail;

        private Triplet(
                @NonNull UserProfileDTO userProfile,
                @NonNull PortfolioCompactDTOList portfolioCompactDTOs,
                @NonNull SecurityPositionDetailDTO securityPositionDetail)
        {
            this.userProfile = userProfile;
            this.portfolioCompactDTOs = portfolioCompactDTOs;
            this.securityPositionDetail = securityPositionDetail;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Get One User Portfolio">
    protected DTOProcessorPortfolioReceived<PortfolioDTO> createPortfolioReceivedProcessor(@NonNull UserBaseKey userBaseKey)
    {
        return new DTOProcessorPortfolioReceived<>(userBaseKey);
    }

    @NonNull public Observable<PortfolioDTO> getPortfolioRx(
            @NonNull OwnedPortfolioId ownedPortfolioId)
    {
        return this.portfolioServiceRx.getPortfolio(
                ownedPortfolioId.userId,
                ownedPortfolioId.portfolioId)
                .map(createPortfolioReceivedProcessor(ownedPortfolioId.getUserBaseKey()));
    }
    //</editor-fold>

    //<editor-fold desc="Reset Cash">
    protected DTOProcessorUpdateUserProfile createUpdateProfileProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache, homeContentCache.get());
    }

    @NonNull public Observable<UserProfileDTO> resetPortfolioRx(
            @NonNull OwnedPortfolioId ownedPortfolioId,
            PurchaseReportDTO purchaseReportDTO)
    {
        return this.portfolioServiceRx.resetPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseReportDTO)
                .map(createUpdateProfileProcessor());
    }
    //</editor-fold>

    //<editor-fold desc="Add Cash">
    @NonNull public Observable<UserProfileDTO> addCashRx(
            @NonNull OwnedPortfolioId ownedPortfolioId,
            PurchaseReportDTO purchaseReportDTO)
    {
        return this.portfolioServiceRx.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseReportDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Mark One User Portfolio">
    @NonNull public Observable<PortfolioDTO> markPortfolioRx(
            @NonNull OwnedPortfolioId ownedPortfolioId)
    {
        return this.portfolioServiceRx.markPortfolio(
                ownedPortfolioId.userId,
                ownedPortfolioId.portfolioId);
    }
    //</editor-fold>
}
