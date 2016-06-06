package com.androidth.general.billing.samsung;

import com.androidth.general.common.billing.samsung.SamsungInteractorRx;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.SamsungSKUList;
import com.androidth.general.common.billing.samsung.SamsungSKUListKey;
import com.androidth.general.billing.THBillingInteractorRx;

public interface THSamsungInteractorRx
        extends
        SamsungInteractorRx<
                SamsungSKUListKey,
                SamsungSKU,
                SamsungSKUList,
                THSamsungProductDetail,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase,
                THSamsungLogicHolderRx>,
        THBillingInteractorRx<
                SamsungSKUListKey,
                SamsungSKU,
                SamsungSKUList,
                THSamsungProductDetail,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase,
                THSamsungLogicHolderRx>
{
}
