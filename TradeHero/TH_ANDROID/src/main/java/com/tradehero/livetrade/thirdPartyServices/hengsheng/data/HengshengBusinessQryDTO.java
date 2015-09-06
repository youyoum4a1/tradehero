package com.tradehero.livetrade.thirdPartyServices.hengsheng.data;

import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.subData.HengshengBusinessQryData;

import java.util.List;

/**
 * <pre>
 * {
 "data": [
         {
             "entrust_bs": "1",
             "real_status": "0",
             "exchange_type": "1",
             "stock_code": "600839",
             "business_amount": "100.00",
             "position_str": "20150826021300011050002200000106",
             "business_no": "45",
             "business_time": "130001",
             "business_balance": "752.00",
             "entrust_no": "109",
             "real_type": "0",
             "stock_account": "A070000399",
             "stock_name": "四川长虹",
             "business_price": "7.520"
         }
     ]
 }
 * </pre>
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class HengshengBusinessQryDTO extends HengshengBaseDTO {
    public List<HengshengBusinessQryData> data;
}

