package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.models.BaseDTOListProcessor;
import com.tradehero.th.models.trade.DTOProcessorTradeReceived;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class TradeServiceWrapper
{
    @NonNull private final TradeServiceRx tradeServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public TradeServiceWrapper(@NonNull TradeServiceRx tradeServiceRx)
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
    @NonNull public Observable<TradeDTOList> getTradesRx(@NonNull OwnedPositionId ownedPositionId)
    {
        basicCheck(ownedPositionId);
        return this.tradeServiceRx.getTrades(
                ownedPositionId.userId,
                ownedPositionId.portfolioId,
                ownedPositionId.positionId)
                .map(new BaseDTOListProcessor<TradeDTO, TradeDTOList>(
                        new DTOProcessorTradeReceived(ownedPositionId)));
    }
    //</editor-fold>

    //<editor-fold desc="Get One Trade">
    @NonNull public Observable<TradeDTO> getTradeRx(@NonNull OwnedTradeId ownedTradeId)
    {
        basicCheck(ownedTradeId);
        return this.tradeServiceRx.getTrade(
                ownedTradeId.userId,
                ownedTradeId.portfolioId,
                ownedTradeId.positionId,
                ownedTradeId.tradeId)
                .map(new DTOProcessorTradeReceived(ownedTradeId));
    }
    //</editor-fold>
}
