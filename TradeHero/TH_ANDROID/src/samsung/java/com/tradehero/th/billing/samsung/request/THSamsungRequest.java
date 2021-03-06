package com.tradehero.th.billing.samsung.request;

import com.tradehero.common.billing.samsung.BaseSamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungOrderId;
import com.tradehero.common.billing.samsung.SamsungProductDetail;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungPurchaseOrder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THProductPurchase;
import com.tradehero.th.billing.THPurchaseOrder;
import com.tradehero.th.billing.request.THBillingRequest;
import android.support.annotation.NonNull;

public class THSamsungRequest<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>,
        SamsungProductDetailType extends SamsungProductDetail<SamsungSKUType>,
        THSamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>
                & THPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        THSamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>
                & THProductPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungExceptionType extends SamsungException>
        extends THBillingRequest<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType,
        SamsungProductDetailType,
        THSamsungPurchaseOrderType,
        SamsungOrderIdType,
        THSamsungPurchaseType,
        SamsungExceptionType>
{
    //<editor-fold desc="Constructors">
    protected THSamsungRequest(@NonNull Builder<
            SamsungSKUListKeyType,
            SamsungSKUType,
            SamsungSKUListType,
            SamsungProductDetailType,
            THSamsungPurchaseOrderType,
            SamsungOrderIdType,
            THSamsungPurchaseType,
            SamsungExceptionType,
            ?> builder)
    {
        super(builder);
    }
    //</editor-fold>
}
