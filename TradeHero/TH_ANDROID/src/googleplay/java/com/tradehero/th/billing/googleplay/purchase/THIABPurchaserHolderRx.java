package com.androidth.general.billing.googleplay.purchase;

import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.purchase.IABPurchaserHolderRx;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABPurchase;
import com.androidth.general.billing.googleplay.THIABPurchaseOrder;
import com.androidth.general.billing.purchase.THPurchaserHolderRx;

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
