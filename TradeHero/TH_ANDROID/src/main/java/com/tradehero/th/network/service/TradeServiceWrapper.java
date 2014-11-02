package com.tradehero.th.network.service;

import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.trade.DTOProcessorTradeListReceived;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class TradeServiceWrapper
{
    @NotNull private final TradeService tradeService;
    @NotNull private final TradeServiceRx tradeServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public TradeServiceWrapper(
            @NotNull TradeService tradeService,
            @NotNull TradeServiceRx tradeServiceRx)
    {
        super();
        this.tradeService = tradeService;
        this.tradeServiceRx = tradeServiceRx;
    }
    //</editor-fold>

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

    @NotNull public Observable<TradeDTOList> getTradesRx(@NotNull OwnedPositionId ownedPositionId)
    {
        basicCheck(ownedPositionId);
        return this.tradeServiceRx.getTrades(
                ownedPositionId.userId,
                ownedPositionId.portfolioId,
                ownedPositionId.positionId);
    }
    //</editor-fold>

    //<editor-fold desc="Get One Trade">
    @NotNull public Observable<TradeDTO> getTradeRx(@NotNull OwnedTradeId ownedTradeId)
    {
        basicCheck(ownedTradeId);
        return this.tradeServiceRx.getTrade(
                ownedTradeId.userId,
                ownedTradeId.portfolioId,
                ownedTradeId.positionId,
                ownedTradeId.tradeId);
    }
    //</editor-fold>
}
