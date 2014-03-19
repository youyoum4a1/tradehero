package com.tradehero.th.billing.googleplay.request;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABProductDetail;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseOrder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.request.THBillingRequest;

/**
 * Created by xavier on 3/13/14.
 */
public class THIABBillingRequest<
        IABSKUType extends IABSKU,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABExceptionType extends IABException>
        extends
        THBillingRequest<
                IABSKUType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>
{
    public boolean consumePurchase;
    public IABPurchaseType purchaseToConsume;
    public IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABExceptionType> consumptionFinishedListener;

    protected THIABBillingRequest()
    {
        super();
    }

    @Override public void onDestroy()
    {
        this.consumptionFinishedListener = null;
        super.onDestroy();
    }
}
