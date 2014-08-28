package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungPurchaser;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THPurchaser;

public interface THSamsungPurchaser
        extends
        SamsungPurchaser<
                SamsungSKU,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase,
                SamsungException>,
        THPurchaser<
                SamsungSKU,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase,
                SamsungException>
{
}
