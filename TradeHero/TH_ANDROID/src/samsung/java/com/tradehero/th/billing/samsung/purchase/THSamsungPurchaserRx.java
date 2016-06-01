package com.ayondo.academy.billing.samsung.purchase;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchase.SamsungPurchaserRx;
import com.ayondo.academy.billing.purchase.THPurchaserRx;
import com.ayondo.academy.billing.samsung.THSamsungOrderId;
import com.ayondo.academy.billing.samsung.THSamsungPurchase;
import com.ayondo.academy.billing.samsung.THSamsungPurchaseOrder;

public interface THSamsungPurchaserRx
        extends
        SamsungPurchaserRx<
                SamsungSKU,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase>,
        THPurchaserRx<
                SamsungSKU,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase>
{
}
