package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABLogicHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THBillingLogicHolder;
import com.tradehero.th.billing.googleplay.request.THIABBillingRequestFull;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
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
