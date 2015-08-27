package com.tradehero.livetrade.hengsheng.data;

import java.util.List;

/**
 * Created by Sam on 15/8/25.
 * For reference: http://open.hs.net/wiki/doc/services/secu/func_secu_stock_qry.html
 * {
     "data": [
         {
             "cost_price": "14.625",
             "money_type": "0",
             "income_balance": "-1285.38",
             "last_price": "10.340",
             "current_amount": "300",
             "exchange_type": "1",
             "enable_amount": "300",
             "stock_code": "600820",
             "stock_account": "A070000399",
             "stock_name": "隧道股份",
             "position_str": "0002200000000007000039900010000000000A070000399600820",
             "market_value": "3102.00"
         }
     ]
 }
 *
 */
public class HengshengPositionDTO extends HengshengBaseDTO{
    public List<PositionDTO> data;
}

class PositionDTO {
    public float cost_price;
    public int money_type;
    public float income_balance;
    public float last_price;
    public int current_amount;
    public int exchange_type;
    public int enable_amount;
    public String stock_code;
    public String stock_account;
    public String stock_name;
    public String position_str;
    public float market_value;
}
