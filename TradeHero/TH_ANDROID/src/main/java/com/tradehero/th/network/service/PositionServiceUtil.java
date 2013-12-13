package com.tradehero.th.network.service;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PagedOwnedPortfolioId;
import com.tradehero.th.api.portfolio.PerPagedOwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurpose PositionService calls
 * Created by xavier on 12/5/13.
 */
public class PositionServiceUtil
{
    public static final String TAG = PositionServiceUtil.class.getSimpleName();

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

    public static GetPositionsDTO getPositions(PositionService positionService, OwnedPortfolioId ownedPortfolioId)
        throws RetrofitError
    {
        if (ownedPortfolioId instanceof PagedOwnedPortfolioId)
        {
            return getPositions(positionService, (PagedOwnedPortfolioId) ownedPortfolioId);
        }
        basicCheck(ownedPortfolioId);
        return positionService.getPositions(ownedPortfolioId.userId, ownedPortfolioId.portfolioId);
    }

    public static GetPositionsDTO getPositions(PositionService positionService, PagedOwnedPortfolioId pagedOwnedPortfolioId)
        throws RetrofitError
    {
        if (pagedOwnedPortfolioId instanceof PerPagedOwnedPortfolioId)
        {
            return getPositions(positionService, (PerPagedOwnedPortfolioId) pagedOwnedPortfolioId);
        }
        basicCheck(pagedOwnedPortfolioId);
        if (pagedOwnedPortfolioId.page == null)
        {
            return positionService.getPositions(pagedOwnedPortfolioId.userId, pagedOwnedPortfolioId.portfolioId);
        }
        return positionService.getPositions(pagedOwnedPortfolioId.userId, pagedOwnedPortfolioId.portfolioId, pagedOwnedPortfolioId.page);
    }

    public static GetPositionsDTO getPositions(PositionService positionService, PerPagedOwnedPortfolioId perPagedOwnedPortfolioId)
        throws RetrofitError
    {
        basicCheck(perPagedOwnedPortfolioId);
        if (perPagedOwnedPortfolioId.page == null)
        {
            return positionService.getPositions(perPagedOwnedPortfolioId.userId, perPagedOwnedPortfolioId.portfolioId);
        }
        else if (perPagedOwnedPortfolioId.perPage == null)
        {
            return positionService.getPositions(perPagedOwnedPortfolioId.userId, perPagedOwnedPortfolioId.portfolioId, perPagedOwnedPortfolioId.page);
        }
        return positionService.getPositions(perPagedOwnedPortfolioId.userId, perPagedOwnedPortfolioId.portfolioId, perPagedOwnedPortfolioId.page, perPagedOwnedPortfolioId.perPage);
    }

    public static void getPositions(PositionService positionService, OwnedPortfolioId kownedPortfolioIdy, Callback<GetPositionsDTO> callback)
    {
        if (kownedPortfolioIdy instanceof PagedOwnedPortfolioId)
        {
            getPositions(positionService, (PagedOwnedPortfolioId) kownedPortfolioIdy, callback);
        }
        else
        {
            basicCheck(kownedPortfolioIdy);
            positionService.getPositions(kownedPortfolioIdy.userId, kownedPortfolioIdy.portfolioId, callback);
        }
    }

    public static void getPositions(PositionService positionService, PagedOwnedPortfolioId pagedOwnedPortfolioId, Callback<GetPositionsDTO> callback)
    {
        basicCheck(pagedOwnedPortfolioId);
        if (pagedOwnedPortfolioId instanceof PerPagedOwnedPortfolioId)
        {
            getPositions(positionService, (PerPagedOwnedPortfolioId) pagedOwnedPortfolioId, callback);
        }
        else if (pagedOwnedPortfolioId.page == null)
        {
            positionService.getPositions(pagedOwnedPortfolioId.userId, pagedOwnedPortfolioId.portfolioId, callback);
        }
        else
        {
            positionService.getPositions(pagedOwnedPortfolioId.userId, pagedOwnedPortfolioId.portfolioId, pagedOwnedPortfolioId.page, callback);
        }
    }

    public static void getPositions(PositionService positionService, PerPagedOwnedPortfolioId perPagedOwnedPortfolioId, Callback<GetPositionsDTO> callback)
    {
        basicCheck(perPagedOwnedPortfolioId);
        if (perPagedOwnedPortfolioId.page == null)
        {
            positionService.getPositions(perPagedOwnedPortfolioId.userId, perPagedOwnedPortfolioId.portfolioId, callback);
        }
        else if (perPagedOwnedPortfolioId.perPage == null)
        {
            positionService.getPositions(perPagedOwnedPortfolioId.userId, perPagedOwnedPortfolioId.portfolioId, perPagedOwnedPortfolioId.page, callback);
        }
        else
        {
            positionService.getPositions(perPagedOwnedPortfolioId.userId, perPagedOwnedPortfolioId.portfolioId, perPagedOwnedPortfolioId.page, perPagedOwnedPortfolioId.perPage, callback);
        }
    }
}
