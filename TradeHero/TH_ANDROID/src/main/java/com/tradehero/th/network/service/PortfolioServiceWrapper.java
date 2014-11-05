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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton public class PortfolioServiceWrapper
{
    @NonNull private final PortfolioService portfolioService;
    @NonNull private final PortfolioServiceAsync portfolioServiceAsync;
    @NonNull private final PortfolioServiceRx portfolioServiceRx;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache;
    @NonNull private final Lazy<PortfolioCompactCacheRx> portfolioCompactCache;
    @NonNull private final Lazy<PortfolioCacheRx> portfolioCache;
    @NonNull private final Lazy<HomeContentCacheRx> homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioServiceWrapper(
            @NonNull PortfolioService portfolioService,
            @NonNull PortfolioServiceAsync portfolioServiceAsync,
            @NonNull PortfolioServiceRx portfolioServiceRx,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<PortfolioCompactCacheRx> portfolioCompactCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull Lazy<HomeContentCacheRx> homeContentCache)
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
                .doOnNext(createUpdateProfileProcessor());
    }

    @Deprecated
    @NonNull public MiddleCallback<UserProfileDTO> resetPortfolio(
            @NonNull OwnedPortfolioId ownedPortfolioId,
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

    @NonNull public Observable<UserProfileDTO> addCashRx(
            @NonNull OwnedPortfolioId ownedPortfolioId,
            PurchaseReportDTO purchaseReportDTO)
    {
        return this.portfolioServiceRx.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseReportDTO)
                .doOnNext(createAddCashProcessor(ownedPortfolioId));
    }

    @NonNull public MiddleCallback<UserProfileDTO> addCash(
            @NonNull OwnedPortfolioId ownedPortfolioId,
            PurchaseReportDTO purchaseReportDTO,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createAddCashProcessor(ownedPortfolioId));
        this.portfolioServiceAsync.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseReportDTO, middleCallback);
        return middleCallback;
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
