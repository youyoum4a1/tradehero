package com.androidth.general.billing.googleplay.purchase;

import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.purchase.IABPurchaserRx;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABPurchase;
import com.androidth.general.billing.googleplay.THIABPurchaseOrder;
import com.androidth.general.billing.purchase.THPurchaserRx;

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
