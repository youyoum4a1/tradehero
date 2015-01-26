package com.tradehero.th.billing.googleplay.purchase;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.purchase.IABPurchaserHolderRx;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;
import com.tradehero.th.billing.purchase.THPurchaserHolderRx;

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
