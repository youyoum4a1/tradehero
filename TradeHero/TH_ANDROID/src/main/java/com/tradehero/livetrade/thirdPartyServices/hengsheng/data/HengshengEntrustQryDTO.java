package com.tradehero.livetrade.thirdPartyServices.hengsheng.data;

import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.subData.HengshengEntrustQryData;

import java.util.List;

/**
 * Created by Sam on 15/8/26.
 *
 * For Reference: http://open.hs.net/wiki/doc/services/secu/func_secu_entrust_qry.html
 * Example response
 * {
 "data": [
         {
             "entrust_date": "20150826",
             "entrust_bs": "1",
             "exchange_type": "1",
             "entrust_type": "0",
             "stock_code": "603729",
             "entrust_prop": "0",
             "business_amount": "0",
             "position_str": "20150826021102058490002200000083",
             "entrust_status": "2",
             "entrust_amount": "100.00",
             "entrust_price": "65.830",
             "entrust_no": "83",
             "withdraw_flag": "1",
             "report_time": "110205",
             "stock_account": "A070000399",
             "report_no": "83",
             "stock_name": "龙韵股份",
             "entrust_time": "110205",
             "business_price": "0.000"
         }
     ]
 }
 */
public class HengshengEntrustQryDTO extends HengshengBaseDTO{
    public List<HengshengEntrustQryData> data;
}

