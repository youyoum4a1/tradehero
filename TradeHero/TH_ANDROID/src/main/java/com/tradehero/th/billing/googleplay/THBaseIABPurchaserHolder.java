package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaserHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

public class THBaseIABPurchaserHolder
    extends BaseIABPurchaserHolder<
        IABSKU,
        THIABProductDetail,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase,
        THIABPurchaser,
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
