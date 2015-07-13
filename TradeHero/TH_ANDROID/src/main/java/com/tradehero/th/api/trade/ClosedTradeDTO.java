package com.tradehero.th.api.trade;

import com.tradehero.common.persistence.DTO;

public class ClosedTradeDTO implements DTO
{
    public int id;
    public int portfolioId;
    public String securityId;
    public String exchange;
    public String symbol;
    public String securityName;
    public int quantity;
    public int price;
    public String currencyDisplay;
    public int state;// 0: 未成交,  1: 已成交,  2: 已撤
    public String closedAtUtc;

    public ClosedTradeDTO()
    {
    }

    @Override
    public String toString() {
        return "ClosedTradeDTO{" +
                "id=" + id +
                ", portfolioId=" + portfolioId +
                ", securityId='" + securityId + '\'' +
                ", exchange='" + exchange + '\'' +
                ", symbol='" + symbol + '\'' +
                ", securityName='" + securityName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", currencyDisplay='" + currencyDisplay + '\'' +
                ", state=" + state +
                ", closedAtUtc=" + closedAtUtc +
                '}';
    }
}