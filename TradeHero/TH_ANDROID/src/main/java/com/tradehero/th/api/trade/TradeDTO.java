package com.tradehero.th.api.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;
import java.util.Date;

public class TradeDTO implements DTO
{
    public int id;
    @JsonProperty("unit_price")
    public double unitPrice;
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

    public boolean isLastTrade;

    public TradeDTO()
    {
    }

    public TradeId getTradeId()
    {
        return new TradeId(id);
    }
}