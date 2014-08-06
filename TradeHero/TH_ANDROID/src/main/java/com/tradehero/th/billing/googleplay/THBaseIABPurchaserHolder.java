package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaserHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

class THBaseIABPurchaserHolder
    extends BaseIABPurchaserHolder<
        IABSKU,
        THIABProductDetail,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase,
        THBaseIABPurchaser,
        IABException>
    implements THIABPurchaserHolder
{
    public THBaseIABPurchaserHolder()
    {
        super();
    }

    @Override protected THBaseIABPurchaser createPurchaser()
    {
        return new THBaseIABPurchaser();
    }
}
