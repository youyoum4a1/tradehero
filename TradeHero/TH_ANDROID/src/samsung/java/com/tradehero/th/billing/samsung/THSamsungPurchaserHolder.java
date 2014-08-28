package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungPurchaserHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THPurchaserHolder;

public interface THSamsungPurchaserHolder
        extends
        SamsungPurchaserHolder<
                SamsungSKU,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase,
                SamsungException>,
        THPurchaserHolder<
                SamsungSKU,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase,
                SamsungException>
{
}
