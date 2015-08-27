package com.tradehero.livetrade.hengsheng.data;

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
    public List<EntrustQry> data;
}

class EntrustQry {
    public String entrust_date;
    public int entrust_bs;
    public int exchange_type;
    public int entrust_type;
    public String stock_code;
    public int entrust_prop;
    public float business_amount;
    public String position_str;
    public int entrust_status;
    public float entrust_amount;
    public float entrust_price;
    public int entrust_no;
    public int withdraw_flag;
    public String report_time;
    public String stock_account;
    public int report_no;
    public String stock_name;
    public String entrust_time;
    public float business_price;
}
