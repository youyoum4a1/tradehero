package com.tradehero.livetrade.thirdPartyServices.hengsheng.data;

import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.subData.HengshengEntrustEnterData;

import java.util.List;

/**
 * Created by Sam on 15/8/25.
 *
 * For reference: http://open.hs.net/wiki/doc/services/secu/func_secu_entrust.html
 *
 * Example response
 * {
 "data": [
     {
        "entrust_no":"83"
     }
 ]
 }
 *
 */
public class HengShengEntrustEnterDTO extends HengshengBaseDTO{
    public List<HengshengEntrustEnterData> data;
}

