package com.tradehero.livetrade.thirdPartyServices.hengsheng.data;

import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.subData.HengshengEntrustEnterData;

import java.util.List;

/**
 *
 * <pre>
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
 * </pre>
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class HengShengEntrustEnterDTO extends HengshengBaseDTO{
    public List<HengshengEntrustEnterData> data;
}

