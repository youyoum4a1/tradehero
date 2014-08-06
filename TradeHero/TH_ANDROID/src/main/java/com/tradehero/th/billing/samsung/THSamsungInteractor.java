package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungInteractor;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.samsung.request.THSamsungRequestFull;
import com.tradehero.th.billing.samsung.request.THUISamsungRequest;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THSamsungInteractor
        extends
        SamsungInteractor<
                SamsungSKUListKey,
                SamsungSKU,
                SamsungSKUList,
                THSamsungProductDetail,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase,
                THSamsungLogicHolder,
                THSamsungRequestFull,
                THUISamsungRequest,
                SamsungException>,
        THBillingInteractor<
                SamsungSKUListKey,
                SamsungSKU,
                SamsungSKUList,
                THSamsungProductDetail,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase,
                THSamsungLogicHolder,
                THSamsungRequestFull,
                THUISamsungRequest,
                SamsungException>
{
}
