package com.tradehero.th.billing.samsung.purchase;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchase.SamsungPurchaserHolderRx;
import com.tradehero.th.billing.purchase.THPurchaserHolderRx;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import com.tradehero.th.billing.samsung.THSamsungPurchaseOrder;

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
