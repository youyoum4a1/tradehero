package com.androidth.general.billing.samsung.purchase;

import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.purchase.SamsungPurchaserRx;
import com.androidth.general.billing.purchase.THPurchaserRx;
import com.androidth.general.billing.samsung.THSamsungOrderId;
import com.androidth.general.billing.samsung.THSamsungPurchase;
import com.androidth.general.billing.samsung.THSamsungPurchaseOrder;

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
