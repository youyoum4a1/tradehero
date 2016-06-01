package com.ayondo.academy.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABInteractorRx;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.ayondo.academy.billing.THBillingInteractorRx;

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
