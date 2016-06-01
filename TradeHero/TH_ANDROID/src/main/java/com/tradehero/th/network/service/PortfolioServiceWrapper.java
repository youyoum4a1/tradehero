package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.billing.PurchaseReportDTO;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTO;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTOList;
import com.ayondo.academy.api.portfolio.PortfolioDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.models.BaseDTOListProcessor;
import com.ayondo.academy.models.portfolio.DTOProcessorPortfolioReceived;
import com.ayondo.academy.models.user.DTOProcessorUpdateUserProfile;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

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
                        new DTOProcessorPortfolioReceived<>(userBaseKey)));
    }
    //</editor-fold>

    //<editor-fold desc="Get One User Portfolio">
    @NonNull public Observable<PortfolioDTO> getPortfolioRx(
            @NonNull OwnedPortfolioId ownedPortfolioId)
    {
        return this.portfolioServiceRx.getPortfolio(
                        ownedPortfolioId.userId,
                        ownedPortfolioId.portfolioId)
                .map(new DTOProcessorPortfolioReceived<PortfolioDTO>(ownedPortfolioId.getUserBaseKey()));
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
