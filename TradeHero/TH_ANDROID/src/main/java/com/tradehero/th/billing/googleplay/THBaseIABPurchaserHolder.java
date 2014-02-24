package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.googleplay.BaseIABPurchaserHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/**
 * Created by xavier on 2/24/14.
 */
public class THBaseIABPurchaserHolder
    extends BaseIABPurchaserHolder<
        IABSKU,
        THIABProductDetail,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase,
        THIABPurchaser,
        BillingPurchaser.OnPurchaseFinishedListener<
            IABSKU,
            THIABPurchaseOrder,
            THIABOrderId,
            THIABPurchase,
            IABException>,
        IABException>
    implements THIABPurchaserHolder
{
    public THBaseIABPurchaserHolder()
    {
        super();
    }

    @Override protected THIABPurchaser createPurchaser()
    {
        return new THIABPurchaser();
    }
}
