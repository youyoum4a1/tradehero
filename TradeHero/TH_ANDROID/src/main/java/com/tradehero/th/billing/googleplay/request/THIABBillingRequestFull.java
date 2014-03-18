package com.tradehero.th.billing.googleplay.request;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;

/**
 * Created by xavier on 3/13/14.
 */
public class THIABBillingRequestFull extends THIABBillingRequest<
        IABSKU, THIABProductDetail,
        THIABPurchaseOrder, THIABOrderId,
        THIABPurchase, IABException>
{
    public THIABBillingRequestFull()
    {
        super();
    }
}
