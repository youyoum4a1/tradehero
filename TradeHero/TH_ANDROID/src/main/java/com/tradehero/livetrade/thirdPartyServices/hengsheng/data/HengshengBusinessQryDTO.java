package com.tradehero.livetrade.thirdPartyServices.hengsheng.data;

import java.util.List;

/**
 * Created by Sam on 15/8/26.
 *
 *
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
 */
public class HengshengBusinessQryDTO extends HengshengBaseDTO {
    public List<BusinessQryDTO> data;
}

class BusinessQryDTO {
    public int entrust_bs;
    public int real_status;
    public String exchange_type;
    public String stock_code;
    public float business_amount;
    public String position_str;
    public int business_no;
    public int business_time;
    public float business_balance;
    public int real_type;
    public String stock_account;
    public String stock_name;
    public float business_price;
}
