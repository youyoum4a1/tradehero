package com.tradehero.th.billing.samsung.request;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.billing.samsung.request.UISamsungRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import com.tradehero.th.billing.samsung.THSamsungPurchaseOrder;
import javax.inject.Inject;

/**
 * Created by xavier on 3/13/14.
 */
public class THUISamsungRequest
    extends THUIBillingRequest<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        THSamsungProductDetail,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
    implements
        UISamsungRequest
{
    @Inject public THUISamsungRequest()
    {
        super();
    }
}
