package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungPurchaser;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THPurchaser;

/**
 * Created by xavier on 3/27/14.
 */
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
