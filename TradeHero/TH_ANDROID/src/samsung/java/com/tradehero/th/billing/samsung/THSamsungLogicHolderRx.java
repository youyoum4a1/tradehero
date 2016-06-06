package com.androidth.general.billing.samsung;

import com.androidth.general.common.billing.samsung.SamsungLogicHolderRx;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.SamsungSKUList;
import com.androidth.general.common.billing.samsung.SamsungSKUListKey;
import com.androidth.general.billing.THBillingLogicHolderRx;

public interface THSamsungLogicHolderRx
        extends SamsungLogicHolderRx<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        THSamsungProductDetail,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase>,
        THBillingLogicHolderRx<
                SamsungSKUListKey,
                SamsungSKU,
                SamsungSKUList,
                THSamsungProductDetail,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase>,
        THSamsungProductDetailDomainInformerRx
{
}
