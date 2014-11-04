package com.tradehero.th.network.service;

import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.models.trade.DTOProcessorTradeListReceived;
import com.tradehero.th.models.trade.DTOProcessorTradeReceived;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class TradeServiceWrapper
{
    @NotNull private final TradeServiceRx tradeServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public TradeServiceWrapper(@NotNull TradeServiceRx tradeServiceRx)
    {
        super();
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
    @NotNull private DTOProcessorTradeListReceived createTradeListReceivedProcessor(
            @NotNull OwnedPositionId ownedPositionId)
    {
        return new DTOProcessorTradeListReceived(ownedPositionId);
    }

    @NotNull public Observable<TradeDTOList> getTradesRx(@NotNull OwnedPositionId ownedPositionId)
    {
        basicCheck(ownedPositionId);
        return this.tradeServiceRx.getTrades(
                ownedPositionId.userId,
                ownedPositionId.portfolioId,
                ownedPositionId.positionId)
                .doOnNext(createTradeListReceivedProcessor(ownedPositionId));
    }
    //</editor-fold>

    //<editor-fold desc="Get One Trade">
    @NotNull private DTOProcessorTradeReceived createTradeReceivedProcessor(
            @NotNull OwnedPositionId ownedPositionId)
    {
        return new DTOProcessorTradeReceived(ownedPositionId);
    }

    @NotNull public Observable<TradeDTO> getTradeRx(@NotNull OwnedTradeId ownedTradeId)
    {
        basicCheck(ownedTradeId);
        return this.tradeServiceRx.getTrade(
                ownedTradeId.userId,
                ownedTradeId.portfolioId,
                ownedTradeId.positionId,
                ownedTradeId.tradeId)
                .doOnNext(createTradeReceivedProcessor(ownedTradeId));
    }
    //</editor-fold>
}
