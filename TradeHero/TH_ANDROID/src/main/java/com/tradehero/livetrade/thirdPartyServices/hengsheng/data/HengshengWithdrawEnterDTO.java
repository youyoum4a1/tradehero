package com.tradehero.livetrade.thirdPartyServices.hengsheng.data;

import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.subData.HengshengWithdrawEnterData;

import java.util.List;

/**
 * Created by Sam on 15/8/26.
 *
 *
 * For reference: http://open.hs.net/wiki/doc/services/secu/func_secu_entrust_withdraw.html
 *
 * Example response
 * {
 "data": [
     {
        "entrust_no":"83"
     }
 ]
 }
 */
public class HengshengWithdrawEnterDTO extends HengshengBaseDTO{
    public List<HengshengWithdrawEnterData> data;
}

