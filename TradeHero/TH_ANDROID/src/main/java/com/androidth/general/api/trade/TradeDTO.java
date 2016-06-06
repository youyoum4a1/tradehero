package com.androidth.general.api.trade;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.androidth.general.common.persistence.DTO;
import java.util.Date;

public class TradeDTO implements DTO
{
    public int id;
    @JsonProperty("unit_price")
    public double unitPriceSecCcy;
    @JsonProperty("transaction_cost")
    public double transactionCost;
    public int quantity;
    @JsonProperty("date_time")
    public Date dateTime;
    @JsonProperty("exchange_rate")
    public double exchangeRate;
    @JsonProperty("quantity_after_trade")
    public int quantityAfterTrade;

    @JsonProperty("average_price_after_trade")
    public double averagePriceAfterTradeRefCcy;
    @JsonProperty("realized_pl_after_trade")
    public double realizedPLAfterTradeRefCcy;

    public String commentText;

    //<editor-fold desc="These need to be set on client side, in ServiceWrapper">
    public int userId;
    public int portfolioId;
    public int positionId;
    //</editor-fold>

    @JsonIgnore @NonNull public OwnedTradeId getOwnedTradeId()
    {
        return new OwnedTradeId(userId, portfolioId, positionId, id);
    }
}