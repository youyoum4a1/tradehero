package com.ayondo.academy.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungLogicHolderRx;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.ayondo.academy.billing.THBillingLogicHolderRx;

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
