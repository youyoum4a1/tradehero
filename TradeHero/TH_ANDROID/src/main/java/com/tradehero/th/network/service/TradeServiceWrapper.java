package com.tradehero.th.network.service;

import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.trade.DTOProcessorTradeListReceived;
import com.tradehero.th.models.trade.DTOProcessorTradeReceived;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class TradeServiceWrapper
{
    @NotNull private final TradeService tradeService;
    @NotNull private final TradeServiceAsync tradeServiceAsync;

    @Inject public TradeServiceWrapper(
            @NotNull TradeService tradeService,
            @NotNull TradeServiceAsync tradeServiceAsync)
    {
        super();
        this.tradeService = tradeService;
        this.tradeServiceAsync = tradeServiceAsync;
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

    @NotNull public MiddleCallback<TradeDTOList> getTrades(
            @NotNull OwnedPositionId ownedPositionId,
            @Nullable Callback<TradeDTOList> callback)
    {
        basicCheck(ownedPositionId);
        MiddleCallback<TradeDTOList> middleCallback = new BaseMiddleCallback<>(
                callback,
                createTradeListReceivedProcessor(ownedPositionId));
        this.tradeServiceAsync.getTrades(
                ownedPositionId.userId,
                ownedPositionId.portfolioId,
                ownedPositionId.positionId,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get One Trade">
    @NotNull private DTOProcessor<TradeDTO> createTradeReceivedProcessor(
            @NotNull OwnedPositionId ownedPositionId)
    {
        return new DTOProcessorTradeReceived(ownedPositionId);
    }

    @NotNull public TradeDTO getTrade(@NotNull OwnedTradeId ownedTradeId)
    {
        basicCheck(ownedTradeId);
        return createTradeReceivedProcessor(ownedTradeId).process(
                this.tradeService.getTrade(
                        ownedTradeId.userId,
                        ownedTradeId.portfolioId,
                        ownedTradeId.positionId,
                        ownedTradeId.tradeId));
    }

    @NotNull public MiddleCallback<TradeDTO> getTrade(
            @NotNull OwnedTradeId ownedTradeId,
            @Nullable Callback<TradeDTO> callback)
    {
        basicCheck(ownedTradeId);
        MiddleCallback<TradeDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createTradeReceivedProcessor(ownedTradeId));
        this.tradeServiceAsync.getTrade(
                ownedTradeId.userId,
                ownedTradeId.portfolioId,
                ownedTradeId.positionId,
                ownedTradeId.tradeId,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
