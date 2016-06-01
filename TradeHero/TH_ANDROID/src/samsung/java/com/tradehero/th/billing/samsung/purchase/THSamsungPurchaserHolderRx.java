package com.ayondo.academy.billing.samsung.purchase;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchase.SamsungPurchaserHolderRx;
import com.ayondo.academy.billing.purchase.THPurchaserHolderRx;
import com.ayondo.academy.billing.samsung.THSamsungOrderId;
import com.ayondo.academy.billing.samsung.THSamsungPurchase;
import com.ayondo.academy.billing.samsung.THSamsungPurchaseOrder;

public interface THSamsungPurchaserHolderRx
        extends
        SamsungPurchaserHolderRx<
                SamsungSKU,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase>,
        THPurchaserHolderRx<
                SamsungSKU,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase>
{
}
