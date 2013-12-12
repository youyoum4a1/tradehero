package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurpose PortfolioService calls
 * Created by xavier on 12/5/13.
 */
public class PortfolioServiceUtil
{
    public static final String TAG = PortfolioServiceUtil.class.getSimpleName();

    private static void basicCheck(OwnedPortfolioId ownedPortfolioId)
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
    public static PortfolioDTO getPortfolio(PortfolioService portfolioService, OwnedPortfolioId ownedPortfolioId)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        return portfolioService.getPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId);
    }

    public static void getPortfolio(PortfolioService portfolioService, OwnedPortfolioId ownedPortfolioId, Callback<PortfolioDTO>  callback)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        portfolioService.getPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Reset Cash">
    public static UserProfileDTO resetPortfolio(PortfolioService portfolioService, OwnedPortfolioId ownedPortfolioId, GooglePlayPurchaseDTO purchaseDTO)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        return portfolioService.resetPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO);
    }

    public static void resetPortfolio(PortfolioService portfolioService, OwnedPortfolioId ownedPortfolioId, GooglePlayPurchaseDTO purchaseDTO, Callback<UserProfileDTO>  callback)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        portfolioService.resetPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Add Cash">
    public static UserProfileDTO addCash(PortfolioService portfolioService, OwnedPortfolioId ownedPortfolioId, GooglePlayPurchaseDTO purchaseDTO)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        return portfolioService.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO);
    }

    public static void addCash(PortfolioService portfolioService, OwnedPortfolioId ownedPortfolioId, GooglePlayPurchaseDTO purchaseDTO, Callback<UserProfileDTO>  callback)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        portfolioService.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Mark One User Portfolio">
    public static PortfolioDTO markPortfolio(PortfolioService portfolioService, OwnedPortfolioId ownedPortfolioId)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        return portfolioService.markPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId);
    }

    public static void markPortfolio(PortfolioService portfolioService, OwnedPortfolioId ownedPortfolioId, Callback<PortfolioDTO>  callback)
        throws RetrofitError
    {
        basicCheck(ownedPortfolioId);
        portfolioService.markPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, callback);
    }
    //</editor-fold>
}
