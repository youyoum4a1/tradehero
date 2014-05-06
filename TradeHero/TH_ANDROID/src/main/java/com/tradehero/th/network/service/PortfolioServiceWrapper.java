package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorAddCash;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

@Singleton public class PortfolioServiceWrapper
{
    private final PortfolioService portfolioService;
    private final PortfolioServiceAsync portfolioServiceAsync;
    private final UserProfileCache userProfileCache;
    private final Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    private final Lazy<PortfolioCompactCache> portfolioCompactCache;
    private final Lazy<PortfolioCache> portfolioCache;


    @Inject public PortfolioServiceWrapper(
            PortfolioService portfolioService,
            PortfolioServiceAsync portfolioServiceAsync,
            UserProfileCache userProfileCache,
            Lazy<PortfolioCompactListCache> portfolioCompactListCache,
            Lazy<PortfolioCompactCache> portfolioCompactCache,
            Lazy<PortfolioCache> portfolioCache)
    {
        super();
        this.portfolioService = portfolioService;
        this.portfolioServiceAsync = portfolioServiceAsync;
        this.userProfileCache = userProfileCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
    }

    private void basicCheck(OwnedPortfolioId ownedPortfolioId)
    {
        if (ownedPortfolioId == null)
        {
            throw new NullPointerException("ownedPortfolioId cannot be null");
        }
        if (ownedPortfolioId.userId == null)
        {
            throw new NullPointerException("ownedPortfolioId.userId cannot be null");
        }
        if (ownedPortfolioId.portfolioId == null)
        {
            throw new NullPointerException("ownedPortfolioId.portfolioId cannot be null");
        }
    }

    //<editor-fold desc="DTO Processors">
    protected DTOProcessor<UserProfileDTO> createUpdateProfileProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache);
    }

    protected DTOProcessor<UserProfileDTO> createAddCashProcessor(OwnedPortfolioId ownedPortfolioId)
    {
        return new DTOProcessorAddCash(userProfileCache,
                portfolioCompactListCache.get(),
                portfolioCompactCache.get(),
                portfolioCache.get(),
                ownedPortfolioId);
    }
    //</editor-fold>

    //<editor-fold desc="Get One User Portfolio">
    public PortfolioDTO getPortfolio(OwnedPortfolioId ownedPortfolioId)
    {
        basicCheck(ownedPortfolioId);
        return this.portfolioService.getPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId);
    }

    public BaseMiddleCallback<PortfolioDTO> getPortfolio(OwnedPortfolioId ownedPortfolioId, Callback<PortfolioDTO> callback)
    {
        basicCheck(ownedPortfolioId);
        BaseMiddleCallback<PortfolioDTO> middleCallback = new BaseMiddleCallback<PortfolioDTO>(callback);
        this.portfolioServiceAsync.getPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Reset Cash">
    public UserProfileDTO resetPortfolio(OwnedPortfolioId ownedPortfolioId, GooglePlayPurchaseDTO purchaseDTO)
    {
        basicCheck(ownedPortfolioId);
        return createUpdateProfileProcessor().process(this.portfolioService.resetPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO));
    }

    public MiddleCallback<UserProfileDTO> resetPortfolio(OwnedPortfolioId ownedPortfolioId, GooglePlayPurchaseDTO purchaseDTO, Callback<UserProfileDTO> callback)
    {
        basicCheck(ownedPortfolioId);
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
        this.portfolioServiceAsync.resetPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Add Cash">
    public UserProfileDTO addCash(OwnedPortfolioId ownedPortfolioId, GooglePlayPurchaseDTO purchaseDTO)
    {
        basicCheck(ownedPortfolioId);
        return createAddCashProcessor(ownedPortfolioId).process(this.portfolioService.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO));
    }

    public MiddleCallback<UserProfileDTO> addCash(OwnedPortfolioId ownedPortfolioId, GooglePlayPurchaseDTO purchaseDTO, Callback<UserProfileDTO> callback)
    {
        basicCheck(ownedPortfolioId);
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createAddCashProcessor(ownedPortfolioId));
        this.portfolioServiceAsync.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Mark One User Portfolio">
    public PortfolioDTO markPortfolio(OwnedPortfolioId ownedPortfolioId)
    {
        basicCheck(ownedPortfolioId);
        return this.portfolioService.markPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId);
    }

    public BaseMiddleCallback<PortfolioDTO> markPortfolio(OwnedPortfolioId ownedPortfolioId, Callback<PortfolioDTO> callback)
    {
        BaseMiddleCallback<PortfolioDTO> middleCallback = new BaseMiddleCallback<PortfolioDTO>(callback);
        basicCheck(ownedPortfolioId);
        this.portfolioServiceAsync.markPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
