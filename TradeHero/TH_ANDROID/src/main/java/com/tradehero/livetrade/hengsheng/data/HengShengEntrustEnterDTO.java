package com.tradehero.livetrade.hengsheng.data;

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
    public List<EntrustEnterDTO> data;
}

class EntrustEnterDTO {
    public int entrust_no;
}
