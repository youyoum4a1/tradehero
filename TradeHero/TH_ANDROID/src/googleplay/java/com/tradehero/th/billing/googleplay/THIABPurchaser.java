package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaser;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THPurchaser;

public interface THIABPurchaser
        extends
        IABPurchaser<
                IABSKU,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                IABException>,
        THPurchaser<
                IABSKU,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                IABException>
{
}
