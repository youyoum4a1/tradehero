package com.tradehero.livetrade.thirdPartyServices.hengsheng.data;

import java.util.List;

/**
 * Created by trdehero on 15/8/25.
 * For reference: http://open.hs.net/wiki/doc/services/secu/func_client_exact_fund_all_qry.html
 * Example data:
 * {
     "data": [
         {
             "frozen_balance": "0",
             "asset_balance": "998654.62",
             "unfrozen_balance": "0",
             "enable_balance": "995552.62",
             "fetch_balance": "995552.62",
             "money_type": "0",
             "current_balance": "995552.62",
             "fund_balance": "995552.62",
             "market_value": "3102.00"
         }
     ]
 }

 {"error_no":"-201","error_code":"10204","error_info":"可用股票数量不足"}
 */
public class HengshengBalanceDTO extends HengshengBaseDTO{
    public List<BalanceDTO> data;
}

class BalanceDTO {
    public float frozen_balance;
    public float asset_balance;
    public float unfrozen_balance;
    public float enable_balance;
    public float fetch_balance;
    public int money_type;
    public float current_balance;
    public float fund_balance;
    public float market_value;
}



