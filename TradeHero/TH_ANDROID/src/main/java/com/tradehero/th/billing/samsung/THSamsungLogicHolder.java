package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungLogicHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THBillingLogicHolder;
import com.tradehero.th.billing.samsung.request.THSamsungRequestFull;

/**
 * Created by xavier on 3/27/14.
 */
public interface THSamsungLogicHolder
    extends SamsungLogicHolder<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        THSamsungProductDetail,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase,
        THSamsungRequestFull,
        SamsungException>,
        THBillingLogicHolder<
                SamsungSKUListKey,
                SamsungSKU,
                SamsungSKUList,
                THSamsungProductDetail,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase,
                THSamsungRequestFull,
                SamsungException>,
        THSamsungProductDetailDomainInformer,
        THSamsungProductIdentifierFetcherHolder,
        THSamsungInventoryFetcherHolder,
        THSamsungPurchaseFetcherHolder,
        THSamsungPurchaserHolder,
        THSamsungPurchaseReporterHolder

{
}
