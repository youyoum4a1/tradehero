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

    public static GetPositionsDTO getPositions(PositionService positionService, OwnedPortfolioId key)
        throws RetrofitError
    {
        if (key instanceof PerPagedOwnedPortfolioId)
        {
            return positionService.getPositions(key.userId, key.portfolioId, ((PerPagedOwnedPortfolioId) key).page, ((PerPagedOwnedPortfolioId) key).perPage);
        }
        if (key instanceof PagedOwnedPortfolioId)
        {
            return positionService.getPositions(key.userId, key.portfolioId, ((PagedOwnedPortfolioId) key).page);
        }
        return positionService.getPositions(key.userId, key.portfolioId);
    }

    public static void getPositions(PositionService positionService, OwnedPortfolioId key, Callback<GetPositionsDTO> callback)
    {
        if (key instanceof PerPagedOwnedPortfolioId)
        {
            positionService.getPositions(key.userId, key.portfolioId, ((PerPagedOwnedPortfolioId) key).page, ((PerPagedOwnedPortfolioId) key).perPage,
                    callback);
        }
        if (key instanceof PagedOwnedPortfolioId)
        {
            positionService.getPositions(key.userId, key.portfolioId, ((PagedOwnedPortfolioId) key).page, callback);
        }
        else
        {
            positionService.getPositions(key.userId, key.portfolioId, callback);
        }
    }
}
