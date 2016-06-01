package com.ayondo.academy.billing.googleplay.purchase;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.purchase.IABPurchaserRx;
import com.ayondo.academy.billing.googleplay.THIABOrderId;
import com.ayondo.academy.billing.googleplay.THIABPurchase;
import com.ayondo.academy.billing.googleplay.THIABPurchaseOrder;
import com.ayondo.academy.billing.purchase.THPurchaserRx;

public interface THIABPurchaserRx
        extends
        IABPurchaserRx<
                IABSKU,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase>,
        THPurchaserRx<
                IABSKU,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase>
{
}
