package com.tradehero.th.network.service;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PagedOwnedPortfolioId;
import com.tradehero.th.api.portfolio.PerPagedOwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurpose PositionService calls
 * Created by xavier on 12/5/13.
 */
@Singleton public class PositionServiceWrapper
{
    public static final String TAG = PositionServiceWrapper.class.getSimpleName();

    @Inject PositionService positionService;

    @Inject public PositionServiceWrapper()
    {
        super();
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

    //<editor-fold desc="Get One User Portfolio Positions List">
    public GetPositionsDTO getPositions(OwnedPortfolioId ownedPortfolioId)
        throws RetrofitError
    {
        if (ownedPortfolioId instanceof PagedOwnedPortfolioId)
        {
            return getPositions((PagedOwnedPortfolioId) ownedPortfolioId);
        }
        basicCheck(ownedPortfolioId);
        return this.positionService.getPositions(ownedPortfolioId.userId, ownedPortfolioId.portfolioId);
    }

    public GetPositionsDTO getPositions(PagedOwnedPortfolioId pagedOwnedPortfolioId)
        throws RetrofitError
    {
        if (pagedOwnedPortfolioId instanceof PerPagedOwnedPortfolioId)
        {
            return getPositions((PerPagedOwnedPortfolioId) pagedOwnedPortfolioId);
        }
        basicCheck(pagedOwnedPortfolioId);
        if (pagedOwnedPortfolioId.page == null)
        {
            return this.positionService.getPositions(pagedOwnedPortfolioId.userId, pagedOwnedPortfolioId.portfolioId);
        }
        return this.positionService.getPositions(pagedOwnedPortfolioId.userId, pagedOwnedPortfolioId.portfolioId, pagedOwnedPortfolioId.page);
    }

    public GetPositionsDTO getPositions(PerPagedOwnedPortfolioId perPagedOwnedPortfolioId)
        throws RetrofitError
    {
        basicCheck(perPagedOwnedPortfolioId);
        if (perPagedOwnedPortfolioId.page == null)
        {
            return this.positionService.getPositions(perPagedOwnedPortfolioId.userId, perPagedOwnedPortfolioId.portfolioId);
        }
        else if (perPagedOwnedPortfolioId.perPage == null)
        {
            return this.positionService.getPositions(perPagedOwnedPortfolioId.userId, perPagedOwnedPortfolioId.portfolioId, perPagedOwnedPortfolioId.page);
        }
        return this.positionService.getPositions(perPagedOwnedPortfolioId.userId, perPagedOwnedPortfolioId.portfolioId, perPagedOwnedPortfolioId.page, perPagedOwnedPortfolioId.perPage);
    }

    public void getPositions(OwnedPortfolioId kownedPortfolioIdy, Callback<GetPositionsDTO> callback)
    {
        if (kownedPortfolioIdy instanceof PagedOwnedPortfolioId)
        {
            getPositions((PagedOwnedPortfolioId) kownedPortfolioIdy, callback);
        }
        else
        {
            basicCheck(kownedPortfolioIdy);
            this.positionService.getPositions(kownedPortfolioIdy.userId, kownedPortfolioIdy.portfolioId, callback);
        }
    }

    public void getPositions(PagedOwnedPortfolioId pagedOwnedPortfolioId, Callback<GetPositionsDTO> callback)
    {
        basicCheck(pagedOwnedPortfolioId);
        if (pagedOwnedPortfolioId instanceof PerPagedOwnedPortfolioId)
        {
            getPositions((PerPagedOwnedPortfolioId) pagedOwnedPortfolioId, callback);
        }
        else if (pagedOwnedPortfolioId.page == null)
        {
            this.positionService.getPositions(pagedOwnedPortfolioId.userId, pagedOwnedPortfolioId.portfolioId, callback);
        }
        else
        {
            this.positionService.getPositions(pagedOwnedPortfolioId.userId, pagedOwnedPortfolioId.portfolioId, pagedOwnedPortfolioId.page, callback);
        }
    }

    public void getPositions(PerPagedOwnedPortfolioId perPagedOwnedPortfolioId, Callback<GetPositionsDTO> callback)
    {
        basicCheck(perPagedOwnedPortfolioId);
        if (perPagedOwnedPortfolioId.page == null)
        {
            this.positionService.getPositions(perPagedOwnedPortfolioId.userId, perPagedOwnedPortfolioId.portfolioId, callback);
        }
        else if (perPagedOwnedPortfolioId.perPage == null)
        {
            this.positionService.getPositions(perPagedOwnedPortfolioId.userId, perPagedOwnedPortfolioId.portfolioId, perPagedOwnedPortfolioId.page, callback);
        }
        else
        {
            this.positionService.getPositions(perPagedOwnedPortfolioId.userId, perPagedOwnedPortfolioId.portfolioId, perPagedOwnedPortfolioId.page, perPagedOwnedPortfolioId.perPage, callback);
        }
    }
    //</editor-fold>
}
