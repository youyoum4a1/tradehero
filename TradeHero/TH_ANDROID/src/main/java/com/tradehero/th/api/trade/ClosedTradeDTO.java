package com.tradehero.th.api.trade;

import com.tradehero.common.persistence.DTO;
import java.util.Date;

public class ClosedTradeDTO implements DTO
{
    public int id;
    public int portfolioId;
    public String securityId;
    public String exchange;
    public String symbol;
    public String securityName;
    public int quantity;
    public double price;
    public String currencyDisplay;
    public int state;// 0: 未成交,  1: 已成交,  2: 已撤
    public Date closedAtUtc;
    public Date createdAtUtc;

    public String business_price;
    public String business_amt;
    public String business_date;
    public String business_time;

    public String entrust_name;
    public String entrust_price;
    public String entrust_amt;
    public String entrust_date;
    public String entrust_time;
    public String entrust_status_name;

    public String market_code;
    public String sec_account;
    public String withdraw_cate;
    public String entrust_no;

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
//                ", closedAtUtc='" + closedAtUtc.toString() + '\'' +
//                ", createdAtUtc='" + createdAtUtc.toString() + '\'' +
                ", business_price='" + business_price + '\'' +
                ", business_amt='" + business_amt + '\'' +
                ", business_date='" + business_date + '\'' +
                ", business_time='" + business_time + '\'' +
                ", entrust_name='" + entrust_name + '\'' +
                ", entrust_price='" + entrust_price + '\'' +
                ", entrust_amt='" + entrust_amt + '\'' +
                ", entrust_date='" + entrust_date + '\'' +
                ", entrust_time='" + entrust_time + '\'' +
                ", entrust_status_name='" + entrust_status_name + '\'' +
                ", market_code='" + market_code + '\'' +
                ", sec_account='" + sec_account + '\'' +
                ", withdraw_cate='" + withdraw_cate + '\'' +
                '}';
    }
}