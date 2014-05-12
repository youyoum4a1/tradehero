package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABLogicHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THBillingLogicHolder;
import com.tradehero.th.billing.googleplay.request.THIABBillingRequestFull;

public interface THIABLogicHolder extends
        IABLogicHolder<
                IABSKUListKey,
                IABSKU,
                IABSKUList,
                THIABProductDetail,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                THIABBillingRequestFull,
                IABException>,
        THBillingLogicHolder<
                IABSKUListKey,
                IABSKU,
                IABSKUList,
                THIABProductDetail,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                THIABBillingRequestFull,
                IABException>,
        THIABProductDetailDomainInformer,
        THIABProductIdentifierFetcherHolder,
        THIABInventoryFetcherHolder,
        THIABPurchaseFetcherHolder,
        THIABPurchaserHolder,
        THIABPurchaseReporterHolder,
        THIABPurchaseConsumerHolder
{
    void unregisterPurchaseConsumptionListener(int requestCode);
}
