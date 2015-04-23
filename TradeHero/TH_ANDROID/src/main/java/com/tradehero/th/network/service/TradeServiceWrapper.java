package com.tradehero.th.network.service;

import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.trade.DTOProcessorTradeListReceived;
import com.tradehero.th.models.trade.DTOProcessorTradeReceived;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    private void basicCheck(OwnedTradeId ownedTradeId)
    {
        basicCheck((OwnedPositionId) ownedTradeId);
        if (ownedTradeId.tradeId == null)
        {
            throw new NullPointerException("ownedTradeId.tradeId cannot be null");
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

    //<editor-fold desc="Get One Trade">
    @NotNull private DTOProcessor<TradeDTO> createTradeReceivedProcessor(
            @NotNull OwnedPositionId ownedPositionId)
    {
        return new DTOProcessorTradeReceived(ownedPositionId);
    }
}
