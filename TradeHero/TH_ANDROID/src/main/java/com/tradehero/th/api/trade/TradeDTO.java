package com.tradehero.th.api.trade;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.portfolio.PortfolioId;

import java.util.Date;

/** Created with IntelliJ IDEA. User: tho Date: 9/3/13 Time: 12:53 PM Copyright (c) TradeHero */
public class TradeDTO implements DTO
{
    public int id;
    public double unit_price;
    public double transaction_cost;
    public int quantity;
    public Date date_time;
    public double exchange_rate;
    public int quantity_after_trade;
    public double average_price_after_trade;
    public double realized_pl_after_trade;

    public TradeDTO() { }


    public TradeId getTradeId()
    {
        return new TradeId(id);
    }
}