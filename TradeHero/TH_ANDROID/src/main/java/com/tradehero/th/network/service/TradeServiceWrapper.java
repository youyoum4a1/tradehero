package com.tradehero.th.network.service;

import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.ClosedTradeDTOList;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.trade.DTOProcessorTradeListReceived;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.client.Response;

@Singleton public class TradeServiceWrapper
{
    @NotNull private final TradeService tradeService;

    @Inject public TradeServiceWrapper(
            @NotNull TradeService tradeService)
    {
        super();
        this.tradeService = tradeService;
    }

    private void basicCheck(OwnedPositionId ownedPositionId)
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
    //<editor-fold desc="Get Trades List">
    @NotNull private DTOProcessor<TradeDTOList> createTradeListReceivedProcessor(
            @NotNull OwnedPositionId ownedPositionId)
    {
        return new DTOProcessorTradeListReceived(ownedPositionId);
    }

    @NotNull public TradeDTOList getTrades(@NotNull OwnedPositionId ownedPositionId)
    {
        basicCheck(ownedPositionId);
        return createTradeListReceivedProcessor(ownedPositionId).process(
                this.tradeService.getTrades(
                        ownedPositionId.userId,
                        ownedPositionId.portfolioId,
                        ownedPositionId.positionId));
    }

    public void getTrades(Callback<ClosedTradeDTOList> callback)
    {
        tradeService.getClosedTrade(callback);
    }

    public void getDelegation(Callback<ClosedTradeDTOList> callback)
    {
        tradeService.getDelegation(callback);
    }

    public void getPendingDelegation(Callback<ClosedTradeDTOList> callback)
    {
        tradeService.getPendingDelegation(callback);
    }

    public void deletePendingDelegation(int orderId, Callback<Response> callback)
    {
        tradeService.deletePendingDelegation(orderId, callback);
    }
}
