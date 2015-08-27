package com.tradehero.th.api.trade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;
import com.tradehero.livetrade.DataUtils;
import com.tradehero.th.utils.SecurityUtils;

import java.util.Date;
import org.jetbrains.annotations.NotNull;

public class TradeDTO implements DTO
{
    public int id;
    @JsonProperty("unit_price")
    private double unitPriceRefCcy;
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

    @JsonProperty("unit_price_currency")
    public String unit_price_currency;

    public String commentText;

    //<editor-fold desc="These need to be set on client side, in ServiceWrapper">
    public int userId;
    public int portfolioId;
    public int positionId;
    //</editor-fold>

    public TradeDTO() {
    }

    @JsonIgnore @NotNull public OwnedTradeId getOwnedTradeId() {
        return new OwnedTradeId(userId, portfolioId, positionId, id);
    }

    public String getCurrencyDisplay() {
        return SecurityUtils.getCurrencyShortDispaly(unit_price_currency);
    }

    //成交金额
    public String displayTradeMoney() {
        return "" + Math.round(Math.abs(quantity * unitPriceRefCcy));
    }

    public String displayTradeQuantity()
    {
        return "" + Math.abs(quantity);
    }

    public boolean isBuy()
    {
        return quantity > 0 ? true : false;
    }

    public double getUnitPriceCurrency() {
        double d1 = unitPriceRefCcy;
        return Double.valueOf(DataUtils.keepTwoDecimal(d1));
    }
}