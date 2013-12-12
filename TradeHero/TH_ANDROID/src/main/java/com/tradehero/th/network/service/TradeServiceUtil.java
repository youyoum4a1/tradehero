package com.tradehero.th.network.service;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurposes trade requests
 * Created by xavier on 12/12/13.
 */
public class TradeServiceUtil
{
    public static final String TAG = TradeServiceUtil.class.getSimpleName();

    private static void basicCheck(OwnedPositionId ownedPositionId)
    {
        if (ownedPositionId == null)
        {
            throw new NullPointerException("ownedPositionId cannot be null");
        }
        if (ownedPositionId.userId == null)
        {
            throw new NullPointerException("ownedPositionId.userId cannot be null");
        }
        if (ownedPositionId.portfolioId == null)
        {
            throw new NullPointerException("ownedPositionId.portfolioId cannot be null");
        }
        if (ownedPositionId.positionId == null)
        {
            throw new NullPointerException("ownedPositionId.positionId cannot be null");
        }
    }

    private static void basicCheck(OwnedTradeId ownedTradeId)
    {
        basicCheck((OwnedPositionId) ownedTradeId);
        if (ownedTradeId.tradeId == null)
        {
            throw new NullPointerException("ownedTradeId.tradeId cannot be null");
        }
    }

    public static List<TradeDTO> getTrades(TradeService tradeService, OwnedPositionId ownedPositionId)
            throws RetrofitError
    {
        basicCheck(ownedPositionId);
        return tradeService.getTrades(ownedPositionId.userId, ownedPositionId.portfolioId, ownedPositionId.positionId);
    }

    public static void getTrades(TradeService tradeService, OwnedPositionId ownedPositionId, Callback<List<TradeDTO>> callback)
            throws RetrofitError
    {
        basicCheck(ownedPositionId);
        tradeService.getTrades(ownedPositionId.userId, ownedPositionId.portfolioId, ownedPositionId.positionId, callback);
    }

    public static TradeDTO getTrade(TradeService tradeService, OwnedTradeId ownedTradeId)
            throws RetrofitError
    {
        basicCheck(ownedTradeId);
        return tradeService.getTrade(ownedTradeId.userId, ownedTradeId.portfolioId, ownedTradeId.positionId, ownedTradeId.tradeId);
    }

    public static void getTrade(TradeService tradeService, OwnedTradeId ownedTradeId, Callback<TradeDTO> callback)
            throws RetrofitError
    {
        basicCheck(ownedTradeId);
        tradeService.getTrade(ownedTradeId.userId, ownedTradeId.portfolioId, ownedTradeId.positionId, ownedTradeId.tradeId, callback);
    }
}
