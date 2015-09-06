package com.tradehero.livetrade.thirdPartyServices.hengsheng.data;

import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.subData.HengshengBalanceData;

import java.util.List;

/**
 * <pre>
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

 </pre>

 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class HengshengBalanceDTO extends HengshengBaseDTO{
    public List<HengshengBalanceData> data;
}



