package com.tradehero.th.billing.googleplay.request;

import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.request.UIIABBillingRequest;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;
import com.tradehero.th.billing.request.THUIBillingRequest;
import javax.inject.Inject;

/**
 * Created by xavier on 3/13/14.
 */
public class THUIIABBillingRequest
    extends THUIBillingRequest<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        THIABProductDetail,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase,
        IABException>
    implements
        UIIABBillingRequest
{
    /**
     * Indicates whether we want the Interactor to pop a dialog when the consume has failed.
     */
    public boolean popIfConsumeFailed;
    public IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKU,
            THIABOrderId,
            THIABPurchase,
            IABException> consumptionFinishedListener;

    @Inject public THUIIABBillingRequest()
    {
        super();
    }
}
