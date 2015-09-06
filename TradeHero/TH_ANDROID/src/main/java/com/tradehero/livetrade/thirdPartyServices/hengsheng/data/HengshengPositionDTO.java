package com.tradehero.livetrade.thirdPartyServices.hengsheng.data;

import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.subData.HengshengPositionData;

import java.util.List;

/**
 * <pre>
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
 * </pre>
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 *
 */
public class HengshengPositionDTO extends HengshengBaseDTO{
    public List<HengshengPositionData> data;
}

