package com.androidth.general.billing.samsung.purchase;

import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.purchase.SamsungPurchaserHolderRx;
import com.androidth.general.billing.purchase.THPurchaserHolderRx;
import com.androidth.general.billing.samsung.THSamsungOrderId;
import com.androidth.general.billing.samsung.THSamsungPurchase;
import com.androidth.general.billing.samsung.THSamsungPurchaseOrder;

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
