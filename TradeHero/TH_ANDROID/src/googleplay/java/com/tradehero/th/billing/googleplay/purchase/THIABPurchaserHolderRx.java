package com.ayondo.academy.billing.googleplay.purchase;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.purchase.IABPurchaserHolderRx;
import com.ayondo.academy.billing.googleplay.THIABOrderId;
import com.ayondo.academy.billing.googleplay.THIABPurchase;
import com.ayondo.academy.billing.googleplay.THIABPurchaseOrder;
import com.ayondo.academy.billing.purchase.THPurchaserHolderRx;

public interface THIABPurchaserHolderRx
        extends
        IABPurchaserHolderRx<
                IABSKU,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase>,
        THPurchaserHolderRx<
                IABSKU,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase>
{
}
