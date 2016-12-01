package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.billing.PurchaseReportDTO;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioCompactDTOList;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.models.BaseDTOListProcessor;
import com.androidth.general.models.portfolio.DTOProcessorPortfolioReceived;
import com.androidth.general.models.user.DTOProcessorUpdateUserProfile;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.schedulers.Schedulers;

@Singleton public class PortfolioServiceWrapper
{
    @NonNull private final PortfolioServiceRx portfolioServiceRx;
    @NonNull private final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioServiceWrapper(
            @NonNull PortfolioServiceRx portfolioServiceRx,
            @NonNull UserProfileCacheRx userProfileCache)
    {
        super();
        this.portfolioServiceRx = portfolioServiceRx;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get User Portfolio List">
    @NonNull public Observable<PortfolioCompactDTOList> getPortfoliosRx(
            @NonNull UserBaseKey userBaseKey,
            @Nullable Boolean includeWatchList)
    {
        return portfolioServiceRx.getPortfolios(userBaseKey.key, includeWatchList)
                .map(new BaseDTOListProcessor<PortfolioCompactDTO, PortfolioCompactDTOList>(
                        new DTOProcessorPortfolioReceived<>(userBaseKey)))
                .subscribeOn(Schedulers.io());//to avoid NetworkOnMainThreadException
    }
    //</editor-fold>

    //<editor-fold desc="Get One User Portfolio">
    @NonNull public Observable<PortfolioDTO> getPortfolioRxMainThread(
            @NonNull OwnedPortfolioId ownedPortfolioId)
    {
        return this.portfolioServiceRx.getPortfolio(
                        ownedPortfolioId.userId,
                        ownedPortfolioId.portfolioId)
                .map(new DTOProcessorPortfolioReceived<PortfolioDTO>(ownedPortfolioId.getUserBaseKey()))
                .subscribeOn(Schedulers.io());//to avoid NetworkOnMainThreadException
    }
    //</editor-fold>

    //<editor-fold desc="Reset Cash">
    @NonNull public Observable<UserProfileDTO> resetPortfolioRx(
            @NonNull OwnedPortfolioId ownedPortfolioId,
            PurchaseReportDTO purchaseReportDTO)
    {
        return this.portfolioServiceRx.resetPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseReportDTO)
                .map(new DTOProcessorUpdateUserProfile(userProfileCache));
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
}
