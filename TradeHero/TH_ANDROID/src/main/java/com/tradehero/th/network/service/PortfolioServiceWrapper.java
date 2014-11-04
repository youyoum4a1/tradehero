package com.tradehero.th.network.service;

import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.portfolio.DTOProcessorPortfolioListReceived;
import com.tradehero.th.models.portfolio.DTOProcessorPortfolioReceived;
import com.tradehero.th.models.user.DTOProcessorAddCash;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton public class PortfolioServiceWrapper
{
    @NotNull private final PortfolioService portfolioService;
    @NotNull private final PortfolioServiceAsync portfolioServiceAsync;
    @NotNull private final PortfolioServiceRx portfolioServiceRx;
    @NotNull private final UserProfileCacheRx userProfileCache;
    @NotNull private final Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache;
    @NotNull private final Lazy<PortfolioCompactCacheRx> portfolioCompactCache;
    @NotNull private final Lazy<PortfolioCacheRx> portfolioCache;
    @NotNull private final Lazy<HomeContentCacheRx> homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioServiceWrapper(
            @NotNull PortfolioService portfolioService,
            @NotNull PortfolioServiceAsync portfolioServiceAsync,
            @NotNull PortfolioServiceRx portfolioServiceRx,
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NotNull Lazy<PortfolioCompactCacheRx> portfolioCompactCache,
            @NotNull Lazy<PortfolioCacheRx> portfolioCache,
            @NotNull Lazy<HomeContentCacheRx> homeContentCache)
    {
        super();
        this.portfolioService = portfolioService;
        this.portfolioServiceAsync = portfolioServiceAsync;
        this.portfolioServiceRx = portfolioServiceRx;
        this.userProfileCache = userProfileCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
        this.homeContentCache = homeContentCache;
    }
    //</editor-fold>

    //<editor-fold desc="DTO Processors">
    protected DTOProcessorPortfolioReceived<PortfolioCompactDTO> createPortfolioCompactReceivedProcessor(@NotNull UserBaseKey userBaseKey)
    {
        return new DTOProcessorPortfolioReceived<>(userBaseKey);
    }
    //</editor-fold>

    //<editor-fold desc="Get User Portfolio List">
    protected DTOProcessorPortfolioListReceived<PortfolioCompactDTOList> createPortfolioCompactListReceivedProcessor(@NotNull UserBaseKey userBaseKey)
    {
        return new DTOProcessorPortfolioListReceived<>(userBaseKey);
    }

    @NotNull public Observable<PortfolioCompactDTOList> getPortfoliosRx(
            @NotNull UserBaseKey userBaseKey,
            @Nullable Boolean includeWatchList)
    {
        return portfolioServiceRx.getPortfolios(userBaseKey.key, includeWatchList)
                .map(createPortfolioCompactListReceivedProcessor(userBaseKey));
    }
    //</editor-fold>

    //<editor-fold desc="Get One User Portfolio">
    protected DTOProcessorPortfolioReceived<PortfolioDTO> createPortfolioReceivedProcessor(@NotNull UserBaseKey userBaseKey)
    {
        return new DTOProcessorPortfolioReceived<>(userBaseKey);
    }

    @NotNull public Observable<PortfolioDTO> getPortfolioRx(
            @NotNull OwnedPortfolioId ownedPortfolioId)
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

    @NotNull public Observable<UserProfileDTO> resetPortfolioRx(
            @NotNull OwnedPortfolioId ownedPortfolioId,
            PurchaseReportDTO purchaseReportDTO)
    {
        return this.portfolioServiceRx.resetPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseReportDTO)
                .doOnNext(createUpdateProfileProcessor());
    }

    @Deprecated
    @NotNull public MiddleCallback<UserProfileDTO> resetPortfolio(
            @NotNull OwnedPortfolioId ownedPortfolioId,
            PurchaseReportDTO purchaseDTO,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
        this.portfolioServiceAsync.resetPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Add Cash">
    protected DTOProcessorAddCash createAddCashProcessor(OwnedPortfolioId ownedPortfolioId)
    {
        return new DTOProcessorAddCash(userProfileCache,
                homeContentCache.get(),
                portfolioCompactListCache.get(),
                portfolioCompactCache.get(),
                portfolioCache.get(),
                ownedPortfolioId);
    }

    @NotNull public Observable<UserProfileDTO> addCashRx(
            @NotNull OwnedPortfolioId ownedPortfolioId,
            PurchaseReportDTO purchaseReportDTO)
    {
        return this.portfolioServiceRx.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseReportDTO)
                .doOnNext(createAddCashProcessor(ownedPortfolioId));
    }

    @NotNull public MiddleCallback<UserProfileDTO> addCash(
            @NotNull OwnedPortfolioId ownedPortfolioId,
            PurchaseReportDTO purchaseReportDTO,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createAddCashProcessor(ownedPortfolioId));
        this.portfolioServiceAsync.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseReportDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Mark One User Portfolio">
    @NotNull public Observable<PortfolioDTO> markPortfolioRx(
            @NotNull OwnedPortfolioId ownedPortfolioId)
    {
        return this.portfolioServiceRx.markPortfolio(
                        ownedPortfolioId.userId,
                        ownedPortfolioId.portfolioId);
    }
    //</editor-fold>
}
