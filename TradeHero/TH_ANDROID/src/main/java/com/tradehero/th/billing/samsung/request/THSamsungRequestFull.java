package com.tradehero.th.billing.samsung.request;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import com.tradehero.th.billing.samsung.THSamsungPurchaseOrder;

/**
 * Created by xavier on 3/27/14.
 */
public class THSamsungRequestFull
    extends THSamsungRequest<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        THSamsungProductDetail,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
{
    protected THSamsungRequestFull()
    {
        super();
    }
}
