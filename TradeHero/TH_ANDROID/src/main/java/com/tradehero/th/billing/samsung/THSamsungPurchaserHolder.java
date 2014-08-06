package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungPurchaserHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THPurchaserHolder;

/**
 * Created by xavier on 3/27/14.
 */
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
