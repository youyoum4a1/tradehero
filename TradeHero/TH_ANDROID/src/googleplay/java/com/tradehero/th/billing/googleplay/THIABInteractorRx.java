package com.androidth.general.billing.googleplay;

import com.androidth.general.common.billing.googleplay.IABInteractorRx;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUList;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.billing.THBillingInteractorRx;

public interface THIABInteractorRx
        extends
        IABInteractorRx<
                IABSKUListKey,
                IABSKU,
                IABSKUList,
                THIABProductDetail,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                THIABLogicHolderRx>,
        THBillingInteractorRx<
                IABSKUListKey,
                IABSKU,
                IABSKUList,
                THIABProductDetail,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                THIABLogicHolderRx>
{
}
