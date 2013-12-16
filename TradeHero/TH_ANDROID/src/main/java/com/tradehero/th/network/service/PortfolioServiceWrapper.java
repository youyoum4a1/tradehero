package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurpose PortfolioService calls
 * Created by xavier on 12/5/13.
 */
public class PortfolioServiceWrapper
{
    public static final String TAG = PortfolioServiceWrapper.class.getSimpleName();

    @Inject PortfolioService portfolioService;

    public PortfolioServiceWrapper()
    {
        super();
        DaggerUtils.inject(this);
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

    //<editor-fold desc="Get One User Portfolio">
    public PortfolioDTO getPortfolio(OwnedPortfolioId ownedPortfolioId)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        return this.portfolioService.getPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId);
    }

    public void getPortfolio(OwnedPortfolioId ownedPortfolioId, Callback<PortfolioDTO>  callback)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        this.portfolioService.getPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Reset Cash">
    public UserProfileDTO resetPortfolio(OwnedPortfolioId ownedPortfolioId, GooglePlayPurchaseDTO purchaseDTO)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        return this.portfolioService.resetPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO);
    }

    public void resetPortfolio(OwnedPortfolioId ownedPortfolioId, GooglePlayPurchaseDTO purchaseDTO, Callback<UserProfileDTO>  callback)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        this.portfolioService.resetPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Add Cash">
    public UserProfileDTO addCash(OwnedPortfolioId ownedPortfolioId, GooglePlayPurchaseDTO purchaseDTO)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        return this.portfolioService.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO);
    }

    public void addCash(OwnedPortfolioId ownedPortfolioId, GooglePlayPurchaseDTO purchaseDTO, Callback<UserProfileDTO>  callback)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        this.portfolioService.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Mark One User Portfolio">
    public PortfolioDTO markPortfolio(OwnedPortfolioId ownedPortfolioId)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        return this.portfolioService.markPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId);
    }

    public void markPortfolio(OwnedPortfolioId ownedPortfolioId, Callback<PortfolioDTO>  callback)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        this.portfolioService.markPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, callback);
    }
    //</editor-fold>
}
